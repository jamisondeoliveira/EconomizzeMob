package com.example.economizzemob

import android.content.Context
import android.util.Log
import com.example.economizzemob.dataconnect.EconomizzeConnector
import com.example.economizzemob.dataconnect.execute
import com.example.economizzemob.dataconnect.instance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException

// Guarda as movimentações no SharedPreferences (funciona offline) e sincroniza com o Firebase SQL Connect
object MovimentacaoRepository {
    private const val TAG = "MovimentacaoRepo"
    private const val PREFS = "economizze"
    private const val CHAVE_MOVS = "movs"
    private const val CHAVE_EXCLUSOES = "exclusoes_pendentes"
    private val gson = Gson()
    private val connector get() = EconomizzeConnector.instance

    fun carregarLocal(context: Context): MutableList<Movimentacao> {
        val json = prefs(context).getString(CHAVE_MOVS, "[]")
        val type = object : TypeToken<List<Movimentacao>>() {}.type
        val lista: List<Movimentacao> = gson.fromJson(json, type) ?: emptyList()
        // registros salvos antes do Firebase não têm id (o Gson deixa null mesmo sendo String)
        @Suppress("USELESS_CAST", "SENSELESS_COMPARISON")
        return lista.map { if ((it.id as String?) == null) it.copy(id = UUID.randomUUID().toString(), sincronizado = false) else it }.toMutableList()
    }

    fun salvarLocal(context: Context, lista: List<Movimentacao>) {
        prefs(context).edit().putString(CHAVE_MOVS, gson.toJson(lista)).apply()
    }

    // Retorna true se o Firebase recebeu; se falhar, o item fica com sincronizado=false e é reenviado no próximo sincronizar()
    suspend fun enviar(mov: Movimentacao): Boolean = tentar("enviar ${mov.id}") {
        connector.salvarMovimentacao.execute(id = UUID.fromString(mov.id), descricao = mov.descricao, valor = mov.valor, data = mov.data, ehSaida = mov.ehSaida)
    }

    suspend fun excluir(context: Context, id: String) {
        val ok = tentar("excluir $id") { connector.excluirMovimentacao.execute(id = UUID.fromString(id)) }
        if (!ok) salvarExclusoesPendentes(context, exclusoesPendentes(context) + id)
    }

    // Envia o que ficou pendente e baixa a lista do Firebase. Retorna null se estiver sem conexão.
    suspend fun sincronizar(context: Context): MutableList<Movimentacao>? {
        val exclusoes = exclusoesPendentes(context)
        val restantes = exclusoes.filterNot { id -> tentar("excluir $id") { connector.excluirMovimentacao.execute(id = UUID.fromString(id)) } }.toSet()
        salvarExclusoesPendentes(context, restantes)

        val locais = carregarLocal(context)
        val naoEnviados = locais.filter { !it.sincronizado && !enviar(it) }.map { it.id }.toSet()

        val remotas = try {
            connector.listarMovimentacoes.execute().data.movimentacoes
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Falha ao listar movimentações", e)
            return null
        }

        val lista = remotas.filterNot { it.id.toString() in restantes }
            .map { Movimentacao(it.descricao, it.valor, it.data, it.ehSaida, it.id.toString(), sincronizado = true) }
            .toMutableList()
        // mantém o que ainda não conseguiu subir para não perder nada
        lista.addAll(locais.filter { it.id in naoEnviados && lista.none { r -> r.id == it.id } })
        salvarLocal(context, lista)
        return lista
    }

    private suspend fun tentar(acao: String, bloco: suspend () -> Unit): Boolean = try {
        bloco()
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.w(TAG, "Falha ao $acao", e)
        false
    }

    private fun exclusoesPendentes(context: Context): Set<String> =
        prefs(context).getStringSet(CHAVE_EXCLUSOES, emptySet()) ?: emptySet()

    private fun salvarExclusoesPendentes(context: Context, ids: Set<String>) {
        prefs(context).edit().putStringSet(CHAVE_EXCLUSOES, ids).apply()
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}

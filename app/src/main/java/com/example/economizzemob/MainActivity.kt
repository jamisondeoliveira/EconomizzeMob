package com.example.economizzemob

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

data class Movimentacao(val descricao: String, val valor: Double, val data: String, val ehSaida: Boolean, val id: String = UUID.randomUUID().toString(), val sincronizado: Boolean = false): java.io.Serializable

class MainActivity : AppCompatActivity() {

    private var saldo = 0.0
    private var movimentacoes = mutableListOf<Movimentacao>()
    private lateinit var valorTotal: TextView
    private lateinit var listaEntrada: LinearLayout
    private lateinit var listaSaida: LinearLayout
    private lateinit var textoAlertaSaldo: TextView
    private lateinit var drawerLayout: DrawerLayout
    private val CHANNEL_ID = "economizze_alerta"
    private val localeBR = Locale.forLanguageTag("pt-BR")
//falta certificado sha
    private val webclient_id="916906114013:android:fd85dee4d021cdb02b1434"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        valorTotal = findViewById(R.id.valorTotal)
        listaEntrada = findViewById(R.id.listaEntrada)
        listaSaida = findViewById(R.id.listaSaida)
        textoAlertaSaldo = findViewById(R.id.textoAlertaSaldo)
        drawerLayout = findViewById(R.id.drawerLayout)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.navView)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> drawerLayout.closeDrawer(GravityCompat.START)
                R.id.nav_historico -> {
                    startActivity(Intent(this, HistoricoActivity::class.java))
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_sair -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            true
        }

        criarCanalNotificacao()
        pedirPermissaoNotificacao()
        carregarHistorico()

        findViewById<Button>(R.id.btnEntrada).setOnClickListener { abrirDialogoMovimentacao("Adicionar Entrada", "Digite o valor", false) }
        findViewById<Button>(R.id.btnSaida).setOnClickListener { abrirDialogoMovimentacao("Adicionar Saída", "Digite a despesa", true) }
    }

    private fun criarCanalNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CHANNEL_ID, "Alertas de Saldo", NotificationManager.IMPORTANCE_HIGH).apply { description = "Avisa quando saldo fica negativo" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }

    private fun pedirPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun mostrarNotificacaoSaldoNegativo() {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        vibrar()
        val notificacao = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("ECONOMIZZE - Saldo Negativo!")
            .setContentText(String.format(localeBR, "Seu saldo está em R$ %.2f. Cuidado!", saldo))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setAutoCancel(true)
            .build()
        try { NotificationManagerCompat.from(this).notify(1, notificacao) } catch (_: Exception) {}
    }

    @SuppressLint("MissingPermission")
    private fun vibrar() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(400, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(400)
        }
    }

    private fun abrirDialogoMovimentacao(titulo: String, textoValor: String, ehSaida: Boolean) {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(30, 20, 30, 20) }
        val campoDescricao = EditText(this).apply { hint = if (ehSaida) "Ex: Mercado" else "Ex: Salário"; inputType = InputType.TYPE_CLASS_TEXT }
        val campoValor = EditText(this).apply { hint = textoValor; inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val campoData = EditText(this).apply { isFocusable = false; isClickable = true; hint = "Data" }
        val calendario = Calendar.getInstance()
        val formatoData = SimpleDateFormat("dd/MM/yyyy", localeBR)
        campoData.setText(formatoData.format(calendario.time))
        campoData.setOnClickListener { DatePickerDialog(this, { _, ano, mes, dia -> val d = Calendar.getInstance().apply { set(ano, mes, dia) }; campoData.setText(formatoData.format(d.time)) }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show() }
        layout.addView(campoDescricao); layout.addView(campoValor); layout.addView(campoData)
        AlertDialog.Builder(this).setTitle(titulo).setView(layout).setNegativeButton("CANCELAR", null)
            .setPositiveButton("ADICIONAR") { _, _ ->
                val descricao = campoDescricao.text.toString().trim()
                val valor = campoValor.text.toString().toDoubleOrNull()
                val data = campoData.text.toString().trim()
                if (descricao.isNotEmpty() && valor != null && valor > 0 && data.isNotEmpty()) {
                    val mov = Movimentacao(descricao, valor, data, ehSaida)
                    movimentacoes.add(mov)
                    renderizar()
                    salvarHistorico(mov)
                }
            }.create().show()
    }

    private fun atualizarSaldo() {
        valorTotal.text = String.format(localeBR, "R$ %.2f", saldo)
        if (saldo < 0) {
            valorTotal.setTextColor(android.graphics.Color.parseColor("#FF3B30"))
            textoAlertaSaldo.visibility = View.VISIBLE
            mostrarNotificacaoSaldoNegativo()
        } else {
            valorTotal.setTextColor(android.graphics.Color.parseColor("#0C214C"))
            textoAlertaSaldo.visibility = View.GONE
        }
    }

    // Salva no aparelho na hora e envia para o Firebase em segundo plano
    private fun salvarHistorico(alterada: Movimentacao) {
        MovimentacaoRepository.salvarLocal(this, movimentacoes)
        lifecycleScope.launch {
            if (MovimentacaoRepository.enviar(alterada)) {
                val i = movimentacoes.indexOfFirst { it.id == alterada.id }
                if (i >= 0 && movimentacoes[i] == alterada) {
                    movimentacoes[i] = alterada.copy(sincronizado = true)
                    MovimentacaoRepository.salvarLocal(this@MainActivity, movimentacoes)
                }
            }
        }
    }

    private fun excluirMovimentacao(mov: Movimentacao) {
        movimentacoes.removeAll { it.id == mov.id }
        renderizar()
        MovimentacaoRepository.salvarLocal(this, movimentacoes)
        lifecycleScope.launch { MovimentacaoRepository.excluir(this@MainActivity, mov.id) }
    }

    // Mostra o que está no aparelho e depois atualiza com o Firebase
    private fun carregarHistorico() {
        movimentacoes = MovimentacaoRepository.carregarLocal(this)
        renderizar()
        lifecycleScope.launch {
            MovimentacaoRepository.sincronizar(this@MainActivity)?.let {
                movimentacoes = it
                renderizar()
            }
        }
    }

    private fun renderizar() {
        listaEntrada.removeAllViews()
        listaSaida.removeAllViews()
        movimentacoes.forEach { adicionarValor(if (it.ehSaida) listaSaida else listaEntrada, it) }
        if (listaEntrada.childCount == 0) adicionarMensagemVazio(listaEntrada, false)
        if (listaSaida.childCount == 0) adicionarMensagemVazio(listaSaida, true)
        saldo = movimentacoes.sumOf { if (it.ehSaida) -it.valor else it.valor }
        atualizarSaldo()
    }

    private fun adicionarValor(lista: LinearLayout, mov: Movimentacao) {
        val (descricao, valor, data, ehSaida) = mov
        val item = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_VERTICAL; setPadding(20, 15, 20, 15) }
        val fundo = android.graphics.drawable.GradientDrawable().apply { setColor(android.graphics.Color.WHITE); cornerRadius = 20f }
        item.background = fundo
        item.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 12 }
        val textoDescricao = TextView(this).apply { text = descricao; textSize = 17f; setTextColor(android.graphics.Color.parseColor("#172033")); setTypeface(null, android.graphics.Typeface.BOLD) }
        val tipo = TextView(this).apply { text = if (ehSaida) "Saída" else "Entrada"; textSize = 13f; setTextColor(android.graphics.Color.parseColor("#737B8C")) }
        val valorTexto = TextView(this).apply { textSize = 20f; setTextColor(android.graphics.Color.parseColor("#0C214C")); setTypeface(null, android.graphics.Typeface.BOLD) }
        atualizarTextoValor(valorTexto, valor, ehSaida)
        val textoData = TextView(this).apply { text = "📅 $data"; textSize = 13f; setTextColor(android.graphics.Color.parseColor("#737B8C")) }
        val botaoEditar = Button(this).apply { text = "EDITAR"; textSize = 11f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 90).apply { topMargin = 10 }; setBackgroundColor(android.graphics.Color.parseColor("#0C214C")); setTextColor(android.graphics.Color.WHITE) }
        val botaoExcluir = Button(this).apply { text = "EXCLUIR"; textSize = 11f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 90).apply { topMargin = 5 }; setBackgroundColor(android.graphics.Color.parseColor("#172033")); setTextColor(android.graphics.Color.WHITE) }
        botaoEditar.setOnClickListener { editarMovimentacao(mov) }
        botaoExcluir.setOnClickListener { excluirMovimentacao(mov) }
        item.addView(textoDescricao); item.addView(tipo); item.addView(valorTexto); item.addView(textoData); item.addView(botaoEditar); item.addView(botaoExcluir)
        lista.addView(item)
    }

    private fun adicionarMensagemVazio(lista: LinearLayout, ehSaida: Boolean) {
        val vazio = TextView(this).apply { text = if (ehSaida) "Nenhuma saída ainda" else "Nenhuma entrada ainda"; textSize = 14f; setTextColor(android.graphics.Color.parseColor("#9CA3AF")); gravity = Gravity.CENTER; setPadding(10,20,10,20); tag = if (ehSaida) "vazio_saida" else "vazio_entrada" }
        lista.addView(vazio)
    }

    private fun editarMovimentacao(mov: Movimentacao) {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(30,20,30,20) }
        val campoDescricao = EditText(this).apply { setText(mov.descricao); inputType = InputType.TYPE_CLASS_TEXT }
        val campoValor = EditText(this).apply { setText(String.format(Locale.US, "%.2f", mov.valor)); inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val campoData = EditText(this).apply { setText(mov.data); isFocusable = false; isClickable = true }
        campoData.setOnClickListener { val calendario = Calendar.getInstance(); DatePickerDialog(this, { _, ano, mes, dia -> val novaData = Calendar.getInstance().apply { set(ano, mes, dia) }; campoData.setText(SimpleDateFormat("dd/MM/yyyy", localeBR).format(novaData.time)) }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show() }
        layout.addView(campoDescricao); layout.addView(campoValor); layout.addView(campoData)
        AlertDialog.Builder(this).setTitle("Editar Movimentação").setView(layout).setNegativeButton("CANCELAR", null).setPositiveButton("SALVAR") { _, _ ->
            val novaDescricao = campoDescricao.text.toString().trim(); val novoValor = campoValor.text.toString().toDoubleOrNull(); val novaData = campoData.text.toString().trim()
            if (novaDescricao.isNotEmpty() && novoValor != null && novoValor > 0 && novaData.isNotEmpty()) {
                val editada = mov.copy(descricao = novaDescricao, valor = novoValor, data = novaData, sincronizado = false)
                val i = movimentacoes.indexOfFirst { it.id == mov.id }
                if (i >= 0) movimentacoes[i] = editada
                renderizar()
                salvarHistorico(editada)
            }
        }.create().show()
    }

    private fun atualizarTextoValor(texto: TextView, valor: Double, ehSaida: Boolean) {
        val sinal = if (ehSaida) "-" else "+"
        texto.text = String.format(localeBR, "%s R$ %.2f", sinal, valor)
    }
}
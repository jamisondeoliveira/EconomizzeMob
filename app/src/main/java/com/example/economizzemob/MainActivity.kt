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
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Movimentacao(val descricao: String, val valor: Double, val data: String, val ehSaida: Boolean): java.io.Serializable

class MainActivity : AppCompatActivity() {

    private var saldo = 0.0
    private val entradas = mutableListOf<Movimentacao>()
    private val saidas = mutableListOf<Movimentacao>()
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

        if (savedInstanceState != null) {
            saldo = savedInstanceState.getDouble("saldo", 0.0)
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            val entradasSalvas = savedInstanceState.getSerializable("entradas") as? ArrayList<Movimentacao>
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            val saidasSalvas = savedInstanceState.getSerializable("saidas") as? ArrayList<Movimentacao>
            entradasSalvas?.let { entradas.addAll(it); it.forEach { m -> adicionarValor(listaEntrada, m.descricao, m.valor, m.data, false) } }
            saidasSalvas?.let { saidas.addAll(it); it.forEach { m -> adicionarValor(listaSaida, m.descricao, m.valor, m.data, true) } }
            atualizarSaldo()
        }

        findViewById<Button>(R.id.btnEntrada).setOnClickListener { abrirDialogoMovimentacao("Adicionar Entrada", "Digite o valor", listaEntrada, false) }
        findViewById<Button>(R.id.btnSaida).setOnClickListener { abrirDialogoMovimentacao("Adicionar Saída", "Digite a despesa", listaSaida, true) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("saldo", saldo)
        outState.putSerializable("entradas", ArrayList(entradas))
        outState.putSerializable("saidas", ArrayList(saidas))
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

    private fun abrirDialogoMovimentacao(titulo: String, textoValor: String, lista: LinearLayout, ehSaida: Boolean) {
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
                    if (ehSaida) { saldo -= valor; saidas.add(mov) } else { saldo += valor; entradas.add(mov) }
                    atualizarSaldo()
                    adicionarValor(lista, descricao, valor, data, ehSaida)
                    salvarHistorico()
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

    // AGORA FORA, CORRETO!
    private fun salvarHistorico() {
        val todas = mutableListOf<Movimentacao>()
        todas.addAll(entradas)
        todas.addAll(saidas)
        val json = Gson().toJson(todas)
        getSharedPreferences("economizze", MODE_PRIVATE).edit().putString("movs", json).apply()
        getSharedPreferences("economizze", MODE_PRIVATE).edit().putString("saldo", saldo.toString()).apply()
    }

    private fun carregarHistorico() {
        // se quiser carregar ao abrir, descomenta
        // val prefs = getSharedPreferences("economizze", MODE_PRIVATE)
        // saldo = prefs.getString("saldo", "0.0")?.toDoubleOrNull() ?: 0.0
        // atualizarSaldo()
    }

    private fun adicionarValor(lista: LinearLayout, descricao: String, valor: Double, data: String, ehSaida: Boolean) {
        lista.findViewWithTag<TextView>("vazio_entrada")?.let { lista.removeView(it) }
        lista.findViewWithTag<TextView>("vazio_saida")?.let { lista.removeView(it) }
        val item = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_VERTICAL; setPadding(20, 15, 20, 15) }
        val fundo = android.graphics.drawable.GradientDrawable().apply { setColor(android.graphics.Color.WHITE); cornerRadius = 20f }
        item.background = fundo
        item.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = 12 }
        val textoDescricao = TextView(this).apply { text = descricao; textSize = 17f; setTextColor(android.graphics.Color.parseColor("#172033")); setTypeface(null, android.graphics.Typeface.BOLD) }
        val tipo = TextView(this).apply { text = if (ehSaida) "Saída" else "Entrada"; textSize = 13f; setTextColor(android.graphics.Color.parseColor("#737B8C")) }
        var valorAtual = valor
        val valorTexto = TextView(this).apply { textSize = 20f; setTextColor(android.graphics.Color.parseColor("#0C214C")); setTypeface(null, android.graphics.Typeface.BOLD) }
        atualizarTextoValor(valorTexto, valorAtual, ehSaida)
        val textoData = TextView(this).apply { text = "📅 $data"; textSize = 13f; setTextColor(android.graphics.Color.parseColor("#737B8C")) }
        val botaoEditar = Button(this).apply { text = "EDITAR"; textSize = 11f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 90).apply { topMargin = 10 }; setBackgroundColor(android.graphics.Color.parseColor("#0C214C")); setTextColor(android.graphics.Color.WHITE) }
        val botaoExcluir = Button(this).apply { text = "EXCLUIR"; textSize = 11f; layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 90).apply { topMargin = 5 }; setBackgroundColor(android.graphics.Color.parseColor("#172033")); setTextColor(android.graphics.Color.WHITE) }
        botaoEditar.setOnClickListener { val valorAnterior = valorAtual; editarMovimentacao(textoDescricao, valorTexto, textoData, valorAnterior, ehSaida) { novoValor -> valorAtual = novoValor } }
        botaoExcluir.setOnClickListener {
            if (ehSaida) { saldo += valorAtual; saidas.removeIf { it.valor == valorAtual } } else { saldo -= valorAtual; entradas.removeIf { it.valor == valorAtual } }
            atualizarSaldo()
            salvarHistorico()
            lista.removeView(item)
            if (lista.childCount == 0) adicionarMensagemVazio(lista, ehSaida)
        }
        item.addView(textoDescricao); item.addView(tipo); item.addView(valorTexto); item.addView(textoData); item.addView(botaoEditar); item.addView(botaoExcluir)
        lista.addView(item)
    }

    private fun adicionarMensagemVazio(lista: LinearLayout, ehSaida: Boolean) {
        val vazio = TextView(this).apply { text = if (ehSaida) "Nenhuma saída ainda" else "Nenhuma entrada ainda"; textSize = 14f; setTextColor(android.graphics.Color.parseColor("#9CA3AF")); gravity = Gravity.CENTER; setPadding(10,20,10,20); tag = if (ehSaida) "vazio_saida" else "vazio_entrada" }
        lista.addView(vazio)
    }

    private fun editarMovimentacao(textoDescricao: TextView, valorTexto: TextView, textoData: TextView, valorAntigo: Double, ehSaida: Boolean, atualizarValor: (Double) -> Unit) {
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(30,20,30,20) }
        val campoDescricao = EditText(this).apply { setText(textoDescricao.text); inputType = InputType.TYPE_CLASS_TEXT }
        val campoValor = EditText(this).apply { setText(String.format(Locale.US, "%.2f", valorAntigo)); inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL }
        val campoData = EditText(this).apply { setText(textoData.text.toString().replace("📅 ", "")); isFocusable = false; isClickable = true }
        campoData.setOnClickListener { val calendario = Calendar.getInstance(); DatePickerDialog(this, { _, ano, mes, dia -> val novaData = Calendar.getInstance().apply { set(ano, mes, dia) }; campoData.setText(SimpleDateFormat("dd/MM/yyyy", localeBR).format(novaData.time)) }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show() }
        layout.addView(campoDescricao); layout.addView(campoValor); layout.addView(campoData)
        AlertDialog.Builder(this).setTitle("Editar Movimentação").setView(layout).setNegativeButton("CANCELAR", null).setPositiveButton("SALVAR") { _, _ ->
            val novaDescricao = campoDescricao.text.toString().trim(); val novoValor = campoValor.text.toString().toDoubleOrNull(); val novaData = campoData.text.toString().trim()
            if (novaDescricao.isNotEmpty() && novoValor != null && novoValor > 0 && novaData.isNotEmpty()) {
                if (ehSaida) saldo += valorAntigo else saldo -= valorAntigo
                if (ehSaida) saldo -= novoValor else saldo += novoValor
                textoDescricao.text = novaDescricao; atualizarTextoValor(valorTexto, novoValor, ehSaida); textoData.text = "📅 $novaData"; atualizarValor(novoValor); atualizarSaldo(); salvarHistorico()
            }
        }.create().show()
    }

    private fun atualizarTextoValor(texto: TextView, valor: Double, ehSaida: Boolean) {
        val sinal = if (ehSaida) "-" else "+"
        texto.text = String.format(localeBR, "%s R$ %.2f", sinal, valor)
    }
}
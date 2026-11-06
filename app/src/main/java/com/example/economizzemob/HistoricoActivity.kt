package com.example.economizzemob

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoricoActivity : AppCompatActivity() {
    private lateinit var adapter: HistoricoAdapter
    private var todas: List<Movimentacao> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        val prefs = getSharedPreferences("economizze", MODE_PRIVATE)
        val json = prefs.getString("movs", "[]")
        val type = object : TypeToken<List<Movimentacao>>() {}.type
        todas = Gson().fromJson(json, type)

        val recycler = findViewById<RecyclerView>(R.id.recyclerHistorico)
        adapter = HistoricoAdapter(todas)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnFiltroTodos).setOnClickListener { adapter.atualizar(todas) }
        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnFiltroEntradas).setOnClickListener { adapter.atualizar(todas.filter { !it.ehSaida }) }
        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnFiltroSaidas).setOnClickListener { adapter.atualizar(todas.filter { it.ehSaida }) }
    }
}
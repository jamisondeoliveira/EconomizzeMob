package com.example.economizzemob

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoricoAdapter(private var lista: List<Movimentacao>) : RecyclerView.Adapter<HistoricoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val desc: TextView = view.findViewById(R.id.txtDescHistorico)
        val data: TextView = view.findViewById(R.id.txtDataHistorico)
        val valor: TextView = view.findViewById(R.id.txtValorHistorico)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_historico, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val m = lista[position]
        holder.desc.text = m.descricao
        holder.data.text = "📅 ${m.data} - ${if(m.ehSaida) "Saída" else "Entrada"}"
        holder.valor.text = String.format(java.util.Locale("pt","BR"), "%s R$ %.2f", if(m.ehSaida) "-" else "+", m.valor)
        holder.valor.setTextColor(android.graphics.Color.parseColor(if(m.ehSaida) "#FF3B30" else "#0C8A43"))
    }

    override fun getItemCount() = lista.size
    fun atualizar(nova: List<Movimentacao>) { lista = nova; notifyDataSetChanged() }
}
// EpiAdapter.kt
package com.example.epiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EpiAdapter(
    private val context: Context,
    private var epiList: List<Epi>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<EpiAdapter.EpiViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(epi: Epi)
        fun onDeleteClick(epi: Epi)
    }

    inner class EpiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeTextView: TextView = itemView.findViewById(R.id.tvNome)
        val quantidadeTextView: TextView = itemView.findViewById(R.id.tvQuantidade)
        val descricaoTextView: TextView = itemView.findViewById(R.id.tvDescricao)
        val optionsTextView: TextView = itemView.findViewById(R.id.tvOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpiViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_epi, parent, false)
        return EpiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpiViewHolder, position: Int) {
        val epi = epiList[position]
        holder.nomeTextView.text = epi.nome
        holder.quantidadeTextView.text = "Quantidade: ${epi.quantidade}"
        holder.descricaoTextView.text = epi.descricao

        holder.optionsTextView.setOnClickListener {
            val popup = PopupMenu(context, holder.optionsTextView)
            popup.inflate(R.menu.menu_options)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        listener.onEditClick(epi)
                        true
                    }
                    R.id.menu_delete -> {
                        listener.onDeleteClick(epi)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return epiList.size
    }

    fun updateList(newList: List<Epi>) {
        epiList = newList
        notifyDataSetChanged()
    }
}

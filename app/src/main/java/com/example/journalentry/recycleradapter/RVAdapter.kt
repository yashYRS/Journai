package com.example.journalentry.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.journalentry.R

class RvAdapter(private val noteList: ArrayList<Model>) : RecyclerView.Adapter<RvAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.adapter_item_layout, p0, false)
        return ViewHolder(v);
    }
    override fun getItemCount(): Int {
        return noteList.size
    }
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.name.text = noteList[p1].index
        p0.content.text = noteList[p1].content
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.card_name)
        val content: TextView = itemView.findViewById(R.id.card_content)

    }
}
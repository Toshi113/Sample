package com.example.sample

import android.view.View
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 普通にViewHolder。
class RecyclerViewHolder (view: View) : RecyclerView.ViewHolder(view){
    // インターフェースもたせてClickListenerにする。
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
    val idContainer: Space = view.findViewById(R.id.id_container)
    val titleTextView: TextView = view.findViewById(R.id.title_textView)
    val detailTextView: TextView = view.findViewById(R.id.detail_textView)
}
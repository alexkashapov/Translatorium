package com.fake.translatorium.main.view

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.widget.TextView
import com.fake.translatorium.R

class TranslateViewHolder(itemView: View): ViewHolder(itemView){
        val translate = itemView.findViewById<TextView>(R.id.text)
        val lang = itemView.findViewById<TextView>(R.id.lang)
}
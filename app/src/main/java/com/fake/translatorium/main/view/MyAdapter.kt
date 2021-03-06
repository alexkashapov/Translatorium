package com.fake.translatorium.main.view

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fake.translatorium.R
import com.fake.translatorium.main.db.Translated
import java.util.*


class MyAdapter() : RecyclerView.Adapter<TranslateViewHolder>() {
    val translates: ArrayList<Translated> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranslateViewHolder {
        val itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.i_recview, parent, false)
        return TranslateViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return translates.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TranslateViewHolder, position: Int) {
        val currentTranslate = translates.get(position)
        //Заполняем поля viewHolder'а данными из элемента набора данных
        holder.translate.text = "${currentTranslate.text} - ${currentTranslate.translated}"
        holder.lang.setText(currentTranslate.langdir)
    }

    fun addAll(history: List<Translated>) {
        translates.addAll(history)
        notifyDataSetChanged()
    }

    fun addItem(tr: Translated) {
        translates.add(tr)
        notifyItemInserted(itemCount)
    }

    fun swapItems(posPrev: Int, posNext: Int) {
        Collections.swap(translates, posPrev, posNext)
        notifyItemMoved(posPrev, posNext)
    }

    fun removeItem(position: Int): Translated {
        val tr = translates.removeAt(position)
        notifyItemRemoved(position)
        return tr
    }

    fun deleteAll() {
        translates.clear()
        notifyDataSetChanged()
    }

}


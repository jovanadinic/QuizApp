package com.example.quizapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class InfoPagerAdapter(private val layouts: List<Int>, private val context: Context) :
    RecyclerView.Adapter<InfoPagerAdapter.InfoViewHolder>() {

    class InfoViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val layout = LayoutInflater.from(context).inflate(viewType, parent, false)
        return InfoViewHolder(layout)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {

    }

    override fun getItemViewType(position: Int): Int {
        return layouts[position]
    }

    override fun getItemCount(): Int {
        return layouts.size
    }
}

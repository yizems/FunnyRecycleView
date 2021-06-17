package cn.yize.funny.recycleview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.yize.funny.recycleview.R

open class SimpleTextAdapter(
    private val context: Context,
    val data: List<String>,
) : RecyclerView.Adapter<SimpleTextAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.funny_recycleview_item_simple_text, parent, false) as TextView
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }


    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}
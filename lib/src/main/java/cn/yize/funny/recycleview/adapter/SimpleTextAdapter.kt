package cn.yize.funny.recycleview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import cn.yize.funny.recycleview.R

open class SimpleTextAdapter(
    context: Context,
    val data: MutableList<String>,
) : AbsTextViewAdapter(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.funny_recycleview_item_simple_text, parent, false) as TextView
        )
    }

    override fun onBindViewHolder(holderText: TextViewHolder, position: Int) {
        holderText.textView.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

}


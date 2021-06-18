package cn.yize.funny.recycleview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.yize.funny.recycleview.R


abstract class AbsTextViewAdapter(val context: Context) : RecyclerView.Adapter<TextViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.funny_recycleview_item_simple_text, parent, false) as TextView
        )
    }
}

class TextViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

package cn.yize.funny.recycleview.listener.transform

import android.view.View
import android.widget.TextView
import cn.yize.funny.recycleview.listener.OnItemScrollListener

class TextViewColorSizeTransform(
    private val selectTextColor: Int,
    private val unSelectTextColor: Int,
    private val selectTextSizeSp: Float,
    private val unSelectTextSizeSp: Float,
) : OnItemScrollListener {

    override fun onItemScrollInSelected(itemView: View, position: Int) {
        val textView = itemView as TextView
        textView.textSize = selectTextSizeSp
        textView.setTextColor(selectTextColor)
    }

    override fun onItemScrollOutSelected(itemView: View, position: Int) {
        val textView = itemView as TextView
        textView.textSize = unSelectTextSizeSp
        textView.setTextColor(unSelectTextColor)
    }
}
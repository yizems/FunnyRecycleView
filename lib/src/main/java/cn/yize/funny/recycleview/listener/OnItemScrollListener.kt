package cn.yize.funny.recycleview.listener

import android.view.View

/**
 * 滚动过程中通知
 */
interface OnItemScrollListener {
    /** item 进入选择区域 */
    fun onItemScrollInSelected(itemView: View, position: Int)

    /** item 移出选择区域 */
    fun onItemScrollOutSelected(itemView: View, position: Int)
}
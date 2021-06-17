package cn.yize.funny.recycleview.listener

import android.view.View

interface IListenerOwner {

    fun getOnItemFillListener(): Set<OnItemScrollListener>

    fun getOnItemSelectedListener(): Set<OnItemSelectedListener>

    fun addOnItemFillListener(listener: OnItemScrollListener)

    fun removeOnItemFillListener(listener: OnItemScrollListener)

    fun removeAllItemFillListener()

    fun addOnItemSelectedListener(listener: OnItemSelectedListener)

    fun removeOnItemSelectedListener(listener: OnItemSelectedListener)

    fun removeAllItemSelectedListener()


    fun dispatchOnItemScrollInSelected(itemView: View, position: Int) {
        getOnItemFillListener().forEach {
            it.onItemScrollInSelected(itemView, position)
        }
    }

    fun dispatchOnItemScrollOutSelected(itemView: View, position: Int) {
        getOnItemFillListener().forEach {
            it.onItemScrollOutSelected(itemView, position)
        }
    }

    fun dispatchOnItemSelected(position: Int) {
        getOnItemSelectedListener().forEach {
            it.onItemSelected(position)
        }
    }
}
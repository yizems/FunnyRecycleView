package cn.yize.funny.recycleview.listener

import android.view.View

interface IListenerDelegate : IListenerOwner {

    fun getOnItemScrollListener(): Set<OnItemScrollListener>

    fun getOnItemSelectedListener(): Set<OnItemSelectedListener>

    fun dispatchOnItemScrollInSelected(itemView: View, position: Int) {
        getOnItemScrollListener().forEach {
            it.onItemScrollInSelected(itemView, position)
        }
    }

    fun dispatchOnItemScrollOutSelected(itemView: View, position: Int) {
        getOnItemScrollListener().forEach {
            it.onItemScrollOutSelected(itemView, position)
        }
    }

    fun dispatchOnItemSelected(position: Int) {
        getOnItemSelectedListener().forEach {
            it.onItemSelected(position)
        }
    }
}

class ListenerDelegate : IListenerDelegate {

    //选中中间item的监听器的集合
    private val mOnItemSelectedListener = mutableSetOf<OnItemSelectedListener>()

    //子view填充或滚动监听器的集合
    private val mOnItemScrollListener = mutableSetOf<OnItemScrollListener>()


    override fun getOnItemScrollListener() = mOnItemScrollListener

    override fun getOnItemSelectedListener() = mOnItemSelectedListener


    override fun addOnItemScrollListener(listener: OnItemScrollListener) {
        mOnItemScrollListener.add(listener)
    }

    override fun removeOnItemScrollListener(listener: OnItemScrollListener) {
        mOnItemScrollListener.remove(listener)
    }

    override fun removeAllItemScrollListener() {
        mOnItemScrollListener.clear()
    }

    override fun addOnItemSelectedListener(listener: OnItemSelectedListener) {
        mOnItemSelectedListener.add(listener)
    }

    override fun removeOnItemSelectedListener(listener: OnItemSelectedListener) {
        mOnItemSelectedListener.remove(listener)
    }

    override fun removeAllItemSelectedListener() {
        mOnItemSelectedListener.clear()
    }

}

interface IListenerDelegateOwner : IListenerOwner {

    fun getListenerDelegate(): IListenerDelegate

    override fun addOnItemScrollListener(listener: OnItemScrollListener) {
        getListenerDelegate().addOnItemScrollListener(listener)
    }

    override fun removeOnItemScrollListener(listener: OnItemScrollListener) {
        getListenerDelegate().removeOnItemScrollListener(listener)
    }

    override fun removeAllItemScrollListener() {
        getListenerDelegate().removeAllItemScrollListener()
    }

    override fun addOnItemSelectedListener(listener: OnItemSelectedListener) {
        getListenerDelegate().addOnItemSelectedListener(listener)
    }

    override fun removeOnItemSelectedListener(listener: OnItemSelectedListener) {
        getListenerDelegate().removeOnItemSelectedListener(listener)
    }

    override fun removeAllItemSelectedListener() {
        getListenerDelegate().removeAllItemSelectedListener()
    }

}
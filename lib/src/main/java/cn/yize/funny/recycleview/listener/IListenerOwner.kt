package cn.yize.funny.recycleview.listener


interface IListenerOwner {

    fun addOnItemScrollListener(listener: OnItemScrollListener)

    fun removeOnItemScrollListener(listener: OnItemScrollListener)

    fun removeAllItemScrollListener()

    fun addOnItemSelectedListener(listener: OnItemSelectedListener)

    fun removeOnItemSelectedListener(listener: OnItemSelectedListener)

    fun removeAllItemSelectedListener()
}
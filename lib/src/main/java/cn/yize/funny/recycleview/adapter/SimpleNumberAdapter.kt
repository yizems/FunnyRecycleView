package cn.yize.funny.recycleview.adapter

import android.content.Context

/**
 *
 * @property minValue Int 开区间
 * @property maxValue Int 开区间
 * @constructor
 */
open class SimpleNumberAdapter(
    context: Context,
    minValue: Int,
    maxValue: Int,
) : AbsTextViewAdapter(context) {

    var minValue = minValue
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var maxValue = maxValue
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.textView.text = (minValue + position).toString()
    }

    override fun getItemCount(): Int {
        return (maxValue - minValue).let {
            if (it < 0) {
                0
            } else {
                it + 1
            }
        }
    }

    /**
     * 根据传入的值获取对应的 position
     * @param value Int
     * @return Int
     */
    fun getPosition(value: Int): Int {
        return value - minValue
    }
}
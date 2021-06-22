package cn.yize.funny.recycleview.adapter

import android.content.Context

/**
 *
 * @property minValue Int 开区间
 * @property maxValue Int 开区间
 * @property valueOffset 实际数值便宜值, 加上或减去一个数值
 * @constructor
 */
open class SimpleNumberAdapter(
    context: Context,
    minValue: Int,
    maxValue: Int,
    val valueOffset: Int = 0
) : AbsTextViewAdapter(context) {

    var minValue = minValue
        set(value) {
            if (field == value) return
            field = value
        }

    var maxValue = maxValue
        set(value) {
            if (field == value) return
            field = value
        }


    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        holder.textView.text = getItem(position).toString()
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
        return value - minValue - valueOffset
    }

    fun getItem(position: Int): Int = minValue + position + valueOffset

    fun getItemNoOffset(position: Int): Int = minValue + position

    fun getValueRange() = ((minValue + valueOffset)..(maxValue + valueOffset))

}
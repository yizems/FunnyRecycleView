package cn.yize.funny.recycleview.util

import android.content.Context
import android.util.TypedValue

internal object SizeUtil {

    fun Float.dp2px(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this, context.resources.displayMetrics
        ).toInt()
    }

    fun Int.dp2px(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(), context.resources.displayMetrics
        ).toInt()
    }
}
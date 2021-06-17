package cn.yize.funny.recycleview.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import cn.yize.funny.recycleview.Gravity
import cn.yize.funny.recycleview.Orientation
import cn.yize.funny.recycleview.layoutmanager.wheel.WheelLayoutManager

/**
 * 用于居中
 */
class WheelDecoration(
    val context: Context,
    val lineColor: Int = 0xFFE4E4E4.toInt(),
    val heightDp: Float = 0.5F,
    linePadding: Float = 0F
) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        strokeWidth = heightDp
        color = lineColor
        isAntiAlias = true
    }

    private val paddingPx = dp2px(linePadding)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val layoutManager = parent.layoutManager

        if (layoutManager !is WheelLayoutManager) {
            return
        }

        if (layoutManager.gravity != Gravity.CENTER) {
            return
        }

        val itemSpace = layoutManager.orientationHelper.totalSpace / layoutManager.visibleCount

        val startLine =
            layoutManager.orientationHelper.startAfterPadding + itemSpace * (layoutManager.visibleCount / 2)

        val endLine =
            startLine + itemSpace

        if (layoutManager.orientation == Orientation.VERTICAL) {
            c.drawLine(
                layoutManager.paddingStart + paddingPx.toFloat(),
                startLine.toFloat(),
                layoutManager.width - paddingPx.toFloat(),
                startLine.toFloat(),
                paint,
            )

            c.drawLine(
                layoutManager.paddingStart + paddingPx.toFloat(),
                endLine.toFloat(),
                layoutManager.width - paddingPx.toFloat(),
                endLine.toFloat(),
                paint,
            )
        } else {
            c.drawLine(
                startLine.toFloat(),
                layoutManager.paddingTop + paddingPx.toFloat(),
                startLine.toFloat(),
                layoutManager.height - paddingPx.toFloat(),
                paint,
            )

            c.drawLine(
                endLine.toFloat(),
                layoutManager.paddingTop + paddingPx.toFloat(),
                endLine.toFloat(),
                layoutManager.height - paddingPx.toFloat(),
                paint,
            )
        }
    }

    private fun dp2px(dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics
        ).toInt()
    }
}
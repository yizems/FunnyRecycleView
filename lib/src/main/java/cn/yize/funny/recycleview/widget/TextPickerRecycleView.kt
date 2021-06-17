package cn.yize.funny.recycleview.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.yize.funny.recycleview.Gravity
import cn.yize.funny.recycleview.adapter.SimpleTextAdapter
import cn.yize.funny.recycleview.decoration.WheelDecoration
import cn.yize.funny.recycleview.layoutmanager.wheel.WheelLayoutManager

class TextPickerRecycleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    //region WheelLayoutManager 属性


    var visibleCount: Int = 5
        set(value) {
            field = value
            setupLayoutManager()
        }
    var orientation: cn.yize.funny.recycleview.Orientation = WheelLayoutManager.DEFAULT_ORIENTATION
        set(value) {
            field = value
            setupLayoutManager()
        }
    var gravity: Gravity = Gravity.CENTER
        set(value) {
            field = value
            setupLayoutManager()
        }

    var smoothSpeed: Float = WheelLayoutManager.DEFAULT_SMOOTH_SPEED
        set(value) {
            field = value
            setupLayoutManager()
        }
    var childAlpha: Float = WheelLayoutManager.DEFAULT_ALPHA
        set(value) {
            field = value
            setupLayoutManager()
        }
    var scale: Float = WheelLayoutManager.DEFAULT_SCALE
        set(value) {
            field = value
            setupLayoutManager()
        }
    var transformView: Boolean = WheelLayoutManager.DEFAULT_TRANSFORM_VIEW
        set(value) {
            field = value
            setupLayoutManager()
        }

    //endregion

    //region WheelDecoration 属性


    var showDivider: Boolean = true
        set(value) {
            field = value
            setupLayoutManager()
        }
    var dividerColor: Int = 0xFFE4E4E4.toInt()
        set(value) {
            field = value
            setupLayoutManager()
        }
    var heightDp: Float = 0.5F
        set(value) {
            field = value
            setupLayoutManager()
        }
    var dividerPadding: Float = 0F
        set(value) {
            field = value
            setupLayoutManager()
        }

    //endregion


    //region 文字属性

    var selectTextColor: Int = 0xFF303133.toInt()
        set(value) {
            field = value
            setupLayoutManager()
        }
    var unSelectTextColor: Int = 0xFF909399.toInt()
        set(value) {
            field = value
            setupLayoutManager()
        }

    var selectTextSizeSp: Float = 18F
        set(value) {
            field = value
            setupLayoutManager()
        }
    var unSelectTextSizeSp: Float = 18F
        set(value) {
            field = value
            setupLayoutManager()
        }

    //endregion


    private val mData = mutableListOf<String>()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (layoutManager == null) {
            setupLayoutManager()
        }

        if (adapter == null) {
            adapter = SimpleTextAdapter(context, mData)
        }
    }

    private fun setupLayoutManager() {
        if (!isAttachedToWindow) {
            return
        }

        val decoration =
            if (showDivider) WheelDecoration(context, dividerColor, heightDp, dividerPadding)
            else null

        WheelLayoutManager(
            visibleCount, orientation, gravity, smoothSpeed,
            childAlpha, scale, transformView,
        ).apply {

            addOnItemFillListener(object : WheelLayoutManager.OnItemFillListener {
                override fun onItemSelected(itemView: View, position: Int) {
                    val textView = itemView as TextView
                    textView.textSize = selectTextSizeSp
                    textView.setTextColor(selectTextColor)
                }

                override fun onItemUnSelected(itemView: View, position: Int) {
                    val textView = itemView as TextView
                    textView.textSize = unSelectTextSizeSp
                    textView.setTextColor(unSelectTextColor)
                }
            })
            attach(this@TextPickerRecycleView, decoration)
        }
    }


    fun addData(data: List<String>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun replaceData(data: List<String>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        if (layoutManager != null && layoutManager !is WheelLayoutManager) {
            throw IllegalArgumentException("只接受 WheelLayoutManager ")
        }
        super.setLayoutManager(layout)
    }

    override fun getLayoutManager(): WheelLayoutManager? {
        return super.getLayoutManager() as? WheelLayoutManager
    }


    fun notifyDataSetChanged() {
        adapter?.notifyDataSetChanged()
    }


}
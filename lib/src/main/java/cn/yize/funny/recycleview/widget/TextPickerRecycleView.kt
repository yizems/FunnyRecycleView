package cn.yize.funny.recycleview.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import cn.yize.funny.recycleview.Gravity
import cn.yize.funny.recycleview.adapter.SimpleTextAdapter
import cn.yize.funny.recycleview.config.Config
import cn.yize.funny.recycleview.config.DefaultConfigOwner
import cn.yize.funny.recycleview.decoration.WheelDecoration
import cn.yize.funny.recycleview.layoutmanager.wheel.WheelLayoutManager
import cn.yize.funny.recycleview.listener.transform.TextViewColorSizeTransform

/**
 * 简单 的文字 选择器
 */
class TextPickerRecycleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {


    companion object : DefaultConfigOwner {
        override var defaultConfig: Config = Config.DEFAULT_CONFIG.copy(
            visibleCount = 5,
            orientation = cn.yize.funny.recycleview.Orientation.VERTICAL,
            gravity = Gravity.CENTER,
            smoothSpeed = 10F,
            childAlpha = 1F,
            scale = 1F,
            transformView = false,
            showDivider = true,
            dividerColor = 0xFFE4E4E4.toInt(),
            dividerHeightDp = 0.5F,
            dividerPadding = 0F,
            selectTextColor = 0xFF303133.toInt(),
            unSelectTextColor = 0xFF909399.toInt(),
            selectTextSizeSp = 18F,
            unSelectTextSizeSp = 18F,
        )

    }


    //region WheelLayoutManager 属性

    var visibleCount: Int = defaultConfig.visibleCount
        set(value) {
            field = value
            setupLayoutManager()
        }
    var orientation: cn.yize.funny.recycleview.Orientation = defaultConfig.orientation
        set(value) {
            field = value
            setupLayoutManager()
        }
    var gravity: Gravity = Gravity.CENTER
        set(value) {
            field = value
            setupLayoutManager()
        }

    var smoothSpeed: Float = defaultConfig.smoothSpeed
        set(value) {
            field = value
            setupLayoutManager()
        }
    var childAlpha: Float = defaultConfig.childAlpha
        set(value) {
            field = value
            setupLayoutManager()
        }
    var scale: Float = defaultConfig.scale
        set(value) {
            field = value
            setupLayoutManager()
        }
    var transformView: Boolean = defaultConfig.transformView
        set(value) {
            field = value
            setupLayoutManager()
        }

    //endregion

    //region WheelDecoration 属性


    var showDivider: Boolean = defaultConfig.showDivider
        set(value) {
            field = value
            setupLayoutManager()
        }
    var dividerColor: Int = defaultConfig.dividerColor
        set(value) {
            field = value
            setupLayoutManager()
        }
    var dividerHeightDp: Float = defaultConfig.dividerHeightDp
        set(value) {
            field = value
            setupLayoutManager()
        }
    var dividerPadding: Float = defaultConfig.dividerPadding
        set(value) {
            field = value
            setupLayoutManager()
        }

    //endregion


    //region 文字属性

    var selectTextColor: Int = defaultConfig.selectTextColor
        set(value) {
            field = value
            setupLayoutManager()
        }
    var unSelectTextColor: Int = defaultConfig.unSelectTextColor
        set(value) {
            field = value
            setupLayoutManager()
        }

    var selectTextSizeSp: Float = defaultConfig.selectTextSizeSp
        set(value) {
            field = value
            setupLayoutManager()
        }
    var unSelectTextSizeSp: Float = defaultConfig.unSelectTextSizeSp
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
            if (showDivider) WheelDecoration(context, dividerColor, dividerHeightDp, dividerPadding)
            else null

        WheelLayoutManager(
            visibleCount, orientation, gravity, smoothSpeed,
            childAlpha, scale, transformView,
        ).apply {

            addOnItemFillListener(
                TextViewColorSizeTransform(
                    selectTextColor, unSelectTextColor, selectTextSizeSp, unSelectTextSizeSp
                )
            )
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
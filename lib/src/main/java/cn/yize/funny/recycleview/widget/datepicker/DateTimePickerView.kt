package cn.yize.funny.recycleview.widget.datepicker

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import cn.yize.funny.recycleview.R
import cn.yize.funny.recycleview.adapter.SimpleNumberAdapter
import cn.yize.funny.recycleview.util.SizeUtil.dp2px
import cn.yize.funny.recycleview.widget.TextPickerRecycleView
import java.util.*
import kotlin.collections.LinkedHashMap

class DateTimePickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private val DEFAULT_START_TIME = ""
    }

    private val modes = mutableSetOf<Mode>().apply {
        addAll(Mode.values().toList())
    }

    private val modeViews = LinkedHashMap<Mode, ViewHolder>(8, 1F)
    private val modeAdapters = LinkedHashMap<Mode, SimpleNumberAdapter>(8, 1F)

    // 默认开始时间 1800-01-01 00:00:00:000
    private var minValue: Calendar = Calendar.getInstance().apply {
        timeInMillis = -5364691543000L
    }

    /**
     * 最大值 2799-12-31 23:59:59:999
     */
    private val maxValue: Calendar = (minValue.clone() as Calendar).apply {
        this.add(Calendar.YEAR, 1000)
        this.timeInMillis - 1
    }

    init {
        orientation = HORIZONTAL
        val typedArray = context.obtainStyledAttributes(R.styleable.DateTimePickerView)

        typedArray.recycle()
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupViews()
    }

    private fun setupViews() {

        // 未设置 mode
        if (modes.isEmpty()) {
            return
        }

        // 已初始化过
        if (modeViews.isNotEmpty()) {
            return
        }



        modes.forEach {
            val adapter = SimpleNumberAdapter(context, 0, 10)

            modeAdapters[it] = adapter

            modeViews[it] = createItemAndView().apply {
                this.pickerView.adapter = adapter
                this.titleTv.text = it.title
            }

        }

    }


    private fun createItemAndView(): ViewHolder {
        val wrapper = LinearLayout(context)
            .apply {
                orientation = VERTICAL
                layoutParams = LayoutParams(
                    0,
                    LayoutParams.WRAP_CONTENT,
                    1F,
                )
            }
        val titleView = TextView(context).apply {

            this.gravity = Gravity.CENTER

            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
            ).apply {
                minHeight = 30.dp2px(context)
                textSize = TextPickerRecycleView.defaultConfig.selectTextSizeSp
                setTextColor(TextPickerRecycleView.defaultConfig.selectTextColor)
            }
        }

        wrapper.addView(titleView)

        val textPickerRecycleView = TextPickerRecycleView(context)

        wrapper.addView(textPickerRecycleView)

        addView(wrapper)

        return ViewHolder(titleView, textPickerRecycleView)
    }


    private class ViewHolder(
        val titleTv: TextView,
        val pickerView: TextPickerRecycleView,
    )

    fun setMode(modes: Set<Mode>) {
        this.modes.addAll(modes)
    }

    /**
     * @param value Long 时间戳,毫秒级
     */
    fun setMinDateTime(value: Long) {
        minValue.timeInMillis = value
    }

    fun setMinDateTime(value: Calendar) {
        minValue.timeInMillis = value.timeInMillis
    }

    /**
     * @param value Long 时间戳,毫秒级
     */
    fun setMaxDateTime(value: Long) {
        maxValue.timeInMillis = value
    }

    fun setMaxDateTime(value: Calendar) {
        maxValue.timeInMillis = value.timeInMillis
    }


    enum class Mode(val title: String) {
        YEAR("年"),
        MONTH("月"),
        DAY("日"),
        HOUR("时"),
        MINUTE("分"),
        SECOND("秒"),
    }
}
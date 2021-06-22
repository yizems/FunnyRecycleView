package cn.yize.funny.recycleview.widget.datepicker

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import cn.yize.funny.recycleview.R
import cn.yize.funny.recycleview.adapter.SimpleNumberAdapter
import cn.yize.funny.recycleview.util.SizeUtil.dp2px
import cn.yize.funny.recycleview.widget.TextPickerRecycleView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.math.max
import kotlin.math.min

/**
 * 日期选择器: 后续补注释和优化
 */
class DateTimePickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    companion object {
        private const val FORMAT_DATE = "yyyy-MM-dd"
        private const val FORMAT_Time = "HH-mm-ss"
    }

    private val mFields = mutableSetOf<DateTimeField>()

    private val fieldViews = LinkedHashMap<DateTimeField, ViewHolder>(8, 1F)
    private val fieldAdapters = LinkedHashMap<DateTimeField, SimpleNumberAdapter>(8, 1F)

    // 默认开始时间 1900-01-01 00:00:00:000
    private var minTime: Calendar = Calendar.getInstance().apply {
        set(1900, 0, 1, 0, 0, 0)
    }

    /**
     * 最大日期 2199-12-31 23:59:59:000
     * 理论上 可以支持到 世界末日 : 支持 [Int.MAX_VALUE] 年
     */
    private val maxTime: Calendar = (minTime.clone() as Calendar).apply {
        this.add(Calendar.YEAR, 300)
        this.timeInMillis - 1
    }

    private var currentTime = Calendar.getInstance()
        .apply {
            this.timeInMillis = 0
        }


    private var mMode: Mode = Mode.DATETIME

    var onSelectedListener: ((
        ret: SelectedDateTime
    ) -> Unit)? = null

    init {
        orientation = HORIZONTAL
        val typedArray = context.obtainStyledAttributes(R.styleable.DateTimePickerView)
        typedArray.recycle()
        initMode()
    }


    private fun initMode() {
        mFields.clear()
        mFields.addAll(mMode.set)
        mFields.sortedBy { it.calendarField }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupViews()
    }


    private fun setupViews() {

        // 未设置 mode
        if (mFields.isEmpty()) {
            return
        }

        // 已初始化过
        if (fieldViews.isNotEmpty()) {
            return
        }

        mFields.forEach {

            val minValue = minTime.get(it.calendarField)

            val maxValue = maxTime.get(it.calendarField)

            val adapter =
                SimpleNumberAdapter(
                    context,
                    minValue,
                    maxValue,
                    if (it == DateTimeField.MONTH) 1 else 0
                )

            fieldAdapters[it] = adapter

            fieldViews[it] = createItemAndView().apply {
                this.pickerView.adapter = adapter
                this.titleTv.text = it.title
                this.pickerView.layoutManager

                this.pickerView.post {
                    this.pickerView.addOnItemSelectedListener { position ->
                        onItemSelected(it, position)
                    }
                }
            }
        }

        setToCurrentDay()
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

    /**
     * 初始化选中当天日期
     * @return Boolean
     */
    private fun setToCurrentDay() = fieldViews[mFields.last()]!!.pickerView.post {
        mFields.first()
            .let {
                changedField(
                    it,
                    max(Calendar.getInstance()[it.calendarField], minTime.get(it.calendarField))
                )
            }
    }

    private fun onItemSelected(changedField: DateTimeField, position: Int) = post {

        val value = fieldAdapters[changedField]!!.getItemNoOffset(position)

        if (changedField == DateTimeField.MONTH || changedField == DateTimeField.YEAR) {
            resetAfter(changedField)
        }

        currentTime.set(changedField.calendarField, value)

        mFields.filter { it > changedField }
            .forEach {
                val minValue = computeMinValue(it, currentTime)

                val maxValue = computeMaxValue(it, currentTime)

                fieldAdapters[it]?.apply {
                    if (this.minValue != minValue) {
                        this.minValue = minValue
                    }
                    if (this.maxValue != maxValue) {
                        this.maxValue = maxValue
                    }
                    this.notifyDataSetChanged()
                    currentTime.set(it.calendarField, getViewSelectedValue(it))
                }
            }

        onSelectedListener?.invoke(getSelectedDateTime())
    }

    /** 重置后面的数值, 后面的参数过大导致跳月 或 跳年 */
    private fun resetAfter(field: DateTimeField) {
        DateTimeField.values().filter { it > field }
            .forEach {
                currentTime[it.calendarField] = if (it == DateTimeField.DAY) 1 else 0
            }
    }

    private fun computeMaxValue(
        field: DateTimeField,
        newCalendar: Calendar
    ): Int {

        val isMaxEdge = mFields.filter { it < field }
            .all {
                maxTime[it.calendarField] == newCalendar[it.calendarField]
            }

        val maxFieldValue = newCalendar.getActualMaximum(field.calendarField)

        return when {
            isMaxEdge -> min(maxFieldValue, maxTime[field.calendarField])
            else -> maxFieldValue
        }
    }

    private fun computeMinValue(
        field: DateTimeField,
        newCalendar: Calendar
    ): Int {

        val isMinEdge = mFields.filter { it < field }
            .all {
                minTime[it.calendarField] == newCalendar[it.calendarField]
            }

        val minFieldValue = newCalendar.getActualMinimum(field.calendarField)

        return when {
            isMinEdge -> max(minFieldValue, minTime[field.calendarField])
            else -> minFieldValue
        }
    }

    /**
     * @param value Long 时间戳,毫秒级
     */
    fun setMinDateTime(value: Long) {
        minTime.timeInMillis = value
    }

    fun setMinDateTime(value: Calendar) {
        minTime.timeInMillis = value.timeInMillis
    }

    /**
     * @param value Long 时间戳,毫秒级
     */
    fun setMaxDateTime(value: Long) {
        maxTime.timeInMillis = value
    }

    fun setMaxDateTime(value: Calendar) {
        maxTime.timeInMillis = value.timeInMillis
    }

    fun setMode(mode: Mode) {
        requireNotInitialized()
        this.mMode = mode
        initMode()
    }

    /**
     * 获取当前选择的时间
     * 可以使用解构函数
     * @return SelectedDateTime
     */
    fun getSelectedDateTime() = SelectedDateTime(
        currentTime,
        mFields.associateWith {
            currentTime[it.calendarField] + if (it == DateTimeField.MONTH) 1 else 0
        },
        mMode.format.format(currentTime.timeInMillis),
    )

    private fun changedField(field: DateTimeField, value: Int) {
        requireInitialized()
        val adapter = fieldAdapters[field]!!
        val position = adapter.getPosition(value)
        if (position < 0 || position >= adapter.itemCount) {
            throw IllegalArgumentException("value 超出 范围: $value not in [${adapter.getValueRange()}]")
        }
        fieldViews[field]!!.pickerView.scrollToPosition(position)
    }

    private fun getViewSelectedValue(field: DateTimeField): Int {
        return fieldAdapters[field]!!.getItemNoOffset(getViewSelectedPosition(field))
    }

    private fun getViewSelectedPosition(field: DateTimeField): Int {
        return fieldViews[field]!!.pickerView.getCurrentPosition()
    }

    private fun requireNotInitialized() {
        if (fieldViews.isNotEmpty()) {
            throw IllegalArgumentException("已初始化,不可以设置")
        }
    }

    private fun requireInitialized() {
        if (fieldViews.isEmpty()) {
            throw IllegalArgumentException("必须初始化之后才可以调用")
        }
    }


    enum class DateTimeField(
        val title: String,
        val calendarField: Int,
    ) {
        YEAR("年", Calendar.YEAR),
        MONTH("月", Calendar.MONTH),
        DAY("日", Calendar.DAY_OF_MONTH),
        HOUR("时", Calendar.HOUR_OF_DAY),
        MINUTE("分", Calendar.MINUTE),
        SECOND("秒", Calendar.SECOND),
    }

    private class ViewHolder(
        val titleTv: TextView,
        val pickerView: TextPickerRecycleView,
    )

    @SuppressLint("SimpleDateFormat")
    class Mode(
        val set: Set<DateTimeField>,
        val formatStr: String,
    ) {
        companion object {
            val DATETIME = Mode(
                DateTimeField.values().toSet(),
                "yyyy-MM-dd HH:mm:ss"
            )
            val DATE = Mode(
                setOf(DateTimeField.YEAR, DateTimeField.MONTH, DateTimeField.DAY),
                "yyyy-MM-dd"
            )

            val TIME = Mode(
                setOf(DateTimeField.HOUR, DateTimeField.MINUTE, DateTimeField.SECOND),
                "HH:mm:ss"
            )
        }

        val format by lazy {
            SimpleDateFormat(formatStr)
        }
    }

    data class SelectedDateTime(
        val calendar: Calendar,
        val fieldMap: Map<DateTimeField, Int>,
        val formatStr: String,
    )
}

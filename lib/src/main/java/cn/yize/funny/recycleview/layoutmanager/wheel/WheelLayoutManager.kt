package cn.yize.funny.recycleview.layoutmanager.wheel

import android.graphics.PointF
import android.graphics.Rect
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import cn.yize.funny.recycleview.Gravity
import cn.yize.funny.recycleview.Orientation
import cn.yize.funny.recycleview.config.Config
import cn.yize.funny.recycleview.config.DefaultConfigOwner
import cn.yize.funny.recycleview.decoration.WheelDecoration
import cn.yize.funny.recycleview.listener.IListenerDelegate
import cn.yize.funny.recycleview.listener.IListenerDelegateOwner
import cn.yize.funny.recycleview.listener.ListenerDelegate
import cn.yize.funny.recycleview.snaphelper.GravitySnapHelper
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class WheelLayoutManager(
    /** 显示的条目 */
    val visibleCount: Int = defaultConfig.visibleCount,
    val orientation: Orientation = defaultConfig.orientation,
    val gravity: Gravity = defaultConfig.gravity,
    /** [smoothScrollToPosition] 的速度 倍数, 值越大,速度越慢 */
    private val smoothSpeed: Float = defaultConfig.smoothSpeed,
    private val alpha: Float = defaultConfig.childAlpha,
    private val scale: Float = defaultConfig.scale,
    private val transformView: Boolean = defaultConfig.transformView,
    private val listenerDelegate: IListenerDelegate = ListenerDelegate(),
) : RecyclerView.LayoutManager(),
    RecyclerView.SmoothScroller.ScrollVectorProvider,
    IListenerDelegateOwner {


    companion object : DefaultConfigOwner {
        override var defaultConfig: Config =
            Config.DEFAULT_CONFIG.copy(
                transformView = true,
                scale = 0.9F,
                childAlpha = 0.8F,
            )
    }


    //保存下item的width和height‘’
    private var mItemWidth: Int = 0
    private var mItemHeight: Int = 0

    /** 当前便宜量 */
    private var mOffset = 0

    /** 最大偏移量 */
    private var mMaxOffset = 0

    /** 最小偏移量 */
    private var mMinOffset = 0

    private val snapHelper = GravitySnapHelper(gravity)

    /** 滚动引发的 requestLayout 要 回调 onItemSelected */
    private var isScroll = false

    val orientationHelper by lazy {
        if (orientation == Orientation.VERTICAL) {
            OrientationHelper.createVerticalHelper(this)
        } else {
            OrientationHelper.createHorizontalHelper(this)
        }
    }

    init {
        if (gravity == Gravity.CENTER && visibleCount % 2 == 0) {
            throw IllegalArgumentException("gravity 为 center 时, visibleCount 不能为 偶数: 当前:$visibleCount")
        }
    }

    /**
     * 使用该类设置
     * @param view RecyclerView
     */
    fun attach(view: RecyclerView, decoration: WheelDecoration? = null) {
        view.layoutManager = this
        snapHelper.attachToRecyclerView(view)

        if (decoration != null) {
            view.addItemDecoration(decoration)
        }
    }


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        //如果itemCount==0了，直接移除全部view
        if (state.itemCount == 0) {
            mOffset = 0
            removeAndRecycleAllViews(recycler)
            return
        }

        if (state.isPreLayout) {
            mOffset = 0
            return
        }

        //暂时移除全部view，然后重新fill进来
        detachAndScrapAttachedViews(recycler)

        if (mOffset > mMaxOffset) {
            mOffset = mMaxOffset
        }

        val startIndex = max(getCurrentPosition() - getStartOffset(), 0)

        val endIndex = min(getCurrentPosition() + getEndOffset() + 1, state.itemCount)

        (startIndex until endIndex)
            .forEach { position ->
                val child = recycler.getViewForPosition(position)
                addView(child)
                measureChild(child, 0, 0)
                val rect = getLayoutRect(
                    position, mOffset
                )
                layoutDecoratedWithMargins(
                    child,
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom
                )
            }

        transformViews()
    }


    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        if (isScroll) {
            listenerDelegate.dispatchOnItemSelected(getCurrentPosition())
            isScroll = false
        }
    }

    /**
     * 获取 某个位置的 布局参数
     * @param position Int
     * @param offset Int 偏移量
     */
    private fun getLayoutRect(position: Int, offset: Int): Rect {
        return if (orientation == Orientation.VERTICAL) {
            Rect(
                paddingStart,
                orientationHelper.startAfterPadding + (position + getStartOffset()) * mItemHeight - offset,
                paddingStart + mItemWidth,
                orientationHelper.startAfterPadding + (position + getStartOffset()) * mItemHeight - offset + mItemHeight,
            )
        } else {
            Rect(
                orientationHelper.startAfterPadding + (position + getStartOffset()) * mItemWidth - offset,
                paddingTop,
                orientationHelper.startAfterPadding + (position + getStartOffset()) * mItemWidth - offset + mItemWidth,
                paddingTop + mItemHeight,
            )
        }
    }


    /** 获取当前选中的 position */
    fun getCurrentPosition(): Int {
        return if (orientation == Orientation.VERTICAL) {
            (mOffset.toFloat() / mItemHeight).roundToInt()
        } else {
            (mOffset.toFloat() / mItemWidth).roundToInt()
        }
    }


    /** 布局 头部 的偏移条数 */
    private fun getStartOffset(): Int {
        return when (gravity) {
            Gravity.START -> 0
            Gravity.CENTER -> visibleCount / 2
            Gravity.END -> visibleCount - 1
        }
    }

    /** 布局 尾部的偏移条数 */
    private fun getEndOffset() = visibleCount - 1 - getStartOffset()


    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        if (state.itemCount == 0) {
            super.onMeasure(recycler, state, widthSpec, heightSpec)
            return
        }
        if (state.isPreLayout) return

        //用第一个view计算宽高，这种方式可能不太好
        val itemView = recycler.getViewForPosition(0)
        addView(itemView)
        //这里不能用measureChild方法，具体看内部源码实现，内部getWidth默认为0
//        measureChildWithMargins(itemView, 0, 0)
        itemView.measure(widthSpec, heightSpec)
        mItemWidth = getDecoratedMeasuredWidth(itemView)
        mItemHeight = getDecoratedMeasuredHeight(itemView)

        detachAndScrapView(itemView, recycler)

        //设置宽高
        setWidthAndHeight(mItemWidth, mItemHeight, state)
    }


    private fun setWidthAndHeight(mItemWidth: Int, mItemHeight: Int, state: RecyclerView.State) {
        if (orientation == Orientation.VERTICAL) {
            setMeasuredDimension(
                paddingStart + paddingEnd + mItemWidth,
                mItemHeight * visibleCount + paddingTop + paddingBottom
            )
            mMaxOffset = (state.itemCount - 1) * mItemHeight
        } else {
            setMeasuredDimension(
                paddingStart + paddingEnd + mItemWidth * visibleCount,
                paddingTop + paddingBottom + mItemHeight
            )
            mMaxOffset = (state.itemCount - 1) * mItemWidth

        }
    }


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return if (orientation == Orientation.VERTICAL) {
            RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT,
            )
        } else {
            RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.MATCH_PARENT,
            )
        }
    }


    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
    ): Int {
        val consume = checkScrollEdge(dy)
        if (consume == 0) {
            return 0
        }

        layoutScroll(consume, recycler, state)

        mOffset += consume

        orientationHelper.offsetChildren(-consume)

        recycleViews(recycler, state)

        transformViews()

        return consume
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
    ): Int {
        val consume = checkScrollEdge(dx)
        if (consume == 0) {
            return 0
        }

        layoutScroll(consume, recycler, state)

        mOffset += consume

        orientationHelper.offsetChildren(-consume)

        recycleViews(recycler, state)

        transformViews()

        return consume
    }

    /** 滚动时 布局 */
    private fun layoutScroll(
        delta: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        /** 是否是向前填充 */
        val fillToStart = delta < 0

        if (childCount == 0) {
            return
        }

        // 锚点view
        val anchorView = if (fillToStart) {
            getChildAt(0)
        } else {
            getChildAt(childCount - 1)
        } ?: return

        //检测滚动距离是否足够绘制下一个View
        if (fillToStart
            && orientationHelper.getDecoratedStart(anchorView) - delta < orientationHelper.startAfterPadding
        ) {
            return
        } else if (!fillToStart
            && orientationHelper.getDecoratedEnd(anchorView) - orientationHelper.endAfterPadding > delta
        ) {
            return
        }

        //开始绘制下一个View
        var consume = 0

        val space = abs(delta)

        // 获取锚点 view 的position
        var position = getPosition(anchorView)

        while (space > consume) {
            // 下一个 position
            position = if (fillToStart) {
                position - 1
            } else {
                position + 1
            }
            //防止越界
            if (position !in 0 until state.itemCount) {
                return
            }

            val child = recycler.getViewForPosition(position)

            if (fillToStart) {
                addView(child, 0)
            } else {
                addView(child)
            }
            measureChild(child, 0, 0)
            val rect = getLayoutRect(position, mOffset)
            layoutDecorated(
                child,
                rect.left,
                rect.top,
                rect.right,
                rect.bottom,
            )
            consume += orientationHelper.getDecoratedMeasurement(child)
        }

    }

    /** 回收 */
    private fun recycleViews(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        (0 until childCount).forEach { position ->
            val view = getChildAt(position)
            if (view != null) {
                val remove =
                    orientationHelper.getDecoratedEnd(view) < orientationHelper.startAfterPadding
                            || orientationHelper.getDecoratedStart(view) > orientationHelper.endAfterPadding
                if (remove) {
                    removeAndRecycleView(view, recycler)
                }
            }
        }
    }


    /** 转换 views */
    private fun transformViews() {

        if (childCount == 0) return

        val currentItem = getCurrentPosition()
        (0..childCount)
            .mapNotNull {
                getChildAt(it)
            }.forEach { child ->
                val position = getPosition(child)

                if (transformView) {
                    calculateTransform(scale, 1F, abs(position - currentItem))
                        .let {
                            child.scaleY = it
                            child.scaleX = it
                        }
                    child.alpha = calculateTransform(alpha, 1F, abs(position - currentItem))
                }

                if (currentItem == position) {
                    listenerDelegate.dispatchOnItemScrollInSelected(child, position)
                } else {
                    listenerDelegate.dispatchOnItemScrollOutSelected(child, position)
                }
            }
    }

    private fun calculateTransform(minValue: Float, maxValue: Float, positionDelta: Int): Float {

        if (positionDelta == 0) return maxValue

        return minValue

//        val space = visibleCount / 2
//
//        if (positionDelta >= space) {
//            return minValue
//        }
//
//        val spaceValue = (maxValue - minValue) / space
//
//        return maxValue - positionDelta * spaceValue
    }


    /** 滚动边界计算 */
    private fun checkScrollEdge(delta: Int): Int {
        if (delta == 0) return delta

        if (mOffset + delta > mMaxOffset) {
            return mMaxOffset - mOffset
        } else if (mOffset + delta < mMinOffset) {
            return mMinOffset - mOffset
        }
        return delta
    }


    override fun canScrollVertically(): Boolean {
        return orientation == Orientation.VERTICAL
    }

    override fun canScrollHorizontally(): Boolean {
        return orientation == Orientation.HORIZONTAL
    }

    override fun scrollToPosition(position: Int) {
        if (itemCount == 0) {
            return
        }
        checkPosition(position)

        mOffset = position * mItemHeight
        isScroll = true
        requestLayout()
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        if (itemCount == 0) {
            return
        }

        if (getCurrentPosition() == position) {
            return
        }

        val fixPosition = if (position > getCurrentPosition()) {
            position + getEndOffset()
        } else {
            position - getStartOffset()
        }

        val linearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return super.calculateSpeedPerPixel(displayMetrics) * smoothSpeed
            }
        }
        linearSmoothScroller.targetPosition = fixPosition
        startSmoothScroll(linearSmoothScroller)
    }

    private fun checkPosition(position: Int) {
        if (position !in 0 until itemCount) {
            throw IndexOutOfBoundsException("越界: 当前大小为 $itemCount, 移动到的位置为: $position")
        }
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) return null

        val point = PointF()

        // 注意这里, 如果 使用 targetPosition > getCurrentPosition() , snaphelper 会引起回弹
        val value = if (targetPosition >= getCurrentPosition()) 1F else -1F

        if (orientation == Orientation.VERTICAL) {
            point.y = value
        } else {
            point.x = value
        }

        return point

    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        if (state == RecyclerView.SCROLL_STATE_IDLE) {

            val send = if (orientation == Orientation.VERTICAL) {
                mOffset % mItemHeight == 0
            } else {
                mOffset % mItemWidth == 0
            }

            if (send) {
                listenerDelegate.dispatchOnItemSelected(getCurrentPosition())
            }
        }
    }

    override fun getListenerDelegate(): IListenerDelegate {
        return listenerDelegate
    }
}

fun log(msg: Any) {
//    Log.e("WheelLayoutManager", msg.toString())
}
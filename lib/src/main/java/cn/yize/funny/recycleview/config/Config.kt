package cn.yize.funny.recycleview.config

import cn.yize.funny.recycleview.Gravity
import cn.yize.funny.recycleview.Orientation

data class Config(
    var visibleCount: Int = 5,
    var orientation: Orientation = Orientation.VERTICAL,
    var gravity: Gravity = Gravity.CENTER,
    var smoothSpeed: Float = 10F,
    var childAlpha: Float = 1F,
    var scale: Float = 1F,
    var transformView: Boolean = false,
    var showDivider: Boolean = true,
    var dividerColor: Int = 0xFFE4E4E4.toInt(),
    var dividerHeightDp: Float = 0.5F,
    var dividerPadding: Float = 0F,
    var selectTextColor: Int = 0xFF303133.toInt(),
    var unSelectTextColor: Int = 0xFF909399.toInt(),
    var selectTextSizeSp: Float = 18F,
    var unSelectTextSizeSp: Float = 18F,
) {
    companion object {
        var DEFAULT_CONFIG = Config()
    }
}
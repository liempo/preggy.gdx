package wtf.liempo.pregnancy.breakout

object BreakoutUtils {
    // Translates pixels into world units (meters)
    internal fun translate(pixels: Float, reverse: Boolean = false) =
            if (reverse) pixels * PIXELS_PER_METER
            else pixels / PIXELS_PER_METER

    // These variables determine how objects are
    // gonna scale in our orthographic camera
    private const val PIXELS_PER_METER = 32f
}
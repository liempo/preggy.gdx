package wtf.liempo.pregnancy.breakout

object BreakoutUtils {
    // Translates pixels into world units (meters)
    internal fun translate(pixels: Float) = pixels / PIXELS_PER_METER
    internal fun translate(pixels: Int) = pixels / PIXELS_PER_METER

    // These variables determine how objects are
    // gonna scale in our orthographic camera
    private const val PIXELS_PER_METER = 32f
}
package wtf.liempo.pregnancy.utils

object GameUtils {

    // Translates pixels into world units (meters)
    internal fun translate(pixels: Float) = pixels / PIXELS_PER_METER
    internal fun translate(pixels: Int) = pixels / PIXELS_PER_METER

    // These variables determine how objects are
    // gonna scale in our orthographic camera
    private const val PIXELS_PER_METER = 32f

    // World stepping variables
    internal const val TIME_STEP = 1f / 60f
    internal const val VELOCITY_ITERATIONS = 6
    internal const val POSITION_ITERATIONS = 2
}
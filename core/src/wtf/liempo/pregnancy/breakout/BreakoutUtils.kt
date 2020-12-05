package wtf.liempo.pregnancy.breakout

import com.badlogic.gdx.graphics.Texture
import ktx.assets.load
import wtf.liempo.pregnancy.Game
import wtf.liempo.pregnancy.mainmenu.MainMenuScreen

object BreakoutUtils {

    internal fun show(game: Game) {
        game.run {
            assets.let {
                it.load<Texture>(BreakoutScreen.TEXTURE_PADDLE)
                it.load<Texture>(BreakoutScreen.TEXTURE_BRICK)
                it.load<Texture>(BreakoutScreen.TEXTURE_BALL)
                it.finishLoading()
            }

            removeScreen<MainMenuScreen>()
            setScreen<BreakoutScreen>()
        }
    }

    // Translates pixels into world units (meters)
    internal fun translate(pixels: Float) = pixels / PIXELS_PER_METER
    internal fun translate(pixels: Int) = pixels / PIXELS_PER_METER

    // These variables determine how objects are
    // gonna scale in our orthographic camera
    private const val PIXELS_PER_METER = 32f
}
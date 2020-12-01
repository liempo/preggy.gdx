package wtf.liempo.pregnancy

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.*
import ktx.freetype.*
import wtf.liempo.pregnancy.mainmenu.MainMenuScreen

class Game : KtxGame<KtxScreen>() {

    // Important game attributes
    val assets by lazy { AssetManager() }
    val batch by lazy { SpriteBatch() }
    val viewport by lazy { FillViewport(VP_WIDTH, VP_HEIGHT, OrthographicCamera()) }

    override fun create() {
        super.create()

        // Load freetype fonts here
        assets.registerFreeTypeFontLoaders()
        assets.loadFreeTypeFont(FONT_FUTURA_TITLE) {
           size = 96; magFilter = Linear; minFilter = Linear
        }
        assets.loadFreeTypeFont(FONT_FUTURA_HEADER) {
            size = 48; magFilter = Linear; minFilter = Linear
        }

        // Load other common stuff
        assets.load<Texture>(TEXTURE_BG)
        assets.finishLoading()

        // Add the screens
        addScreen(MainMenuScreen(this))

        // Set MainMenuScreen to first screen
        setScreen<MainMenuScreen>()
    }

    override fun dispose() {
        super.dispose()
        assets.dispose()
    }

    @Suppress("unused")
    companion object {

        /** Return the relative position so I can use percentage (0.0 - 1.0) */
        internal fun relativeX(percentage: Float) = percentage * VP_WIDTH
        internal fun relativeY(percentage: Float) = percentage * VP_HEIGHT

        // Viewport size (correlates with size of background.png)
        internal const val VP_HEIGHT = 1792F
        internal const val VP_WIDTH = 1008F

        // Asset descriptor for Game class (and its screens)
        internal const val TEXTURE_BG = "common/background.png"
        internal const val FONT_FUTURA_TITLE = "fonts/futura_bold_italic.ttf"
        internal const val FONT_FUTURA_HEADER = "fonts/futura_medium.ttf"
    }
}
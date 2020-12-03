package wtf.liempo.pregnancy

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.*
import ktx.freetype.*
import wtf.liempo.pregnancy.breakout.BreakoutScreen
import wtf.liempo.pregnancy.mainmenu.MainMenuScreen

class Game : KtxGame<KtxScreen>() {

    // Important game attributes
    internal val assets by lazy { AssetManager() }
    internal val batch by lazy { SpriteBatch() }

    override fun create() {

        // Load common assets
        assets.run {
            // Freetype fonts
            registerFreeTypeFontLoaders()
            load<FreeTypeFontGenerator>(FONT_FUTURA_BOLD_ITALIC)
            load<FreeTypeFontGenerator>(FONT_FUTURA_MEDIUM)

            // Common textures
            assets.load<Texture>(TEXTURE_BG)

            // Wait for finish
            assets.finishLoading()
        }

        // Add the screens
        addScreen(MainMenuScreen(this))
        addScreen(BreakoutScreen(this))

        // Set MainMenuScreen to first screen
        setScreen<MainMenuScreen>()
    }

    override fun dispose() {
        assets.dispose()
        batch.dispose()
    }

    @Suppress("unused")
    companion object {

        // Viewport size (correlates with size of background.png)
        internal const val GAME_HEIGHT = 1920f
        internal const val GAME_WIDTH = 1080f

        // Asset descriptor for Game class (and its screens)
        internal const val TEXTURE_BG = "common/background.png"
        internal const val FONT_FUTURA_BOLD_ITALIC = "fonts/futura_bold_italic.ttf"
        internal const val FONT_FUTURA_MEDIUM = "fonts/futura_medium.ttf"
    }
}
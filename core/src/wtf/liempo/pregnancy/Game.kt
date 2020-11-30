package wtf.liempo.pregnancy

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import ktx.app.KtxGame
import ktx.app.KtxScreen
import wtf.liempo.pregnancy.mainmenu.MainMenuScreen

class Game : KtxGame<KtxScreen>() {

    val assets by lazy { AssetManager() }

    override fun create() {
        super.create()

        // Load common assets
        assets.load(ASSET_BACKGROUND)
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

    companion object {
        // Viewport size (correlates with size of background.png)
        internal const val VP_HEIGHT = 1792F
        internal const val VP_WIDTH = 1008F

        // Asset descriptor for Game class (and its screens)
        internal val ASSET_BACKGROUND = AssetDescriptor(
                "background.png", Texture::class.java)
    }
}
package wtf.liempo.pregnancy

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.*
import wtf.liempo.pregnancy.mainmenu.MainMenuScreen

class Game : KtxGame<KtxScreen>() {

    val batch by lazy { SpriteBatch() }
    val camera by lazy { OrthographicCamera() }
    val viewport by lazy {
        FillViewport(VP_WIDTH, VP_HEIGHT, camera)
    }

    val assets by lazy { AssetManager() }

    override fun create() {
        super.create()

        // Load common assets here
        assets.load<Texture>(ASSET_BACKGROUND)
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
        internal const val ASSET_BACKGROUND = "background.png"
    }
}
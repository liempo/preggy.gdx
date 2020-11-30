package wtf.liempo.pregnancy.mainmenu

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.app.KtxScreen
import wtf.liempo.pregnancy.Game

class MainMenuScreen(game: Game): KtxScreen {

    private val batch by lazy { SpriteBatch() }
    private val camera by lazy { OrthographicCamera() }
    private val viewport by lazy {
        FillViewport(Game.VP_WIDTH, Game.VP_HEIGHT, camera)
    }

    private val background by lazy {
        game.assets.get(Game.ASSET_BACKGROUND)
    }

    override fun render(delta: Float) {
        batch.begin()

        batch.draw(background, 0f, 0f)

        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        batch.projectionMatrix = camera.combined
    }
}
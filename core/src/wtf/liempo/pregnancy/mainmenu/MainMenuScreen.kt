package wtf.liempo.pregnancy.mainmenu

import com.badlogic.gdx.graphics.Texture
import ktx.app.KtxScreen
import ktx.graphics.use
import wtf.liempo.pregnancy.Game

class MainMenuScreen(private val game: Game): KtxScreen {

    private val background: Texture =
            game.assets.get(Game.ASSET_BACKGROUND)

    override fun render(delta: Float) {
        game.batch.use {
            game.batch.draw(background, 0f, 0f)
        }
    }

    override fun resize(width: Int, height: Int) {
        game.viewport.update(width, height, true)
        game.batch.projectionMatrix = game.camera.combined
    }
}
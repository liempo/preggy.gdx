package wtf.liempo.pregnancy.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.scene2d.*
import ktx.style.label
import ktx.style.skin
import ktx.style.textButton
import wtf.liempo.pregnancy.Game

class MainMenuScreen(private val game: Game): KtxScreen {

    private val stage = Stage(game.viewport)
    private val background: Texture =
            game.assets[Game.TEXTURE_BG]

    override fun show() {
        // Initialize the default skin to be used by Scene2D
        // TODO convert this shit to json for better (???)
        Scene2DSkin.defaultSkin = skin {
            label("title") {
                font = game.assets[Game.FONT_FUTURA_TITLE]
            }

            textButton {
                font = game.assets[Game.FONT_FUTURA_HEADER]
                fontColor = Color.valueOf("64113f")
                downFontColor = Color.WHITE
            }
        }

        // Setup the fucking stage
        Gdx.input.inputProcessor = stage
        stage.actors {
            table {
                // Table settings:
                setFillParent(true)

                // Table children:
                label("Pregnancy", style = "title")
                textButton("brick breaker")
            }
        }
    }

    override fun render(delta: Float) {
        stage.batch.use {
            it.draw(background, 0f, 0f)
        }

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
}
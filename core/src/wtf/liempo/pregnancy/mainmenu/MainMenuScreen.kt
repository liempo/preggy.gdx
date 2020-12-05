package wtf.liempo.pregnancy.mainmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.app.KtxScreen
import ktx.assets.load
import ktx.freetype.generateFont
import ktx.graphics.use
import ktx.scene2d.*
import ktx.style.label
import ktx.style.skin
import ktx.style.textButton
import wtf.liempo.pregnancy.Game
import wtf.liempo.pregnancy.breakout.BreakoutScreen

class MainMenuScreen(private val game: Game):
        KtxScreen, ClickListener() {

    private val camera = OrthographicCamera()
    private val viewport = FillViewport(
            Game.GAME_WIDTH, Game.GAME_HEIGHT, camera)
    private val stage = Stage(viewport)

    private val background: Texture =
            game.assets[Game.TEXTURE_BG]

    private val title: BitmapFont by lazy {
        val generator: FreeTypeFontGenerator
                = game.assets[Game.FONT_FUTURA_BOLD_ITALIC]
        generator.generateFont { size = 128 }
    }

    private val subtitle: BitmapFont by lazy {
        val generator: FreeTypeFontGenerator
           = game.assets[Game.FONT_FUTURA_MEDIUM]
        generator.generateFont { size = 72 }
    }

    override fun show() {
        // Initialize the default skin to be used by Scene2D
        Scene2DSkin.defaultSkin = skin {
            label("title") {
                font = title
            }

            textButton {
                font = subtitle
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
                row().pad(32f)
                textButton("breakout")
                        .addListener(this@MainMenuScreen)
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

    override fun clicked(event: InputEvent?, x: Float, y: Float) {
        // TODO determine which button from event
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

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
}
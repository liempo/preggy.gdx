package wtf.liempo.pregnancy.breakout

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import ktx.app.KtxScreen
import ktx.box2d.*
import ktx.graphics.use
import wtf.liempo.pregnancy.Game

class BreakoutScreen(private val game: Game): KtxScreen {

    // Box2D stuffs and renderer
    private val world = createWorld(Vector2(0f, 0f))
    private val debugRenderer = Box2DDebugRenderer()

    private val background: Texture =
            game.assets[Game.TEXTURE_BG]


    override fun show() {
        val ground = world.body {
            type = BodyDef.BodyType.StaticBody
            position.set(Vector2(0f, 0f))

            // Not sure but let's try 9 as width
            box(width = 1f, height = 1f) {
                density = 1f
            }

        }

        val fixture = ground.box(width = 1f, height = 1f){
            density = 1f
        }
    }

    override fun render(delta: Float) {
        debugRenderer.render(world, game.viewport.camera.combined)

        world.step(1/60f, 6, 2)

        game.batch.use {
            it.draw(background, 0f, 0f)
        }
    }

    companion object {

        // This variables determines the pixel per meter,
        // will be used to scale down the World (Box2D)
        private const val PPM = 100
    }

}
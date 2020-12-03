package wtf.liempo.pregnancy.breakout

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.app.KtxScreen
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.createWorld
import wtf.liempo.pregnancy.Game

class BreakoutScreen(private val game: Game): KtxScreen {

    private val camera = OrthographicCamera()
    private val viewport = FillViewport(
            translate(Game.GAME_WIDTH),
            translate(Game.GAME_HEIGHT),
            camera)

    private val world = createWorld()
    private var accumulator = 0f

    private val renderer = Box2DDebugRenderer()

    override fun show() {

        // Setup the bodies
        val ceiling = world.body {
            position.set(0f, translate(Game.GAME_HEIGHT))

            box (translate(Game.GAME_WIDTH * 2), translate(20f)) {
                density = 1f
            }
        }
    }

    override fun render(delta: Float) {
        renderer.render(world, camera.combined)

        // Step world code snippet (not sure what's going on here)
        accumulator += delta.coerceAtMost(0.25f)
        if (accumulator >= TIME_STEP) {
            accumulator -= TIME_STEP
            world.step(TIME_STEP,
                    VELOCITY_ITERATIONS,
                    POSITION_ITERATIONS)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    companion object {

        // Translates pixels into world units (meters)
        private fun translate(pixels: Float) = pixels / PIXELS_PER_METER

        // These variables determine how objects are
        // gonna scale in our orthographic camera
        private const val SCALE = 2f
        private const val PIXELS_PER_METER = 32f

        // World stepping variables
        private const val TIME_STEP = 1f / 60f
        private const val VELOCITY_ITERATIONS = 6
        private const val POSITION_ITERATIONS = 2
    }

}
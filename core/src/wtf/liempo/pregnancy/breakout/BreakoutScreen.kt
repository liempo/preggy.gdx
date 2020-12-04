package wtf.liempo.pregnancy.breakout

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.app.KtxScreen
import ktx.box2d.*
import ktx.graphics.use
import ktx.math.vec2
import wtf.liempo.pregnancy.Game
import wtf.liempo.pregnancy.Game.Companion.GAME_HEIGHT
import wtf.liempo.pregnancy.Game.Companion.GAME_WIDTH
import wtf.liempo.pregnancy.breakout.BreakoutUtils.translate
import kotlin.experimental.or

class BreakoutScreen(private val game: Game): KtxScreen {

    // Screen and camera management
    private val camera = OrthographicCamera()
    private val viewport = FillViewport(
            translate(GAME_WIDTH),
            translate(GAME_HEIGHT),
            camera)

    // Box2D attributes
    private val world = createWorld(vec2(y = -9.8f))
    private val renderer = Box2DDebugRenderer()
    private var accumulator = 0f

    // World bodies, the essential one
    private lateinit var ball: Body
    private lateinit var paddle: Body

    override fun show() {
        // ---- SETUP THE WORLD SURROUNDINGS ----
        //  Iterate SurroundingEdges and create a body for it
        for (edge in SurroundingEdges.values()) {
            world.body(BodyDef.BodyType.StaticBody) {
                // Create the shape and fixture
                edge(edge.start, edge.end) {
                    density = 1f
                    restitution = 1f
                    friction = 100f

                    // Setup the collision filter
                    filter {
                        categoryBits = BIT_WALL
                        maskBits = BIT_BALL or BIT_PADDLE
                    }
                }
            }
        }

        // ---- SETUP ESSENTIAL BODIES ----
        ball = world.body(BodyDef.BodyType.DynamicBody) {
            position.set(translate(GAME_WIDTH / 2),
                    translate(GAME_HEIGHT / 2))
            userData = "ball"

            circle(radius = translate(32f)) {
                restitution = 0.8f; density = 8f; friction = 0f
                filter {
                    categoryBits = BIT_BALL
                    maskBits = BIT_PADDLE or BIT_WALL
                }
            }
        }

        paddle = world.body(BodyDef.BodyType.DynamicBody) {
            // 10% above ground, and centered horizontally
            position.set(translate(GAME_WIDTH / 2),
                    translate(GAME_HEIGHT * 0.10f))

            box(translate(128f), translate(32f)) {
                restitution = 0.5f; density = 10f; friction = 0.4f
                filter {
                    categoryBits = BIT_PADDLE
                    maskBits = BIT_BALL or BIT_WALL
                }
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

        // Render textures here
        game.batch.use {

        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    companion object {
        // World stepping variables
        private const val TIME_STEP = 1f / 60f
        private const val VELOCITY_ITERATIONS = 6
        private const val POSITION_ITERATIONS = 2

        // Category bits for collision filtering
        // Read more here https://bit.ly/2VArJdX
        private const val BIT_WALL: Short = 0x0002
        private const val BIT_PADDLE: Short = 0x0004
        private const val BIT_BALL: Short = 0x0008
        private const val BIT_BRICK: Short = 0x000A
    }

}
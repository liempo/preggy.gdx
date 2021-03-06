package wtf.liempo.pregnancy.breakout

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef
import com.badlogic.gdx.utils.viewport.FillViewport
import ktx.app.KtxScreen
import ktx.assets.load
import ktx.box2d.*
import ktx.collections.gdxArrayOf
import ktx.graphics.use
import ktx.math.vec2
import ktx.math.vec3
import wtf.liempo.pregnancy.Game
import wtf.liempo.pregnancy.Game.Companion.GAME_HEIGHT
import wtf.liempo.pregnancy.Game.Companion.GAME_WIDTH
import wtf.liempo.pregnancy.mainmenu.MainMenuScreen
import wtf.liempo.pregnancy.utils.GameUtils
import wtf.liempo.pregnancy.utils.GameUtils.translate
import wtf.liempo.pregnancy.utils.SurroundingEdges
import kotlin.experimental.or
import kotlin.random.Random

class BreakoutScreen(private val game: Game) :
        KtxScreen, InputAdapter() {

    // Screen and camera management
    private val camera = OrthographicCamera()
    private val viewport = FillViewport(
            translate(GAME_WIDTH),
            translate(GAME_HEIGHT),
            camera)

    // Box2D attributes
    private val world = createWorld(vec2(y = -9.8f))
    private val contacts = BreakoutContactListener()
    private val renderer = Box2DDebugRenderer()
    private var accumulator = 0f
    private var isPaused = true

    // World bodies, the essential one
    private lateinit var ball: Body
    private lateinit var paddle: Body

    private fun createBall() {
        ball = world.body(BodyDef.BodyType.DynamicBody) {
            // Radius of the ball in meters
            val radius = translate(32f)

            // Center to the game screen
            position.set(translate(GAME_WIDTH / 2),
                    translate(GAME_HEIGHT / 2))
            fixedRotation = false

            // Set this body's user data to sprite
            val texture: Texture = game.assets[Assets.BALL.path]
            userData = Sprite(texture).apply {
                setSize(radius * 2f,
                        radius * 2f)
                setOriginCenter()
            }

            circle(radius) {
                userData = ID_BALL
                restitution = 1f
                density = 0.1f
                friction = 0f
                filter {
                    categoryBits = ID_BALL
                    maskBits = ID_PADDLE or ID_WALL
                }
            }
        }
    }

    private fun createPaddle() {
        paddle = world.body(BodyDef.BodyType.DynamicBody) {
            val width = translate(256f)
            val height = translate(64f)

            // 10% above ground, and centered horizontally
            position.set(translate(GAME_WIDTH / 2),
                    translate(GAME_HEIGHT * PADDLE_HEIGHT_PERCENT))
            // Set this body's user data to a sprite
            val texture: Texture = game.assets[Assets.PADDLE.path]
            userData = Sprite(texture).apply {
                setSize(width, height)
                setOriginCenter()
            }

            box(width, height) {
                userData = ID_PADDLE
                restitution = 0.5f
                density = 0.5f
                friction = 0.4f
                filter {
                    categoryBits = ID_PADDLE
                    maskBits = ID_BALL or ID_WALL
                }
            }
        }
    }

    private fun createBricks() {
        val rows = 5
        val columns = 12
        val width = translate(72)
        val height = translate(72)
        val offsetX = (width / 2) + // <--- We add this because box2d draw from center
                (translate(GAME_WIDTH) - (columns * width)) / 2
        val offsetY = translate(GAME_HEIGHT * 0.95f)

        for (i in 0 until rows) {
            for (j in 0 until columns) {

                val x = offsetX + (j * width)
                val y = offsetY - (i * height)

                world.body(BodyDef.BodyType.StaticBody) {
                    position.set(x, y)

                    val texture: Texture = game.assets[Assets.BRICK.path]
                    userData = Sprite(texture).apply {
                        setSize(width, height)
                        setOriginCenter()
                    }

                    box(width, height) {
                        userData = ID_BRICK
                        restitution = 0.1f
                        density = 10f
                        friction = 0.4f
                        filter {
                            categoryBits = ID_BRICK
                            maskBits = ID_BALL
                        }
                    }
                }
            }
        }
    }

    // WARNING: Must be called after paddle else a lateinit error
    private fun createWalls() {
        //  Iterate SurroundingEdges and create a body for it
        for (edge in SurroundingEdges.values()) {
            world.body(BodyDef.BodyType.StaticBody) {
                // Create the shape and fixture
                edge(edge.start, edge.end) {
                    userData = if (edge.name == "FLOOR") ID_FLOOR else ID_WALL
                    density = 1f
                    restitution = 1f
                    friction = 100f

                    filter {
                        println(edge.name)
                        // Set a different category bit for
                        // FLOOR so a ball could pass through
                        if (edge.name == "FLOOR") {
                            categoryBits = ID_FLOOR
                            maskBits = ID_PADDLE
                        } else {
                            categoryBits = ID_WALL
                            maskBits = ID_BALL or ID_PADDLE
                        }
                    }
                }
            }.also { ground ->
                if (edge.name == "FLOOR") {
                    // Create a PrismaticJoint (in a Java way)
                    // LibKTX behaves in a naughty naughty way
                    val def = PrismaticJointDef().apply {
                        initialize(ground, paddle,
                                paddle.worldCenter,
                                vec2(x = 1f))
                        collideConnected = true
                    }; world.createJoint(def)
                }
            }
        }
    }

    private fun reset() {
        with(ball) {
            // Recenter the ball
            setTransform(vec2(
                    x = translate(GAME_WIDTH / 2),
                    y = translate(GAME_HEIGHT / 2)), 0f)

            // Stop it from moving
            linearVelocity = vec2(0f, 0f)
            angularVelocity = 0f

            // Create initial force for the ball
            val force = vec2(
                    x =  translate(32f) *
                            (if (Random.nextBoolean()) 1f else -1f),
                    y = translate(-512f))
            val point = vec2(
                    x = translate(GAME_WIDTH / 2),
                    y = translate(GAME_HEIGHT / 2))

            println(force)
            applyLinearImpulse(force, point, true)
        }

        with (paddle) {
            // Recenter paddle
            setTransform(vec2(
                    x = translate(GAME_WIDTH / 2),
                    y = translate(GAME_HEIGHT *
                            PADDLE_HEIGHT_PERCENT)), 0f)

            // Stop it from moving
            linearVelocity = vec2(0f, 0f)
            angularVelocity = 0f
        }

        isPaused = true // Unpause onTouchDown
    }

    override fun show() {
        // ---- SETUP WORLD BODIES ----
        createBall()
        createPaddle()
        createBricks()
        createWalls()
        reset()

        // ---- MISCELLANEOUS STUFF ----
        world.setContactListener(contacts)
        Gdx.input.inputProcessor = this
    }

    override fun render(delta: Float) {
        // Render textures here
        game.run {
            // Extract assets to render
            val background: Texture = assets[Game.TEXTURE_BG]

            // Draw using SpriteBatch
            batch.use(camera) {
                it.draw(background, 0f, 0f,
                        translate(GAME_WIDTH),
                        translate(GAME_HEIGHT))

                // Check if ball got out of bounds
                if (ball.position.y < 0) reset()

                // Draw the sprites in the position of its bodies
                val bodies = gdxArrayOf<Body>()
                world.getBodies(bodies)
                for (body in bodies) {
                    // Skip if userdata is null or not Sprite
                    if (body.userData !is Sprite) continue

                    (body.userData as Sprite).apply {
                        setOriginBasedPosition(
                                body.position.x,
                                body.position.y)
                    }.draw(it)
                }

                // Remove bricks that has been hit
                for (brick in contacts.bricksToRemove)
                    world.destroyBody(brick)
                contacts.bricksToRemove.clear()
            }
        }

        // Render the Box2D debugger
        renderer.render(world, camera.combined)

        // Make delta (from param) var by name shadowing it
        @Suppress("NAME_SHADOWING") var delta = delta
        // Set delta to zero if isPaused
        if (isPaused) delta = 0f

        // Step world code snippet (not sure what's going on here)
        accumulator += delta.coerceAtMost(0.25f)
        if (accumulator >= GameUtils.TIME_STEP) {
            accumulator -= GameUtils.TIME_STEP
            world.step(GameUtils.TIME_STEP,
                    GameUtils.VELOCITY_ITERATIONS,
                    GameUtils.POSITION_ITERATIONS)
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun touchUp(screenX: Int, screenY: Int,
                         pointer: Int, button: Int): Boolean {
        // Stop the paddle from moving
        paddle.setLinearVelocity(0f, 0f)
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int,
                           pointer: Int, button: Int): Boolean {
        if (isPaused) { isPaused = false; return true }

        // Get world space coordinates (in meters) and extract X
        val touchX = camera.unproject(vec3(
                x = screenX.toFloat(),
                y = screenY.toFloat())).x
        // Get X axis center of the screen (in meters)
        val centerX = translate(GAME_WIDTH) / 2

        // Determine whether paddle goes left or right
        val speed = 1080f * if (touchX >= centerX) 1 else -1

        // Move the paddle with created velocity
        paddle.linearVelocity = vec2(x = translate(speed))
        return true
    }

    inner class BreakoutContactListener : ContactListener {

        // Create a list of bricks to remove on game
        internal val bricksToRemove = gdxArrayOf<Body>()

        override fun beginContact(contact: Contact?) {
            contact?.run {
                // Remove brick here
                if (fixtureA.userData == ID_BRICK)
                    bricksToRemove.add(fixtureA.body)
                if (fixtureB.userData == ID_BRICK)
                    bricksToRemove.add(fixtureB.body)
            }
        }

        override fun endContact(contact: Contact?) {}
        override fun preSolve(contact: Contact?, oldManifold: Manifold?) {}
        override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}
    }

    enum class Assets(val path: String) {
        BALL("breakout/ball.png"),
        PADDLE("breakout/paddle.png"),
        BRICK("breakout/brick.png");
    }

    companion object {
        internal fun show(game: Game) {
            with (game) {
                assets.let {
                    for (asset in Assets.values())
                        game.assets.load<Texture>(asset.path)

                    it.finishLoading()
                }

                removeScreen<MainMenuScreen>()
                setScreen<BreakoutScreen>()
            }
        }

        // These variables will be used for:
        //  - Fixture IDs to determent on contact
        //  - Category bits for collision filtering
        // Read more here https://bit.ly/2VArJdX
        private const val ID_WALL: Short = 2
        private const val ID_PADDLE: Short = 4
        private const val ID_FLOOR: Short = 6
        private const val ID_BALL: Short = 8
        private const val ID_BRICK: Short = 10

        // Box2D bodies' constants
        private const val PADDLE_HEIGHT_PERCENT = 0.10f
    }
}
package wtf.liempo.pregnancy.catch

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
import wtf.liempo.pregnancy.mainmenu.MainMenuScreen
import wtf.liempo.pregnancy.utils.GameUtils
import wtf.liempo.pregnancy.utils.GameUtils.translate
import wtf.liempo.pregnancy.utils.SurroundingEdges
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.experimental.or
import kotlin.random.Random

class CatchScreen(private val game: Game):
        KtxScreen, InputAdapter() {

    // Screen and camera management
    private val camera = OrthographicCamera()
    private val viewport = FillViewport(
            translate(Game.GAME_WIDTH),
            translate(Game.GAME_HEIGHT),
            camera)

    // Box2D attributes
    private val world = createWorld(vec2(y = -9.8f))
    private val renderer = Box2DDebugRenderer()
    private val contacts = BreakoutContactListener()

    private var accumulator = 0f
    private var isPaused = true

    // World bodies, essential
    private lateinit var girl: Body

    // WARNING: Must be called after girl else a lateinit error
    private fun createWalls() {
        //  Iterate SurroundingEdges and create a body for it
        for (edge in SurroundingEdges.values()) {
            if (edge.name == "CEILING") continue

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
                            maskBits = ID_GIRL
                        } else {
                            categoryBits = ID_WALL
                            maskBits = ID_GIRL or
                                    ID_CORRECT or ID_WRONG
                        }
                    }
                }
            }.also { ground ->
                if (edge.name == "FLOOR") {
                    // Create a PrismaticJoint (in a Java way)
                    // LibKTX behaves in a naughty naughty way
                    val def = PrismaticJointDef().apply {
                        initialize(ground, girl,
                                girl.worldCenter,
                                vec2(x = 1f))
                        collideConnected = true
                    }; world.createJoint(def)
                }
            }
        }
    }

    private fun createGirl() {
        girl = world.body(BodyDef.BodyType.DynamicBody) {
            // Create dimensions, must be 1:1,
            // but sprite will be rendered as 1:2
            val width = translate(256f)
            val height = translate(256f)

            // 5% above ground, and centered horizontally
            position.set(translate(Game.GAME_WIDTH / 2),
                    translate(Game.GAME_HEIGHT * GIRL_HEIGHT_PERCENT))

            // Set this body's user data to a sprite
            val texture: Texture = game.assets[Assets.GIRL.path]
            userData = Sprite(texture).apply {
                setSize(width, height * 2)
                // setOriginCenter behaves in a very naughty way
                setOrigin(width / 2,    height / 2)
            }

            box(width, height) {
                userData = ID_GIRL
                restitution = 0.5f
                density = 0.5f
                friction = 0.4f
                filter {
                    categoryBits = ID_GIRL
                    maskBits = ID_WALL or ID_CORRECT or ID_WRONG
                }
            }
        }
    }

    override fun show() {
        createGirl()

        Timer().schedule(timerTask {

            println("creating")
            val correct = Random.nextBoolean()
            val id = if (correct) ID_CORRECT else ID_WRONG

            world.body(BodyDef.BodyType.DynamicBody) {
                val width = translate(128f)
                val height = translate(128f)

                // 20 pixels above the game, no ceiling
                position.set(
                        translate(Game.GAME_WIDTH) * Random.nextFloat(),
                        translate(Game.GAME_HEIGHT + 256f))

                // Randomize the asset
                val asset =  if (correct)
                    randomCorrectAsset()
                else randomWrongAsset()

                // Set texture to sprite
                val texture: Texture = game.assets[asset.path]
                userData = Sprite(texture).apply {
                    setSize(width, height)
                    setOriginCenter()
                }

                box(width, height) {
                    userData = id
                    restitution = 0.5f
                    density = 0.5f
                    friction = 1f
                    filter {
                        categoryBits = id
                        maskBits = ID_GIRL or ID_WALL
                    }
                }
            }
        }, 0, 1000)

        createWalls()
        world.setContactListener(contacts)
        Gdx.input.inputProcessor = this
    }

    override fun render(delta: Float) {
        // Render texture here

        game.run {
            // Extract assets to render
            val background: Texture = assets[Game.TEXTURE_BG]

            // Draw using SpriteBatch
            batch.use(camera) {
                it.draw(background, 0f, 0f,
                        translate(Game.GAME_WIDTH),
                        translate(Game.GAME_HEIGHT))



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
                        rotation = body.transform.rotation
                    }.draw(it)
                }

                // Remove bricks that has been hit
                for (brick in contacts.objectsToRemove)
                    world.destroyBody(brick)
                contacts.objectsToRemove.clear()
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
        girl.setLinearVelocity(0f, 0f)
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
        val centerX = translate(Game.GAME_WIDTH) / 2

        // Determine whether paddle goes left or right
        val speed = 1080f * if (touchX >= centerX) 1 else -1

        // Move the paddle with created velocity
        girl.linearVelocity = vec2(x = translate(speed))
        return true
    }

    inner class BreakoutContactListener : ContactListener {

        // Create a list of objects to remove on game
        internal val objectsToRemove = gdxArrayOf<Body>()

        override fun beginContact(contact: Contact?) {
            contact?.run {
                // Remove brick here
                if (fixtureA.userData == ID_WRONG || fixtureA.userData == ID_CORRECT)
                    objectsToRemove.add(fixtureA.body)
                if (fixtureB.userData == ID_WRONG || fixtureB.userData == ID_CORRECT)
                    objectsToRemove.add(fixtureB.body)
            }
        }

        override fun endContact(contact: Contact?) {}
        override fun preSolve(contact: Contact?, oldManifold: Manifold?) {}
        override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}
    }

    enum class Assets(val path: String) {
        BEER("catch/beer.png"),
        CIGARETTE("catch/cigarette.png"),
        MILK("catch/milk.png"),
        WATER("catch/water.png"),
        JUICE("catch/juice.png"),
        GIRL("catch/girl.png");
    }

    companion object {

        private fun randomCorrectAsset(): Assets =
            listOf(Assets.MILK, Assets.WATER, Assets.JUICE).random()
        private fun randomWrongAsset(): Assets =
                listOf(Assets.CIGARETTE, Assets.BEER).random()

        internal fun show(game: Game) {
            with (game) {
                assets.let {
                    for (asset in Assets.values())
                        game.assets.load<Texture>(asset.path)
                    it.finishLoading()
                }

                removeScreen<MainMenuScreen>()
                setScreen<CatchScreen>()
            }
        }

        // These variables will be used for:
        //  - Fixture IDs to determent on contact
        //  - Category bits for collision filtering
        // Read more here https://bit.ly/2VArJdX
        private const val ID_WALL: Short = 2
        private const val ID_GIRL: Short = 4
        private const val ID_FLOOR: Short = 6
        private const val ID_CORRECT: Short = 8
        private const val ID_WRONG: Short = 10

        // Box2D bodies' constants
        private const val GIRL_HEIGHT_PERCENT = 0.10f
    }
}
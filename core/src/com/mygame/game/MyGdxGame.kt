package com.mygame.game

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.*

class MyGdxGame : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch
    internal lateinit var img: Texture
    internal val engine = Engine()
    private lateinit var injector: Injector

    override fun create() {
        batch = SpriteBatch()
        img = Texture("badlogic.jpg")

        injector = Guice.createInjector(GameModule(this))
        injector.getInstance(Systems::class.java).list.forEach { systemType ->
            val system = injector.getInstance(systemType)
            engine.addSystem(system)
        }

        createEntities()
    }

    private fun createEntities() {
        val world = injector.getInstance(World::class.java)

        engine.addEntity(Entity().apply {
            add(TextureComponent(img))
            add(TransformComponent(Vector2(2f, 2f)))
            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })

            body.createFixture(PolygonShape().apply {
                setAsBox(img.width.pixelsToUnits / 2, img.height.pixelsToUnits / 2)
            }, 1.0f)
        })
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        engine.update(Gdx.graphics.deltaTime)
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}

class PhysicsDebugSystem @Inject constructor(
        private val world: World,
        private val camera: OrthographicCamera
) : EntitySystem() {
    private val renderer = Box2DDebugRenderer()

    override fun update(deltaTime: Float) {
        renderer.render(world, camera.combined)
    }
}

class PhysicsSystem @Inject constructor(private val world: World) : EntitySystem() {
    private var accumulator = 0f

    companion object {
        private val TIME_STEP = 1.0f / 300f
        private val VELOCITY_ITERATIONS = 6
        private val POSITION_ITERATIONS = 2
    }

    override fun update(deltaTime: Float) {
        val frameTime = Math.min(deltaTime, 0.25f)
        accumulator += frameTime
        while (accumulator > TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
            accumulator -= TIME_STEP
        }
    }
}

class RenderingSystem @Inject constructor(
        private val batch: SpriteBatch,
        private val camera: OrthographicCamera
): IteratingSystem(Family.all(TransformComponent::class.java, TextureComponent::class.java).get()) {

    override fun update(deltaTime: Float) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val img = entity.texture.texture
        val pos = entity.transform.position
        batch.draw(img, pos.x, pos.y, img.width.pixelsToUnits, img.height.pixelsToUnits)
    }
}

class GameModule(private val myGdxGame: MyGdxGame) : Module {
    override fun configure(binder: Binder) {
        binder.bind(SpriteBatch::class.java).toInstance(myGdxGame.batch)

    }

    @Provides @Singleton
    fun systems(): Systems = Systems(listOf(
            PhysicsSystem::class.java,
            RenderingSystem::class.java,
            PhysicsDebugSystem::class.java
    ))

    @Provides @Singleton
    fun camera(): OrthographicCamera {
        val viewportWidth = Gdx.graphics.width.pixelsToUnits
        val viewportHeight = Gdx.graphics.height.pixelsToUnits

        return OrthographicCamera(viewportWidth, viewportHeight).apply {
            position.set(viewportWidth / 2f, viewportHeight / 2f, 0f)
            update()
        }
    }

    @Provides @Singleton
    fun world(): World {
        Box2D.init()
        return World(Vector2(0f, -9.81f), true)
    }
}

data class Systems(val list: List<Class<out EntitySystem>>)

val Int.pixelsToUnits: Float
        get() = this / 32f

/*
val Float.pixelsToUnits: Float
    get() = this / 32f*/

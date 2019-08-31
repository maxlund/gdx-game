package com.mygame.game

import com.badlogic.ashley.core.*
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.google.inject.*
import com.mygame.game.components.*
import com.mygame.game.utils.SystemsContainer
import com.mygame.game.utils.pixelsToUnits

class MyGdxGame : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch
    internal val engine = Engine()
    private lateinit var injector: Injector

    companion object {
        internal lateinit var img: Texture
    }

    override fun create() {
        batch = SpriteBatch()
        img = Texture("badlogic.jpg")
        injector = Guice.createInjector(GameModule(this))
        injector.getInstance(SystemsContainer::class.java).list.forEach { systemType ->
            val system = injector.getInstance(systemType)
            engine.addSystem(system)
        }

        createEntities()

        Gdx.input.inputProcessor = injector.getInstance(MyInputAdapter::class.java)
    }

    private fun createEntities() {
        val world = injector.getInstance(World::class.java)

        engine.addEntity(Entity().apply {
            add(TextureComponent(img))
            add(TransformComponent(Vector2(5f, 15f)))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })

            body.createFixture(PolygonShape().apply {
                setAsBox(img.width.pixelsToUnits / 2, img.height.pixelsToUnits / 2)
            }, 1.0f)

            body.setTransform(transformComp.position, 0f)
            add(PhysicsComponent(body))
        })

        engine.addEntity(Entity().apply {
            add(TransformComponent(Vector2(0f, 0f)))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.StaticBody
            })

            body.createFixture(PolygonShape().apply {
                setAsBox(15f, 1f)
            }, 1.0f)

            body.setTransform(transformComp.position, 0f)
            add(PhysicsComponent(body))
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

class MyInputAdapter @Inject constructor(
        private val camera: OrthographicCamera,
        private val engine: Engine,
        private val world: World
) : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val worldPosition = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        engine.addEntity(Entity().apply {
            add(TextureRegionComponent(TextureRegion(MyGdxGame.img)))
            add(TransformComponent(Vector2(worldPosition.x, worldPosition.y), 0f, 0.25f))

            val body = world.createBody(BodyDef().apply {
                type = BodyDef.BodyType.DynamicBody
            })

            body.createFixture(PolygonShape().apply {
                setAsBox(1f, 1f)
            }, 1.0f)

            body.setTransform(transformComp.position, 0f)
            add(PhysicsComponent(body))
        })

        return true
    }
}

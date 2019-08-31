package com.mygame.game

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.World
import com.google.inject.Binder
import com.google.inject.Module
import com.google.inject.Provides
import com.google.inject.Singleton
import com.mygame.game.systems.PhysicsDebugSystem
import com.mygame.game.systems.PhysicsSynchronizationSystem
import com.mygame.game.systems.PhysicsSystem
import com.mygame.game.systems.RenderingSystem
import com.mygame.game.utils.SystemsContainer
import com.mygame.game.utils.pixelsToUnits

class GameModule(private val myGdxGame: MyGdxGame) : Module {
    override fun configure(binder: Binder) {
        binder.requireAtInjectOnConstructors()
        binder.requireExactBindingAnnotations()
        binder.bind(SpriteBatch::class.java).toInstance(myGdxGame.batch)
    }

    @Provides
    @Singleton
    fun systems(): SystemsContainer = SystemsContainer(listOf(
            PhysicsSystem::class.java,
            PhysicsSynchronizationSystem::class.java,
            RenderingSystem::class.java,
            PhysicsDebugSystem::class.java
    ))

    @Provides
    @Singleton
    fun camera(): OrthographicCamera {
        val viewportWidth = Gdx.graphics.width.pixelsToUnits
        val viewportHeight = Gdx.graphics.height.pixelsToUnits

        return OrthographicCamera(viewportWidth, viewportHeight).apply {
            position.set(viewportWidth / 2f, viewportHeight / 2f, 0f)
            update()
        }
    }

    @Provides
    @Singleton
    fun world(): World {
        Box2D.init()
        return World(Vector2(0f, -9.81f), true)
    }

    @Provides
    @Singleton
    fun engine(): Engine = myGdxGame.engine
}
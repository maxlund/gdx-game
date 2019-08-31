package com.mygame.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.google.inject.Inject
import com.mygame.game.components.PhysicsComponent
import com.mygame.game.components.TransformComponent
import com.mygame.game.components.physicsComp
import com.mygame.game.components.transformComp

class PhysicsSynchronizationSystem @Inject constructor() : IteratingSystem(Family.all(TransformComponent::class.java, PhysicsComponent::class.java).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        entity.transformComp.position.set(entity.physicsComp.body.position)
        entity.transformComp.angleRadian = entity.physicsComp.body.angle
    }
}
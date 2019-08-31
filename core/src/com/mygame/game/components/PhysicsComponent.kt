package com.mygame.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body

class PhysicsComponent(val body: Body) : Component {
    companion object : ComponentResolver<PhysicsComponent>(PhysicsComponent::class.java)
}

val Entity.physicsComp: PhysicsComponent
    get() = PhysicsComponent[this]

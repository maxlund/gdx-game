package com.mygame.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2

class TransformComponent(val position: Vector2, var angleRadian: Float, val scale: Float) : Component {
    constructor(position: Vector2) : this(position, 0f, 1f)
    companion object : ComponentResolver<TransformComponent>(TransformComponent::class.java)
}

val Entity.transformComp: TransformComponent
    get() = TransformComponent[this]

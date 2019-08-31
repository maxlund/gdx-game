package com.mygame.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Texture

class TextureComponent(val texture: Texture) : Component {
    companion object : ComponentResolver<TextureComponent>(TextureComponent::class.java)
}

val Entity.textureComp: TextureComponent
    get() = TextureComponent[this]

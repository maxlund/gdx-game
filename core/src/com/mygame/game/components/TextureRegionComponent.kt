package com.mygame.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.TextureRegion

class TextureRegionComponent(val textureRegion: TextureRegion) : Component {
    companion object : ComponentResolver<TextureRegionComponent>(TextureRegionComponent::class.java)
}

val Entity.textureRegionComp: TextureRegionComponent
    get() = TextureRegionComponent[this]

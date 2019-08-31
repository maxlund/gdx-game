package com.mygame.game.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.google.inject.Inject
import com.mygame.game.components.*
import com.mygame.game.utils.pixelsToUnits
import com.mygame.game.utils.toDegrees

class RenderingSystem @Inject constructor(
        private val batch: SpriteBatch,
        private val camera: OrthographicCamera
) : IteratingSystem(Family
        .all(TransformComponent::class.java)
        .one(TextureComponent::class.java, TextureRegionComponent::class.java)
        .get()) {

    override fun update(deltaTime: Float) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pos = entity.transformComp.position

        entity.tryGet(TextureComponent)?.let { textureComp ->
            val img = textureComp.texture
            batch.draw(img,
                    pos.x - img.width.pixelsToUnits / 2f,
                    pos.y - img.height.pixelsToUnits / 2f,
                    img.width.pixelsToUnits, img.height.pixelsToUnits)
        }

        entity.tryGet(TextureRegionComponent)?.let { textureRegionComp ->
            val img = textureRegionComp.textureRegion
            val width = img.regionWidth.pixelsToUnits
            val height = img.regionHeight.pixelsToUnits
            val scale = entity.transformComp.scale
            batch.draw(img,
                    pos.x - width / 2f, pos.y - height / 2f,
                    width / 2f, height / 2f,
                    width, height,
                    scale, scale,
                    entity.transformComp.angleRadian.toDegrees
            )
        }
    }
}
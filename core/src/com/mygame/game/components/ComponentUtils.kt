package com.mygame.game.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

open class ComponentResolver<T: Component>(componentClass: Class<T>) {
    val mapper: ComponentMapper<T> = ComponentMapper.getFor(componentClass)
    operator fun get(entity: Entity): T = mapper.get(entity)
}

fun <T: Component> Entity.tryGet(componentResolver: ComponentResolver<T>) : T? {
    return componentResolver.mapper.get(this)
}
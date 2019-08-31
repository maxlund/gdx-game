package com.mygame.game.utils

import com.badlogic.ashley.core.EntitySystem

data class SystemsContainer(val list: List<Class<out EntitySystem>>)
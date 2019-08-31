package com.mygame.game.utils

import com.badlogic.gdx.math.MathUtils

val Int.pixelsToUnits: Float
    get() = this / 32f

val Float.toDegrees: Float
    get() = this * MathUtils.radiansToDegrees
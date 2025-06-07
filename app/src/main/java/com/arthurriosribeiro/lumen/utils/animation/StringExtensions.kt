package com.arthurriosribeiro.lumen.utils.animation

fun String?.orDash() : String {
    return if (this.isNullOrBlank()) "-" else this
}
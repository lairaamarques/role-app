package com.example.projetorole.backend.services

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object DistanceCalculator {

    fun calculate(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(toLat - fromLat)
        val dLon = Math.toRadians(toLon - fromLon)

        val lat1 = Math.toRadians(fromLat)
        val lat2 = Math.toRadians(toLat)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                sin(dLon / 2) * sin(dLon / 2) * cos(lat1) * cos(lat2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c
    }
}
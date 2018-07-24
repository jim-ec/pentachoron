/*
 *  Created by Jim Eckerlein on 7/24/18 12:35 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/24/18 12:35 PM
 */

package io.jim.tesserapp.geometry

import kotlin.math.sqrt

/**
 * Construct a regular Pentachoron, with its three dimensional base cell
 * being a regular Tetrahedron with a radius of one, with its base face lying in the xy plane
 * centered around the origin.
 */
@Suppress("unused")
fun pentachoron() = listOf(
        Position(0.0, 0.0, 1.0, 0.0),
        Position(sqrt(3.0) / 2.0, 0.0, -1.0 / 2.0, 0.0),
        Position(-sqrt(3.0) / 2.0, 0.0, -1.0 / 2.0, 0.0),
        Position(0.0, sqrt(2.0), 0.0, 0.0),
        Position(0.0, sqrt(2.0) / 4.0, 0.0, sqrt(30.0) / 4))

/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.geometry

fun quadrilateral(
        a: Position,
        b: Position,
        c: Position,
        d: Position
) = listOf(
        Line(a, b),
        Line(b, c),
        Line(c, d),
        Line(d, a)
)

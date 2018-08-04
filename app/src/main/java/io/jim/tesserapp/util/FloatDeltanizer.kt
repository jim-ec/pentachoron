/*
 *  Created by Jim Eckerlein on 8/4/18 10:37 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 8/4/18 10:36 PM
 */

package io.jim.tesserapp.util

import kotlin.properties.Delegates

class FloatDeltanizer(initializer: Float) {
    
    private var old = initializer
    
    var new by Delegates.observable(initializer) { _, oldValue, _ ->
        old = oldValue
    }
    
    val delta: Float
        get() = new - old
    
}

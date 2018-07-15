/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.graphics.gl

import android.opengl.GLES20

/**
 * A GL VBO handle, conveniently provides scoped binding to a GL target.
 *
 * @property target The GL target this buffer is bound to, such as [GLES20.GL_ARRAY_BUFFER].
 */
class Vbo(
        val target: Int
) {
    
    /**
     * The actual handle retrieved from GL.
     */
    val handle = resultCode { GLES20.glGenBuffers(1, resultCode) }
    
    /**
     * Calls [f] while this buffer is bound to its [target].
     * Unbinds any buffer which is currently bound to [target].
     * After [f] has run,  is unbound (0 is bound to [target]).
     */
    inline fun bound(crossinline f: () -> Unit) {
        GLES20.glBindBuffer(target, handle)
        f()
        GLES20.glBindBuffer(target, 0)
    }
    
}

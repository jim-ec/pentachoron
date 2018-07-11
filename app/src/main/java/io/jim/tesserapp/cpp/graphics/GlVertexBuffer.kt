package io.jim.tesserapp.cpp.graphics

import android.opengl.GLES20
import io.jim.tesserapp.cpp.ATTRIBUTE_COUNTS
import io.jim.tesserapp.cpp.Vertex
import io.jim.tesserapp.util.allocateNativeFloatMemory

/**
 * GL VAO.
 *
 * @property drawMode Mode how to draw the vertices, e.g. [GLES20.GL_TRIANGLE_STRIP].
 */
class GlVertexBuffer(
        val drawMode: Int,
        val instructVertexAttributePointers: () -> Unit
) {
    
    val buffer = GlBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_STATIC_DRAW)
    
    fun draw(vertices: List<Vertex>) {
        val memory = allocateNativeFloatMemory(vertices.size * ATTRIBUTE_COUNTS * 4).asFloatBuffer()
        
        vertices.forEach { (position, color) ->
            memory.put(position.x.toFloat())
            memory.put(position.y.toFloat())
            memory.put(position.z.toFloat())
            memory.put(1f)
    
            memory.put(color.red)
            memory.put(color.green)
            memory.put(color.blue)
            memory.put(1f)
        }
    
        memory.rewind()
        
        buffer.bound {
            instructVertexAttributePointers()
            
            buffer.allocate(
                    vertices.size * ATTRIBUTE_COUNTS,
                    memory
            )
    
            GLES20.glDrawArrays(
                    drawMode,
                    0,
                    vertices.size * ATTRIBUTE_COUNTS)
    
            GlException.check("Draw vertex memory")
        }
    }
    
}

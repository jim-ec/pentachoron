package io.jim.tesserapp.cpp.graphics

import android.opengl.GLES20
import io.jim.tesserapp.cpp.ATTRIBUTE_COUNTS
import io.jim.tesserapp.cpp.InputStreamMemory
import io.jim.tesserapp.cpp.Vertex
import io.jim.tesserapp.graphics.blue
import io.jim.tesserapp.graphics.green
import io.jim.tesserapp.graphics.red

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
        val memory = InputStreamMemory(100, ATTRIBUTE_COUNTS)
        
        vertices.forEach { (position, color) ->
            memory.record {
                memory.write(position.x, position.y, position.z, 1.0)
                memory.write(color.red, color.green, color.blue, 1f)
            }
        }
        
        buffer.bound {
            instructVertexAttributePointers()
            
            buffer.allocate(
                    memory.writtenVectorCounts,
                    memory.floatMemory
            )
    
            GLES20.glDrawArrays(
                    drawMode,
                    0,
                    memory.writtenElementCounts)
    
            GlException.check("Draw vertex memory")
        }
    }
    
}

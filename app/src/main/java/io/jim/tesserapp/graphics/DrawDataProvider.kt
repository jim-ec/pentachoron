package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.SymbolicColor
import io.jim.tesserapp.math.vector.VectorN
import io.jim.tesserapp.util.InputStreamMemory

/**
 * Gathers all data necessary for drawing geometry.
 * This includes vertex data as well as model matrices.
 *
 * This geometry memory is only responsible for raw data, without incorporating with GL at all.
 */
class DrawDataProvider {
    
    /**
     * Vertex memory.
     * Memory data is updated automatically upon geometrical change.
     */
    val vertexMemory = InputStreamMemory(100, VertexBuffer.ATTRIBUTE_COUNTS)
    
    /**
     * Compute new model matrices and rewrite the vertex memory.
     */
    fun updateVertices(
            geometries: Iterable<Geometry>,
            visualizer: (Geometry, (position: VectorN, color: SymbolicColor) -> Unit) -> Unit,
            colorResolver: (SymbolicColor) -> Int
    ) {
        
        vertexMemory.rewind()
        
        geometries.forEach { geometry ->
    
            visualizer(geometry) { position, color ->
    
                vertexMemory.record {
        
                    vertexMemory.write(position.x, position.y, position.z, 1.0)
                    
                    // Resolve symbolic geometry color into an actual integer and write
                    // that into the memory:
                    with(colorResolver(color)) {
                        vertexMemory.write(red, green, blue, 1f)
                    }
                    
                }
            }
        }
    }
    
}

package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.VertexBuffer
import io.jim.tesserapp.graphics.blue
import io.jim.tesserapp.graphics.green
import io.jim.tesserapp.graphics.red
import io.jim.tesserapp.util.InputStreamMemory

fun List<Vertex>.draw(vertexBuffer: VertexBuffer) {
    val memory = InputStreamMemory(100, VertexBuffer.ATTRIBUTE_COUNTS)
    forEach { (position, color) ->
        memory.record {
            memory.write(position.x, position.y, position.z, 1.0)
            memory.write(color.red, color.green, color.blue, 1f)
        }
    }
    vertexBuffer.draw(memory, memory.writtenElementCounts)
}
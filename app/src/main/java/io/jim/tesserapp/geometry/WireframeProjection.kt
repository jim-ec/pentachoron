package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.Vector3dh
import io.jim.tesserapp.math.vector.Vector4dh


/**
 * Project [geometry] down to a 3d wireframe.
 * @param f Called for each wireframe vertex, i.e. its position and color.
 */
inline fun wireframeProjection(
        geometry: Geometry,
        crossinline f: (position: Vector3dh, color: Int) -> Unit
) {
    geometry.transform.computeModelMatrix()

    val transformedPosition = Vector4dh()
    val homogeneous = Vector3dh()

    geometry.lines.forEach { line ->
        line.forEachPosition { position ->

            // Apply 4-dimensional model matrix to 4d point:
            transformedPosition.multiplication(position, geometry.transform.modelMatrix)

            // Project vector down to a 3d volume:
            transformedPosition /= transformedPosition.q + Geometry.Q_PROJECTION_VOLUME

            homogeneous.load(transformedPosition.x, transformedPosition.y, transformedPosition.z)

            f(homogeneous, line.color)
        }
    }
}

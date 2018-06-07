package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry

/**
 * Data in that structure is owned by both, the render thread and ui thread (or any other).
 * Therefore, any access to it must be synchronized.
 *
 * @property featuredGeometry Featured geometry.
 * @property camera Camera related preferences.
 */
data class SharedRenderData(val featuredGeometry: Geometry, val camera: Camera = Camera())

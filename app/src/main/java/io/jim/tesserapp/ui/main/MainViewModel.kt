/*
 *  Created by Jim Eckerlein on 7/23/18 9:34 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/23/18 9:28 AM
 */

package io.jim.tesserapp.ui.main

import androidx.lifecycle.ViewModel
import io.jim.tesserapp.geometry.*
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.util.MutableLiveDataNonNull

/**
 * Stores persistent data related to the main activity,
 * inter alia transform of the featured geometry.
 */
class MainViewModel : ViewModel() {
    
    var selectedAxis = MutableLiveDataNonNull(SelectedAxis.X)
    
    var transformMode = MutableLiveDataNonNull(TransformMode.ROTATE)
    
    /**
     * List containing all geometries.
     *
     * The geometries are kept inside a hash-set instead of in a simply list because
     * geometries added multiple times should not result in the same geometry stored
     * more once in the set.
     *
     * This happens because this set outlives the UI, and the UI may initially add
     * geometry to the set each time the UI is set up.
     */
    val geometries = HashSet<Geometry>()
    
    /**
     * Create geometry, replacing any previously existing one.
     */
    fun createGeometries(
            featuredGeometryName: String,
            enableGrid: Boolean,
            primaryColor: Color,
            accentColor: Color,
            xColor: Color,
            yColor: Color,
            zColor: Color
    ) {
        // Reset any previous state:
        geometries.clear()
        
        geometries += Geometry(
                name = featuredGeometryName,
                isFourDimensional = true,
                onTransformUpdate = {
                    synchronized(this) {
                        Transform(
                                rotationX = rotationX.value * Math.PI,
                                rotationY = rotationY.value * Math.PI,
                                rotationZ = rotationZ.value * Math.PI,
                                rotationQ = rotationQ.value * Math.PI,
                                translationX = translationX.value,
                                translationY = translationY.value,
                                translationZ = translationZ.value,
                                translationQ = translationQ.value
                        )
                    }
                },
                lines = extruded(
                        positions = extruded(
                                quadrilateral(
                                        Position(2.0, 2.0, 2.0, 0.0),
                                        Position(-2.0, 2.0, 2.0, 0.0),
                                        Position(-2.0, -2.0, 2.0, 0.0),
                                        Position(2.0, -2.0, 2.0, 0.0)),
                                direction = Position(0.0, 0.0, -4.0, 0.0)
                        ),
                        direction = Position(0.0, 0.0, 0.0, 2.0)
                ),
                color = accentColor
        )
        
        geometries += Geometry("X-Axis",
                lines = listOf(Line(Position(0.0, 0.0, 0.0, 0.0), Position(1.0, 0.0, 0.0, 0.0))),
                color = xColor)
        
        geometries += Geometry("Y-Axis",
                lines = listOf(Line(Position(0.0, 0.0, 0.0, 0.0), Position(0.0, 1.0, 0.0, 0.0))),
                color = yColor)
        
        geometries += Geometry("Z-Axis",
                lines = listOf(Line(Position(0.0, 0.0, 0.0, 0.0), Position(0.0, 0.0, 1.0, 0.0))),
                color = zColor)
        
        if (enableGrid) {
            geometries += Geometry(
                    name = "Grid",
                    lines = gridOmitAxisIndicator(),
                    onTransformUpdate = { Transform() },
                    color = primaryColor
            )
        }
    }
    
    /**
     * X rotation, in units of PI.
     */
    val rotationX = MutableLiveDataNonNull(0.0)
    
    /**
     * Y rotation, in units of PI.
     */
    val rotationY = MutableLiveDataNonNull(0.0)
    
    /**
     * Z rotation, in units of PI.
     */
    val rotationZ = MutableLiveDataNonNull(0.0)
    
    /**
     * Q rotation, in units of PI.
     */
    val rotationQ = MutableLiveDataNonNull(0.0)
    
    /**
     * X translation.
     */
    val translationX = MutableLiveDataNonNull(0.0)
    
    /**
     * Y translation.
     */
    val translationY = MutableLiveDataNonNull(0.0)
    
    /**
     * Z translation.
     */
    val translationZ = MutableLiveDataNonNull(0.0)
    
    /**
     * Q translation.
     */
    val translationQ = MutableLiveDataNonNull(4.0)
    
    /**
     * Camera distance.
     */
    val cameraDistance = MutableLiveDataNonNull(5.0)
    
    /**
     * Rotation on the horizontal orbit.
     * This is the base rotation.
     */
    val horizontalCameraRotation = MutableLiveDataNonNull(Math.PI / 3.0)
    
    /**
     * Rotation on the vertical orbit.
     * This is the secondary rotation.
     */
    val verticalCameraRotation = MutableLiveDataNonNull(-Math.PI / 8.0)
    
    val cameraFovX = MutableLiveDataNonNull(60.0)
    
    enum class SelectedAxis {
        X, Y, Z, Q
    }
    
    enum class TransformMode {
        ROTATE, TRANSLATE
    }
    
}

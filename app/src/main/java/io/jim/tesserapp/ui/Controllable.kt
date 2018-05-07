package io.jim.tesserapp.ui

/**
 * The set of GUI-controls set values through this interface.
 */
interface Controllable {

    /**
     * Rotation around the x-axis.
     */
    var rotationX: Float

    /**
     * Rotation around the y-axis.
     */
    var rotationY: Float

    /**
     * Rotation around the z-axis.
     */
    var rotationZ: Float

    /**
     * Rotation around on the q-x plane.
     */
    var rotationQ: Float

    /**
     * Translation along the x-axis.
     */
    var translationX: Float

    /**
     * Translation along the y-axis.
     */
    var translationY: Float

    /**
     * Translation along the q-axis.
     */
    var translationQ: Float

    /**
     * Translation along the z-axis.
     */
    var translationZ: Float

    /**
     * Camera distance.
     */
    var cameraDistance: Float

    /**
     * Option whether to render the base-grid.
     */
    var renderGrid: Boolean
}

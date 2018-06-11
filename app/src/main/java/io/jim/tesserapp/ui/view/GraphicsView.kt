package io.jim.tesserapp.ui.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.axis
import io.jim.tesserapp.geometry.grid
import io.jim.tesserapp.graphics.themedColorInt
import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.math.vector.Vector4dh
import io.jim.tesserapp.rendering.Renderer

/**
 * A view capable of rendering 3D geometry.
 */
class GraphicsView : GLSurfaceView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val viewModel = (context as MainActivity).viewModel

    @PublishedApi
    internal val renderer = Renderer(
            context as MainActivity,
            resources.displayMetrics.xdpi.toDouble()
    )

    private val touchStartPosition = Vector3d(0.0, 0.0, 0.0)
    private var touchStartTime = 0L

    private val grid =
            Geometry().apply {
                name = "Grid"
                baseColor = themedColorInt(context, R.attr.colorGrid)
                grid()
            }

    companion object {
        private const val CLICK_TIME_MS = 100L
        private const val TOUCH_ROTATION_SENSITIVITY = 0.008
    }

    /**
     * Enable or disable grid rendering.
     */
    var renderGrid = true
        set(value) {
            if (value) {
                queueEvent {
                    renderer.addGeometry(grid)
                }
            } else {
                queueEvent {
                    renderer.removeGeometry(grid)
                }
            }
            field = value
        }

    init {

        // Setup renderer:
        setEGLContextClientVersion(3)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_CONTINUOUSLY

        // Create axis:
        queueEvent {
            renderer.addGeometry(Geometry().apply {
                name = "Axis"
                axis(
                        xAxisColor = themedColorInt(context, R.attr.colorAxisX),
                        yAxisColor = themedColorInt(context, R.attr.colorAxisY),
                        zAxisColor = themedColorInt(context, R.attr.colorAxisZ)
                )
            })
        }
    }

    /**
     * Handles camera orbit position upon touch events.
     */
    override fun onTouchEvent(event: MotionEvent?) =
            if (null == event) false
            else when {
                event.action == ACTION_DOWN -> {
                    touchStartPosition.x = event.x.toDouble()
                    touchStartPosition.y = event.y.toDouble()
                    touchStartTime = System.currentTimeMillis()
                    true
                }
                event.action == ACTION_MOVE -> {
                    val dx = event.x - touchStartPosition.x
                    val dy = event.y - touchStartPosition.y

                    viewModel.horizontalCameraRotation.value += dx * TOUCH_ROTATION_SENSITIVITY
                    viewModel.verticalCameraRotation.value -= dy * TOUCH_ROTATION_SENSITIVITY

                    touchStartPosition.x = event.x.toDouble()
                    touchStartPosition.y = event.y.toDouble()
                    true
                }
                event.action == ACTION_UP
                        && System.currentTimeMillis() - touchStartTime < CLICK_TIME_MS -> {
                    performClick()
                    true
                }
                else -> false
            }

    /**
     * Clicks rewind camera position to default.
     */
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    /**
     * Invokes [f] on the render thread, so it can safely access the featured geometry from
     * another thread.
     *
     * Note that each single call allocates a new runnable to be queued to the render thread,
     * so it shouldn't be called frequently to reduce gc-pressure.
     *
     * @param f
     * Function called on the render thread.
     * The featured geometry is passed to it.
     */
    inline fun queueEventOnFeaturedGeometry(crossinline f: (featuredGeometry: Geometry) -> Unit) {
        queueEvent {
            f(renderer.featuredGeometry)
        }
    }

    /**
     * Saves the featured geometry's transform.
     *
     * @property rotationMatrix
     * The whole matrix must be preserved instead of just the x-rotation, y-rotation etc,
     * because rotation is pre-multiplied.
     */
    class SavedState(
            parcelable: Parcelable,
            val translation: Vector4dh,
            val rotationMatrix: Matrix
    ) : BaseSavedState(parcelable)

    /**
     * Stores the featured geometry's transform.
     *
     * **This is actually not thread-safe, because the featured geometry lives on the
     * render thread.**
     */
    override fun onSaveInstanceState() =
            SavedState(
                    super.onSaveInstanceState(),
                    renderer.featuredGeometry.transform.translation,
                    renderer.featuredGeometry.transform.rotationMatrix)

    /**
     * Extracts the featured geometry's transform.
     */
    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)

        if (state !is SavedState) return

        queueEventOnFeaturedGeometry {
            // Initialize transform of featured geometry:
            it.transform.translateX(state.translation.x)
            it.transform.translateY(state.translation.y)
            it.transform.translateZ(state.translation.z)
            it.transform.rotationMatrix.swap(state.rotationMatrix)
        }
    }

}

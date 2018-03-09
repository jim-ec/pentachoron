package io.jim.tesserapp.gui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import io.jim.tesserapp.math.Triangle
import io.jim.tesserapp.math.Vector

class CoordinateSystem(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply { color = Color.RED }
    private val triangle = Triangle(Vector(0.0, 1.0), Vector(1.0, 0.0), Vector(-1.0, 0.0))

    private fun transform(v: Vector): Vector {
        return Vector(v.x, -v.y) * 100.0 + Vector(width / 2.0, height / 2.0)
    }

    private fun tx(v: Vector) = transform(v).x.toFloat()
    private fun ty(v: Vector) = transform(v).y.toFloat()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas === null) return

        val path = Path()
                .apply { moveTo(tx(triangle.first), ty(triangle.first)) }
                .apply { lineTo(tx(triangle.second), ty(triangle.second)) }
                .apply { lineTo(tx(triangle.third), ty(triangle.third)) }

        canvas.drawPath(path, paint)
    }

}

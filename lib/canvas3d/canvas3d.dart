import 'package:flutter/material.dart';
import 'package:tesserapp/canvas3d/painter.dart';
import 'package:tesserapp/geometry/drawable.dart';
import 'package:tesserapp/geometry/matrix.dart';
import 'package:tesserapp/geometry/vector.dart';

class Canvas3d extends StatelessWidget {
  final Matrix modelMatrix;

  final Drawable drawable;

  /// Color of geometry.
  final Color color;

  /// Color of the outline.
  final Color outlineColor;

  /// Direction of global light.
  /// A vector longer than 1.0 increases light intensity.
  final Vector lightDirection;

  final bool printDrawStats;
  final TextStyle drawStatsStyle;
  
  Canvas3d({
    Key key,
    @required this.modelMatrix,
    Drawable drawableBuilder(),
    @required this.outlineColor,
    @required this.lightDirection,
    @required this.color,
    this.printDrawStats = false,
    this.drawStatsStyle,
  })  : drawable = drawableBuilder(),
        super(key: key) {
    if(printDrawStats) {
      assert(drawStatsStyle != null, "When printing draw stats, drawStatsStyle must not be null");
    }
  }

  @override
  Widget build(BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: Canvas3dPainter(this),
        ),
      );
}

import 'package:flutter/material.dart';
import 'package:tesserapp/canvas3d/painter.dart';
import 'package:tesserapp/geometry/matrix.dart';
import 'package:tesserapp/geometry/polygon.dart';
import 'package:tesserapp/geometry/vector.dart';

class Canvas3d extends StatelessWidget {
  final Matrix globalTransform;

  /// List of polygons to be drawn.
  final Iterable<Polygon> polygons;

  /// Color of geometry.
  final Color color;

  /// Color of the outline.
  final Color outlineColor;

  /// Direction of global light.
  /// A vector longer than 1.0 increases light intensity.
  final Vector lightDirection;

  Canvas3d({
    Key key,
    @required this.globalTransform,
    Iterable<Polygon> polygonBuilder(),
    @required this.outlineColor,
    @required this.lightDirection,
    @required this.color,
  })  : polygons = polygonBuilder(),
        super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: Canvas3dPainter(this),
        ),
      );
}

import 'package:flutter/material.dart';
import 'package:tesserapp/canvas3d/geometry.dart';
import 'package:tesserapp/canvas3d/painter.dart';
import 'package:tesserapp/geometry/vector.dart';

class Canvas3d extends StatelessWidget {
  /// Camera distance from the origin along the y axis.
  final double cameraDistance;

  /// List of geometry to be drawn.
  final Iterable<Geometry> geometries;

  /// Color of the outline, if drawn.
  final Color outlineColor;

  /// Direction of global light.
  /// A vector longer than 1.0 increases light intensity.
  final Vector lightDirection;

  Canvas3d({
    Key key,
    @required this.cameraDistance,
    @required this.geometries,
    @required this.outlineColor,
    @required this.lightDirection,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: Canvas3dPainter(this),
        ),
      );
}

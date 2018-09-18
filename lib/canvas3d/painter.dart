import 'package:flutter/material.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/canvas3d/processing_polygon.dart';
import 'package:vector_math/vector_math_64.dart' show makeViewMatrix;

class Canvas3dPainter extends CustomPainter {
  final Canvas3d canvas3d;

  final Paint outlinePaint;

  Canvas3dPainter(this.canvas3d)
      : outlinePaint = Paint()
          ..color = canvas3d.outlineColor
          ..style = PaintingStyle.stroke
          ..strokeWidth = 0.01
          ..strokeJoin = StrokeJoin.round;

  @override
  bool shouldRepaint(final CustomPainter oldDelegate) => true;

  @override
  void paint(final Canvas canvas, final Size size) {
    final aspectRatio = size.width / size.height;

    // Transform canvas into viewport space:
    canvas
      ..clipRect(Rect.fromLTWH(0.0, 0.0, size.width, size.height))
      ..translate(size.width / 2.0, size.height / 2.0)
      ..scale(-size.width / 2.0, -size.height / 2.0);

    // Scale canvas according to current orientation, to maintain the
    // same geometry scaling when rotating the viewport.
    if (size.width < size.height) {
      // Portrait oriented canvas:
      canvas.scale(1.0, aspectRatio);
    } else {
      // Landscape oriented canvas:
      canvas.scale(1.0 / aspectRatio, 1.0);
    }

    final view = makeViewMatrix(
      canvas3d.cameraPosition.eye,
      canvas3d.cameraPosition.focus,
      canvas3d.cameraPosition.up,
    );

    final polygonsGlobalSpace =
        canvas3d.geometries.expand((geometry) => geometry.polygons
            .map((polygon) => ProcessingPolygon(
                  polygon,
                  geometry.color,
                ))
            .map((polygon) => polygon.transformed(geometry.transform)));

    final polygonsViewSpace = polygonsGlobalSpace
        .map((polygon) => polygon.transformed(view))
        .map((polygon) => polygon.illuminated(canvas3d.lightDirection))
        .where((polygon) => polygon.points.every((v) => v.z < 0.0))
        .map((polygon) => polygon.perspectiveDivision);

    // Depth sort polygons.
    final depthSortedPolygons = polygonsViewSpace.toList(growable: false)
      ..sort();

    final drawPolygons = depthSortedPolygons;
    var outlinePath = Path();

    for (final polygon in drawPolygons) {
      // Convert polygon position vectors into offsets.
      final offsets =
          polygon.points.map((position) => Offset(position.x, position.y));

      // Path of the current polygon to draw.
      final path = Path()..addPolygon(offsets.toList(growable: false), false);

      // Add current polygon path to outline path.
      outlinePath = Path.combine(PathOperation.union, outlinePath, path);

      final paint = Paint()..color = polygon.color;

      canvas.drawPath(path, paint);
    }

    outlinePath.close();
    canvas.drawPath(outlinePath, outlinePaint);
  }
}

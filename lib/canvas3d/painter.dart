import 'package:flutter/material.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/canvas3d/rendering.dart';
import 'package:vector_math/vector_math_64.dart'
    show Matrix4, makePerspectiveMatrix, makeViewMatrix;

class Canvas3dPainter extends CustomPainter {
  final Canvas3d canvas3d;

  final outlinePaint;

  static const nearPlane = 0.1;

  static const farPlane = 100.0;

  Canvas3dPainter(this.canvas3d)
      : outlinePaint = Paint()
          ..color = canvas3d.outlineColor
          ..style = PaintingStyle.stroke
          ..strokeJoin = StrokeJoin.round;

  @override
  bool shouldRepaint(final CustomPainter oldDelegate) => true;

  @override
  void paint(final Canvas canvas, final Size size) {
    final aspectRatio = size.width / size.height;

    // Transform canvas into viewport space:
    canvas
      ..translate(size.width / 2.0, size.height / 2.0)
      ..scale(size.width / 2.0, -size.height / 2.0)
      ..clipRect(Rect.fromLTRB(-1.0, 1.0, 1.0, -1.0));

    final projection = makePerspectiveMatrix(
      canvas3d.fov.radians,
      aspectRatio,
      nearPlane,
      farPlane,
    );

    final view = makeViewMatrix(
      canvas3d.cameraPosition.eye,
      canvas3d.cameraPosition.focus,
      canvas3d.cameraPosition.up,
    );

    final polygonsGlobalSpace =
        canvas3d.geometries.expand((geometry) => geometry.polygons
            .map((polygon) => ProcessingPolygon(
                  polygon.positions,
                  geometry.color,
                ))
            .map((polygon) => polygon.transformed(geometry.transform)));

    final polygonsViewSpace = polygonsGlobalSpace
        .map((polygon) => polygon.transformed(view))
        .map((polygon) => polygon.illuminated(canvas3d.lightDirection));

    // Depth sort polygons.
    final depthSortedPolygons = polygonsViewSpace.toList()..sort();

    // Clip polygons away that are either too near or too far.
    // This has to be done before perspective division occurs.
    // When orthographic projection is used, there is no "too near".
    final distanceClippedPolygons = depthSortedPolygons
        .where((polygon) => polygon.positions.every((v) => v.z < 0.0));

    // Transform into projective space.
    final polygonsProjectiveSpace = distanceClippedPolygons
        .map((polygon) => polygon.perspectiveTransformed(projection));

    final drawPolygons = polygonsProjectiveSpace.toList();
    var outlinePath = Path();
    outlinePaint.strokeWidth = 0.01 / aspectRatio;

    for (final polygon in drawPolygons) {
      // Convert polygon position vectors into offsets.
      final offsets = polygon.positions
          .map((position) => Offset(position.x, position.y))
          .toList();

      // Path of the current polygon to draw.
      final path = Path()..addPolygon(offsets, false);

      // Add current polygon path to outline path.
      outlinePath = Path.combine(PathOperation.union, outlinePath, path);

      final paint = Paint()..color = polygon.color;

      canvas.drawPath(path, paint);
    }

    canvas.scale(1.0, aspectRatio);
    outlinePath.close();
    canvas.drawPath(
        outlinePath.transform(
            Matrix4.diagonal3Values(1.0, 1.0 / aspectRatio, 1.0).storage),
        outlinePaint);
  }
}

import 'package:flutter/material.dart';
import 'package:pentachoron/canvas3d/canvas3d.dart';
import 'package:pentachoron/canvas3d/depth_compare.dart';
import 'package:pentachoron/canvas3d/illuminated_polygon.dart';
import 'package:pentachoron/geometry/matrix.dart';
import 'package:pentachoron/geometry/volume.dart';

class Canvas3dPainter extends CustomPainter {
  final Canvas3d canvas3d;

  final Paint outlinePaint;

  static final view = Matrix.fromRows([
    [1.0, 0.0, 0.0, 0.0, 0.0],
    [0.0, 0.0, -1.0, 0.0, 0.0],
    [0.0, 1.0, 0.0, 0.0, 0.0],
    [0.0, 0.0, 0.0, 1.0, 0.0],
    [0.0, 0.0, 0.0, 0.0, 1.0],
  ]);

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
    final t0 = DateTime.now();

    // Transform canvas into viewport space:
    canvas
      ..clipRect(Rect.fromLTWH(0.0, 0.0, size.width, size.height))
      ..save()
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

    var outlinePath = Path();
    var totalPolygonInputCount = 0;
    var totalPolygonDrawCount = 0;
    
    for (final volume in canvas3d.drawable
        .map((drawable) => Volume(drawable
            .lines(canvas3d.modelMatrix * view)
            .map((line) => line.intersection)
            .where((line) => line != null)))
        .where((volume) => volume.points.length >= 3)) {
      final polygons = volume.hull;
      totalPolygonInputCount += polygons.length;

      final polygonsPerspectiveSpace = polygons
          .map((polygon) => IlluminatedPolygon(
              polygon, canvas3d.color, canvas3d.lightDirection))
          .where((polygon) => polygon.polygon.points.every((v) => v.z < 0.0))
          .map((polygon) => IlluminatedPolygon.perspectiveDivision(polygon))
          .where((polygon) => polygon.polygon.normal.z > 0.0);

      // Depth sort polygons.
      final depthSortedPolygons = polygonsPerspectiveSpace.toList(
          growable: false)
        ..sort(
            (final a, final b) => depthCompareBarycenter(a.polygon, b.polygon));

      final drawPolygons = depthSortedPolygons;
      totalPolygonDrawCount += drawPolygons.length;

      for (final polygon in drawPolygons) {
        // Convert polygon position vectors into offsets.
        final offsets = polygon.polygon.points
            .map((position) => Offset(position.x, position.y));

        // Path of the current polygon to draw.
        final path = Path()..addPolygon(offsets.toList(growable: false), false);

        // Add current polygon path to outline path.
        outlinePath = Path.combine(PathOperation.union, outlinePath, path);

        final paint = Paint()..color = polygon.color;

        canvas.drawPath(path, paint);
      }
    }

    outlinePath.close();
    canvas.drawPath(outlinePath, outlinePaint);

    canvas.restore();

    if (canvas3d.printDrawStats) {
      TextPainter(
        text: TextSpan(
          text: "${DateTime.now().difference(t0).inMilliseconds}ms\n"
              "Polygon input count: $totalPolygonInputCount\n"
              "Polygon draw count: $totalPolygonDrawCount\n"
              "Model matrix:\n${canvas3d.modelMatrix.toStringLong()}\n",
          style: canvas3d.drawStatsStyle,
        ),
        textDirection: TextDirection.ltr,
      )
        ..layout()
        ..paint(canvas, Offset(10.0, 10.0));
    }
  }
}

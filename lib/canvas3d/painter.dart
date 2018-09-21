import 'package:flutter/material.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/canvas3d/depth_compare.dart';
import 'package:tesserapp/canvas3d/illuminated_polygon.dart';
import 'package:tesserapp/generic/benchmark.dart';
import 'package:tesserapp/geometry/matrix.dart';

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

    final polygons = canvas3d.drawable.intersection.hull;

    final b = Benchmark.start();

    final modelView = canvas3d.modelMatrix * view;

    b.step("Compute model-view matrix");

    final polygonsModelViewSpace = polygons
        .map((polygon) => polygon.transformed(modelView))
        .toList(growable: false);

    b.step("Transform to model view space");

    final polygonsIlluminated = polygonsModelViewSpace
        .map((polygon) => IlluminatedPolygon(
            polygon, canvas3d.color, canvas3d.lightDirection))
        .toList(growable: false);

    b.step("Illumated");

    final polygonsClipped = polygonsIlluminated
        .where((polygon) => polygon.polygon.points.every((v) => v.z < 0.0))
        .toList(growable: false);

    b.step("Clip");

    final polygonsProjectiveSpace = polygonsClipped
        .map((polygon) => IlluminatedPolygon.perspectiveDivision(polygon))
        .toList(growable: false);

    b.step("Transform to projection space");

    final polygonsCulled = polygonsProjectiveSpace
        .where((polygon) => polygon.polygon.normal.z > 0.0)
        .toList(growable: false);

    b.step("Transform");

    // Depth sort polygons.
    final depthSortedPolygons = polygonsCulled.toList(growable: false)
      ..sort(
          (final a, final b) => depthCompareBarycenter(a.polygon, b.polygon));

    b.step("Sort");

    final drawPolygons = depthSortedPolygons;
    var outlinePath = Path();

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

    b.step("Draw");

    outlinePath.close();
    canvas.drawPath(outlinePath, outlinePaint);

    canvas.restore();

    if (canvas3d.printDrawStats) {
      TextPainter(
        text: TextSpan(
          text: "${DateTime.now().difference(t0).inMilliseconds}ms\n"
              "Polygon input count: ${polygons.length}\n"
              "Polygon draw count: ${drawPolygons.length}\n"
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

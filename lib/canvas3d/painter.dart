part of canvas3d;

class _Canvas3dPainter extends CustomPainter {
  final DrawParameters parameters;

  final outlinePaint;

  _Canvas3dPainter(this.parameters)
      : outlinePaint = Paint()
          ..color = parameters.outlineColor
          ..style = PaintingStyle.stroke
          ..strokeWidth = 0.01
          ..strokeJoin = StrokeJoin.round
          ..isAntiAlias = parameters.antiAliasing;

  @override
  bool shouldRepaint(final CustomPainter oldDelegate) => true;

  @override
  void paint(final Canvas canvas, final Size size) {
    // Transform canvas into viewport space:
    canvas
      ..translate(size.width / 2.0, size.height / 2.0)
      ..scale(size.width / 2.0, -size.height / 2.0);

    final projection = !parameters.orthographicProjection
        ? makePerspectiveMatrix(
            parameters.fov.radians, size.width / size.height, 0.1, 100.0)
        : makeOrthographicMatrix(
            (parameters.frustumSize / 2.0) * -size.width / size.height,
            (parameters.frustumSize / 2.0) * size.width / size.height,
            -parameters.frustumSize / 2.0,
            parameters.frustumSize / 2.0,
            0.1,
            10.0);

    final view = makeViewMatrix(
      parameters.cameraPosition.eye,
      parameters.cameraPosition.focus,
      parameters.cameraPosition.up,
    );

    final sortedPolygons = parameters.geometries
        .expand((geometry) => geometry.polygons
            .map((polygon) => _ProcessingPolygon(
                  polygon.positions,
                  polygon.color,
                  geometry.outlined,
                  parameters.enableCulling,
                ))
            .map((polygon) => polygon.transformed(geometry.transform)))
        .map((polygon) => polygon.illuminated(parameters.lightDirection))
        .map((polygon) => polygon.transformed(view))
        .toList()
          ..sort();

    var outline = (parameters.outlineMode != OutlineMode.off) ? Path() : null;

    sortedPolygons
        .map((polygon) => polygon.perspectiveTransformed(projection))
        .where((polygon) => polygon.normal.z > 0.0 || !parameters.enableCulling)
        .forEach((polygon) {
      final offsets = polygon.positions
          .map((position) => Offset(position.x, position.y))
          .toList();
      final path = Path()..addPolygon(offsets, false);

      if (parameters.outlineMode != OutlineMode.off && polygon.outlined) {
        // Add current polygon path to outline path.
        outline = Path.combine(PathOperation.union, outline, path);
      } else if (parameters.outlineMode == OutlineMode.occluded) {
        // Remove current path from outline, so that the outline outlines
        // only the visible, un-obscured part of the geometry
        // rather than simply the whole geometry.
        // Is is quite performance heavy when having a lot of polygons.
        outline = Path.combine(PathOperation.difference, outline, path);
      }

      canvas.drawPath(
          path,
          Paint()
            ..color = polygon.color
            ..isAntiAlias = parameters.antiAliasing);
    });

    canvas.drawPath(outline, outlinePaint);
  }
}

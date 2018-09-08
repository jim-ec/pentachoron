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

    final polygonsGlobalSpace =
        parameters.geometries.expand((geometry) => geometry.polygons
            .map((polygon) => _ProcessingPolygon(
                  polygon.positions,
                  polygon.color,
                  geometry.outlined,
                  geometry.culling,
                ))
            .map((polygon) => polygon.transformed(geometry.transform)));

    final polygonsViewSpace = polygonsGlobalSpace
        .map((polygon) => polygon.illuminated(parameters.lightDirection))
        .map((polygon) => polygon.transformed(view));

    final depthSortedPolygons = polygonsViewSpace.toList()..sort();

    final polygonsProjectiveSpace = depthSortedPolygons
        .map((polygon) => polygon.perspectiveTransformed(projection));

    final culledPolygons = polygonsProjectiveSpace.where((polygon) {
      switch (polygon.culling) {
        case CullMode.off:
          return true;
        case CullMode.frontFacing:
          return polygon.normal.z < 0.0;
        case CullMode.backFacing:
          return polygon.normal.z > 0.0;
      }
    });

    final drawPolygons = culledPolygons;
    var outline = (parameters.outlineMode != OutlineMode.off) ? Path() : null;

    for (final polygon in drawPolygons) {
      
      // Convert polygon position vectors into offsets.
      final offsets = polygon.positions
          .map((position) => Offset(position.x, position.y))
          .toList();

      // Path of the current polygon to draw.
      final path = Path()..addPolygon(offsets, false);

      // Modify outline according to current path.
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

      final paint = Paint()
        ..color = polygon.color
        ..isAntiAlias = parameters.antiAliasing;

      canvas.drawPath(path, paint);
    }
    canvas.drawPath(outline, outlinePaint);
  }
}

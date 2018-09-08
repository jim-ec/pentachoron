part of canvas3d;

class _Canvas3dPainter extends CustomPainter {
  final DrawParameters parameters;

  final outlinePaint;

  static const nearPlane = 0.1;

  static const farPlane = 100.0;

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
      ..scale(size.width / 2.0, -size.height / 2.0)
      ..clipRect(Rect.fromLTRB(-1.0, 1.0, 1.0, -1.0));

    final projection = !parameters.orthographicProjection
        ? makePerspectiveMatrix(
            parameters.fov.radians,
            size.width / size.height,
            nearPlane,
            farPlane,
          )
        : makeOrthographicMatrix(
            (parameters.frustumSize / 2.0) * -size.width / size.height,
            (parameters.frustumSize / 2.0) * size.width / size.height,
            -parameters.frustumSize / 2.0,
            parameters.frustumSize / 2.0,
            nearPlane,
            farPlane,
          );

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
        .map((polygon) => (parameters.lightSpace == LightSpace.global)
            ? polygon.illuminated(parameters.lightDirection)
            : polygon)
        .map((polygon) => polygon.transformed(view))
        .map((polygon) => (parameters.lightSpace == LightSpace.view)
            ? polygon.illuminated(parameters.lightDirection)
            : polygon);

    // Depth sort polygons.
    final depthSortedPolygons = polygonsViewSpace.toList()..sort();

    // Clip polygons away that are either too near or too far.
    // This has to be done before perspective division occurs.
    // When orthographic projection is used, there is no "too near".
    final distanceClippedPolygons = depthSortedPolygons.where((polygon) =>
        polygon.positions.every((v) =>
            (v.z < 0.0 || parameters.orthographicProjection) &&
            v.z > -parameters.viewDistance));

    // Transform into projective space.
    final polygonsProjectiveSpace = distanceClippedPolygons
        .map((polygon) => polygon.perspectiveTransformed(projection));

    // Cull polygons.
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

    // Clip polygons away that are fully out of view:
    final clippedPolygons = culledPolygons.where((polygon) => polygon.positions
        .any((v) => v.x < 1.0 && v.x > -1.0 && v.y < 1.0 && v.y > -1.0));

    final drawPolygons = clippedPolygons.toList();
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

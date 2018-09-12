part of canvas3d;

class _Canvas3dPainter extends CustomPainter {
  final Canvas3d canvas3d;

  final outlinePaint;

  static const nearPlane = 0.1;

  static const farPlane = 100.0;

  _Canvas3dPainter(this.canvas3d)
      : outlinePaint = Paint()
          ..color = canvas3d.outlineColor
          ..style = PaintingStyle.stroke
          ..strokeJoin = StrokeJoin.round
          ..isAntiAlias = canvas3d.antiAliasing;

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

    final projection = !canvas3d.orthographicProjection
        ? makePerspectiveMatrix(
            canvas3d.fov.radians,
            aspectRatio,
            nearPlane,
            farPlane,
          )
        : makeOrthographicMatrix(
            (canvas3d.frustumSize / 2.0) * -aspectRatio,
            (canvas3d.frustumSize / 2.0) * aspectRatio,
            -canvas3d.frustumSize / 2.0,
            canvas3d.frustumSize / 2.0,
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
            .map((polygon) => _ProcessingPolygon(
                  polygon.positions,
                  polygon.color,
                  geometry.outlined,
                  geometry.culling,
                ))
            .map((polygon) => polygon.transformed(geometry.transform)));

    final polygonsViewSpace = polygonsGlobalSpace
        .map((polygon) => (canvas3d.lightSpace == LightSpace.global)
            ? polygon.illuminated(canvas3d.lightDirection)
            : polygon)
        .map((polygon) => polygon.transformed(view))
        .map((polygon) => (canvas3d.lightSpace == LightSpace.view)
            ? polygon.illuminated(canvas3d.lightDirection)
            : polygon);

    // Depth sort polygons.
    final depthSortedPolygons = polygonsViewSpace.toList()..sort();

    // Clip polygons away that are either too near or too far.
    // This has to be done before perspective division occurs.
    // When orthographic projection is used, there is no "too near".
    final distanceClippedPolygons = depthSortedPolygons.where((polygon) =>
        polygon.positions.every((v) =>
            (v.z < 0.0 || canvas3d.orthographicProjection) &&
            v.z > -canvas3d.viewDistance));

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
    var outlinePath = (canvas3d.outlineMode != OutlineMode.off) ? Path() : null;
    outlinePaint.strokeWidth = 0.01 / aspectRatio;

    for (final polygon in drawPolygons) {
      // Convert polygon position vectors into offsets.
      final offsets = polygon.positions
          .map((position) => Offset(position.x, position.y))
          .toList();

      // Path of the current polygon to draw.
      final path = Path()..addPolygon(offsets, false);

      // Modify outline according to current path.
      if (canvas3d.outlineMode != OutlineMode.off && polygon.outlined) {
        // Add current polygon path to outline path.
        outlinePath = Path.combine(PathOperation.union, outlinePath, path);
      } else if (canvas3d.outlineMode == OutlineMode.occluded) {
        // Remove current path from outline, so that the outline outlines
        // only the visible, un-obscured part of the geometry
        // rather than simply the whole geometry.
        // Is is quite performance heavy when having a lot of polygons.
        outlinePath = Path.combine(PathOperation.difference, outlinePath, path);
      }

      final paint = Paint()
        ..color = polygon.color
        ..isAntiAlias = canvas3d.antiAliasing;

      canvas.drawPath(path, paint);
    }

    if(outlinePath != null) {
      canvas.scale(1.0, aspectRatio);
      canvas.drawPath(outlinePath.transform(Matrix4.diagonal3Values(1.0, 1.0 / aspectRatio, 1.0).storage), outlinePaint);
    }
  }
}

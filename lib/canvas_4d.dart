import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:tesserapp/angle.dart';
import 'package:vector_math/vector_math_64.dart';

class Canvas4d extends StatelessWidget {
  final Color color;
  final CameraPosition cameraPosition;
  
  const Canvas4d({
    Key key,
    @required this.color,
    @required this.cameraPosition,
  }) : super(key: key);
  
  @override
  Widget build(BuildContext context) =>
      Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: _Canvas4dPainter(color, cameraPosition),
        ),
      );
}

class _Canvas4dPainter extends CustomPainter {
  final Paint geometryPaint;
  final CameraPosition cameraPosition;
  
  /// Vertical field of view in radians:
  static const fov = 60.0 * degrees2Radians;
  
  _Canvas4dPainter(final Color color,
      this.cameraPosition,) : geometryPaint = Paint()
    ..color = color
    ..strokeWidth = 0.0;
  
  @override
  bool shouldRepaint(final CustomPainter oldDelegate) => true;
  
  @override
  void paint(final Canvas canvas, final Size size) {
    final List<Vector3> positions = [
      Vector3(0.0, 1.0, 0.0),
      Vector3(0.0, -1.0, 1.0),
      Vector3(0.0, -1.0, -1.0),
    ];
    
    // Transform canvas into GL viewport space:
    canvas.translate(size.width / 2.0, size.height / 2.0);
    canvas.scale(size.width / 2.0, -size.height / 2.0);
    
    final quaternion =
    Quaternion.euler(cameraPosition.polar.radians, 0.0, cameraPosition.azimuth.radians);
    
    final projection =
    makePerspectiveMatrix(fov, size.width / size.height, 0.1, 100.0);
    
    final view = makeViewMatrix(
      Vector3(5.0, 0.0, 0.0),
      Vector3.zero(),
      Vector3(0.0, 1.0, 0.0),
    );
    
    final offsets = positions
        .map((v) => quaternion.rotated(v))
        .map((v) => Vector4(v.x, v.y, v.z, 1.0))
        .map((v) => view.transform(v))
        .map((v) => projection.transform(v) / v.w)
        .map((position) => Offset(position.x, position.y))
        .toList();
    
    canvas.drawPath(Path()
      ..addPolygon(offsets, true), geometryPaint);
  }
}

/// Camera position.
/// Rather than using cartesian coordinates, which would be quite impractical
/// as the camera is supposed to orbit around a fixed center, the position
/// is denoted by a given [distance] from the origin and two angles.
///
/// - The [polar] angle defines the rotation around the x-y plane.
/// - The [azimuth] angle dines the rotation from the x-y plane towards the
///   z-axis.
///
class CameraPosition {
  double distance = 1.0;
  Angle polar;
  Angle azimuth;
  
  CameraPosition({
    this.distance,
    this.polar = const Angle.zero(),
    this.azimuth = const Angle.zero(),
  });
}

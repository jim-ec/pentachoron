import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/generic/number_range.dart';
import 'package:vector_math/vector_math_64.dart';

class Canvas4d extends StatelessWidget {
  final CameraPosition cameraPosition;
  final List<Face> faces;

  const Canvas4d({
    Key key,
    @required this.cameraPosition,
    @required this.faces,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: _Canvas4dPainter(cameraPosition, faces),
        ),
      );
}

class _Canvas4dPainter extends CustomPainter {
  final CameraPosition cameraPosition;
  final bool enableCulling = true;

  final List<Face> faces;

  /// Vertical field of view in radians:
  static const fov = Angle.fromDegrees(60.0);

  /// Direction of global light:
  static final lightDirection = Vector3(1.0, 0.8, 0.2).normalized();

  _Canvas4dPainter(this.cameraPosition, this.faces);

  @override
  bool shouldRepaint(final CustomPainter oldDelegate) => true;

  @override
  void paint(final Canvas canvas, final Size size) {
    // Transform canvas into viewport space:
    canvas.translate(size.width / 2.0, size.height / 2.0);
    canvas.scale(size.width / 2.0, -size.height / 2.0);

    final quaternion = Quaternion.euler(
        cameraPosition.polar.radians, 0.0, cameraPosition.azimuth.radians);

    final projection = makePerspectiveMatrix(
        fov.radians, size.width / size.height, 0.1, 100.0);

    final view = makeViewMatrix(
      Vector3(cameraPosition.distance, 0.0, 0.0),
      Vector3.zero(),
      Vector3(0.0, 1.0, 0.0),
    );

    faces.map((face) {
      final positionsGlobalSpace = face.positions
          .map((pos) => Vector3(pos.x, pos.y, pos.z))
          .map((v) => quaternion.rotated(v))
          .toList();

      final normal = (positionsGlobalSpace[2] - positionsGlobalSpace[0])
          .cross(positionsGlobalSpace[1] - positionsGlobalSpace[0])
          .normalized();
      final luminance = normal.dot(lightDirection);
      final softenLuminance = remap(luminance, -1.0, 1.0, -0.2, 1.2);
      final illuminatedColor =
      Color.lerp(Color(0xff000000), face.color, softenLuminance);

      final positionsViewSpace =
      positionsGlobalSpace.map((v) => view.transformed3(v)).toList();

      return Face.fromVector3(positionsViewSpace[0], positionsViewSpace[1],
          positionsViewSpace[2], illuminatedColor);
    }).toList()
      ..sort((faceA, faceB) => faceA.barycenter.z > faceB.barycenter.z ? 1 : -1)
      ..forEach((face) {
        final positionsPerspectiveSpace = face.positions
            .map((pos) => Vector3(pos.x, pos.y, pos.z))
            .map((v) => projection.perspectiveTransform(v))
            .toList();
    
        final normalPerspectiveSpace = (positionsPerspectiveSpace[2] -
            positionsPerspectiveSpace[0])
            .cross(positionsPerspectiveSpace[1] - positionsPerspectiveSpace[0])
            .normalized();
        final isFrontFacing = normalPerspectiveSpace.z > 0.0;
        if (!isFrontFacing && enableCulling) return;
    
        final offsets = positionsPerspectiveSpace
            .map((position) => Offset(position.x, position.y))
            .toList();
    
        canvas.drawPath(
            Path()
              ..addPolygon(offsets, true), Paint()
          ..color = face.color);
      });
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

@immutable
class Position {
  final double x, y, z;

  const Position(this.x,
      this.y,
      this.z,);

  const Position.zero() : this(0.0, 0.0, 0.0);

  Position operator +(final Position other,) =>
      Position(x + other.x, y + other.y, z + other.z);

  Position operator -(final Position other,) =>
      this + -other;

  Position operator -() => Position(-x, -y, -z);

  Position operator /(final double value) =>
      Position(x / value, y / value, z / value);
}

@immutable
class Face {
  final Position a, b, c;
  final Color color;

  Face(this.a, this.b, this.c, this.color);

  Face.fromVector3(final Vector3 a,
      final Vector3 b,
      final Vector3 c,
      final Color color,)
      : this(Position(a.x, a.y, a.z), Position(b.x, b.y, b.z),
      Position(c.x, c.y, c.z), color);

  List<Position> get positions => [a, b, c];

  Position get barycenter => (a + b + c) / 3.0;
}

List<Face> cube({
  final Position center,
  final double sideLength,
  final Color color,
}) {
  final a = sideLength / 2;
  final positions = [
    center + Position(a, a, a),
    center + Position(a, a, -a),
    center + Position(a, -a, a),
    center + Position(a, -a, -a),
    center + Position(-a, a, a),
    center + Position(-a, a, -a),
    center + Position(-a, -a, a),
    center + Position(-a, -a, -a),
  ];

  return [
    Face(positions[0], positions[1], positions[3], color),
    Face(positions[0], positions[3], positions[2], color),
    Face(positions[1], positions[5], positions[3], color),
    Face(positions[5], positions[7], positions[3], color),
    Face(positions[5], positions[4], positions[7], color),
    Face(positions[4], positions[6], positions[7], color),
    Face(positions[4], positions[0], positions[2], color),
    Face(positions[4], positions[2], positions[6], color),
    Face(positions[0], positions[4], positions[5], color),
    Face(positions[0], positions[5], positions[1], color),
    Face(positions[2], positions[3], positions[6], color),
    Face(positions[3], positions[7], positions[6], color),
  ];
}

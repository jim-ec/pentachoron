import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/generic/number_range.dart';
import 'package:vector_math/vector_math_64.dart';

class Canvas4d extends StatelessWidget {
  final CameraPosition cameraPosition;
  final List<Geometry> geometries;

  const Canvas4d({
    Key key,
    @required this.cameraPosition,
    @required this.geometries,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: _Canvas4dPainter(cameraPosition, geometries),
        ),
      );
}

class _Canvas4dPainter extends CustomPainter {
  final CameraPosition cameraPosition;
  final bool enableCulling = true;

  final List<Geometry> geometries;

  /// Vertical field of view in radians:
  static const fov = Angle.fromDegrees(60.0);

  /// Direction of global light:
  static final lightDirection = Vector3(1.0, 0.8, 0.2).normalized();

  _Canvas4dPainter(this.cameraPosition, this.geometries);

  @override
  bool shouldRepaint(final CustomPainter oldDelegate) => true;

  @override
  void paint(final Canvas canvas, final Size size) {
    // Transform canvas into viewport space:
    canvas.translate(size.width / 2.0, size.height / 2.0);
    canvas.scale(size.width / 2.0, -size.height / 2.0);

    final projection = makePerspectiveMatrix(
        fov.radians, size.width / size.height, 0.1, 100.0);

    final view = makeViewMatrix(
      cameraPosition.eye,
      cameraPosition.focus,
      cameraPosition.up,
    );

    geometries.expand<Polygon>((geometry) {
//      final quaternion = Quaternion.euler(
//          cameraPosition.polar.radians, 0.0, cameraPosition.azimuth.radians);
      final quaternion = Quaternion.identity();

      return geometry.polygons.map((polygon) {
        final polygonGlobalSpace = Polygon(polygon.positions.map((v) => quaternion.rotated(v)).toList(), polygon.color);

        final luminance = polygonGlobalSpace.normal.dot(lightDirection);
        final softenLuminance = remap(luminance, -1.0, 1.0, -0.2, 1.2);
        final illuminatedColor = Color.lerp(Color(0xff000000),
            polygon.color ?? geometry.color, softenLuminance);

        final positionsViewSpace =
            polygonGlobalSpace.positions.map((v) => view.transformed3(v)).toList();

        return Polygon(positionsViewSpace, illuminatedColor);
      });
    }).toList()
      ..sort((a, b) => a.barycenter.z > b.barycenter.z ? 1 : -1)
      ..forEach((polygon) {
        final positionsPerspectiveSpace = polygon.positions
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
            Path()..addPolygon(offsets, true), Paint()..color = polygon.color);
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
  final Vector3 eye, focus, up;

  CameraPosition({
    this.eye,
    this.focus,
    up,
  }) : up = up ?? Vector3(0.0, 1.0, 0.0);

  CameraPosition.fromOrbitEuler({
    final double distance,
    final Angle polar,
    final Angle azimuth,
  }) : this(
            focus: Vector3.zero(),
            eye: Matrix4
                .rotationY(polar.radians)
                .multiplied(Matrix4.rotationZ(azimuth.radians))
                .transform3(Vector3(distance, 0.0, 0.0)));
}

/// A polygon consists of an arbitrary count of vertices.
///
/// All vertices must share the same mathematical plane, i.e. the polygon has
/// a single normal vector.
@immutable
class Polygon {
  final List<Vector3> positions;
  final Color color;

  Polygon(this.positions, [this.color]) {
    assert(positions.length >= 3, "Each polygon must have at least 3 vertices");
  }

  Vector3 get barycenter =>
      positions.reduce((a, b) => a + b) / positions.length.toDouble();

  Vector3 get normal => (positions[2] - positions[0])
      .cross(positions[1] - positions[0])
      .normalized();
}

List<Polygon> cube({
  final Vector3 center,
  final double sideLength,
  final Color color,
}) {
  final a = sideLength / 2;
  final positions = [
    center + Vector3(a, a, a),
    center + Vector3(a, a, -a),
    center + Vector3(a, -a, a),
    center + Vector3(a, -a, -a),
    center + Vector3(-a, a, a),
    center + Vector3(-a, a, -a),
    center + Vector3(-a, -a, a),
    center + Vector3(-a, -a, -a),
  ];

  return [
    Polygon([positions[0], positions[1], positions[3], positions[2]], color),
    Polygon([positions[1], positions[5], positions[7], positions[3]], color),
    Polygon([positions[5], positions[4], positions[6], positions[7]], color),
    Polygon([positions[4], positions[0], positions[2], positions[6]], color),
    Polygon([positions[0], positions[4], positions[5], positions[1]], color),
    Polygon([positions[2], positions[3], positions[7], positions[6]], color),
  ];
}

class Geometry {
  final Quaternion quaternion;
  final Vector3 translation;
  final Vector3 scale;
  final List<Polygon> polygons;
  final Color color;

  Geometry({
    @required this.quaternion,
    @required this.translation,
    @required this.scale,
    @required this.polygons,
    this.color,
  });
}

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
  /// Camera position, in global space.
  final CameraPosition cameraPosition;

  /// If enabled, back facing polygons are not drawn at all.
  /// This improves performance, as fewer vertices have to processed
  /// and fewer polygons needs to be drawn.
  /// On the other side, enabling culling can increase artifacts at the
  /// polygon edges due to anti-aliasing.
  final bool enableCulling = false;

  /// If enabled, geometry is drawn using an orthographic projection
  /// rather then using a perspective projection.
  final bool orthographicProjection = false;

  /// List of geometry to be drawn.
  final List<Geometry> geometries;

  /// Vertical field of view in radians.
  /// The value is only used when rendering perspective projection.
  final fov = Angle.fromDegrees(60.0);

  /// The frustum side length.
  /// The value is only used when rendering orthographic projection.
  final frustumSize = 10.0;

  /// Direction of global light:
  final lightDirection = Vector3(1.0, 0.8, 0.2).normalized();

  _Canvas4dPainter(this.cameraPosition, this.geometries);

  @override
  bool shouldRepaint(final CustomPainter oldDelegate) => true;

  @override
  void paint(final Canvas canvas, final Size size) {
    // Transform canvas into viewport space:
    canvas
      ..translate(size.width / 2.0, size.height / 2.0)
      ..scale(size.width / 2.0, -size.height / 2.0);

    final projection = !orthographicProjection
        ? makePerspectiveMatrix(
            fov.radians, size.width / size.height, 0.1, 100.0)
        : makeOrthographicMatrix(
            (frustumSize / 2.0) * -size.width / size.height,
            (frustumSize / 2.0) * size.width / size.height,
            -frustumSize / 2.0,
            frustumSize / 2.0,
            0.1,
            10.0);

    final view = makeViewMatrix(
      cameraPosition.eye,
      cameraPosition.focus,
      cameraPosition.up,
    );

    geometries
        .expand((geometry) => geometry.polygons)
        .map((polygon) => polygon.map((v) => Quaternion.identity().rotated(v)))
        .map((polygon) => polygon.illuminated(lightDirection))
        .map((polygon) => polygon.map((v) => view.transformed3(v)))
        .toList()
          ..sort()
          ..map((polygon) =>
                  polygon.map((v) => projection.perspectiveTransform(v)))
              .where((polygon) => polygon.normal.z > 0.0 || !enableCulling)
              .forEach((polygon) {
            final offsets = polygon.positions
                .map((position) => Offset(position.x, position.y))
                .toList();

            canvas.drawPath(Path()..addPolygon(offsets, false),
                Paint()..color = polygon.color);
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
class Polygon implements Comparable<Polygon> {
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

  Polygon map(Vector3 f(final Vector3 position)) =>
      Polygon(positions.map(f).toList(), color);

  Polygon illuminated(final Vector3 lightDirection) {
    final luminance = normal.dot(lightDirection);
    final softenLuminance = remap(luminance, -1.0, 1.0, -0.2, 1.2);
    final illuminatedColor =
        Color.lerp(Color(0xff000000), color, softenLuminance);
    return Polygon(positions, illuminatedColor);
  }

  /// Performs a depth comparison.
  ///
  /// The polygon which's barycenter has a higher z coordinate
  /// is occluding the other one.
  @override
  int compareTo(final Polygon other) =>
      barycenter.z > other.barycenter.z ? 1 : -1;
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
    final List<Polygon> polygons,
    final Color color,
  })  : color = color ?? Color(0xff000000),
        polygons = polygons
            .map((poly) => Polygon(poly.positions, poly.color ?? color))
            .toList();
}

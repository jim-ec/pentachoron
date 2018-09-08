import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/generic/number_range.dart';
import 'package:vector_math/vector_math_64.dart';

class Canvas4d extends StatelessWidget {
  final CameraPosition cameraPosition;
  final List<Geometry> geometries;
  final OutlineMode outlineMode;
  final Color outlineColor;

  Canvas4d({
    Key key,
    @required this.cameraPosition,
    @required this.geometries,
    this.outlineMode = OutlineMode.off,
    this.outlineColor,
  }) : super(key: key) {
    if (outlineMode != OutlineMode.off) {
      assert(
          outlineColor != null,
          "If outline mode is not off, "
          "a non-null outline color must be specified");
    }
  }

  @override
  Widget build(BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: _Canvas4dPainter(
            cameraPosition: cameraPosition,
            geometries: geometries,
            outlineMode: outlineMode,
            outlineColor: outlineColor,
          ),
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
  final bool enableCulling = true;

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

  final Color outlineColor = Color(0xffff0000);

  final outlinePaint;

  final OutlineMode outlineMode;

  _Canvas4dPainter({
    this.cameraPosition,
    this.geometries,
    final Color outlineColor,
    this.outlineMode,
  }) : outlinePaint = Paint()
          ..color = outlineColor ?? Color(0x0)
          ..style = PaintingStyle.stroke
          ..strokeWidth = 0.01
          ..strokeJoin = StrokeJoin.round;

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

    final sortedPolygons = geometries
        .expand((geometry) => geometry.polygons
            .map((polygon) => ProcessingPolygon(
                  polygon.positions,
                  polygon.color,
                  geometry.outlined,
                  enableCulling,
                ))
            .map((polygon) => polygon.transformed(geometry.transform)))
        .map((polygon) => polygon.illuminated(lightDirection))
        .map((polygon) => polygon.transformed(view))
        .toList()
          ..sort();

    var outline = (outlineMode != OutlineMode.off) ? Path() : null;

    sortedPolygons
        .map((polygon) => polygon.perspectiveTransformed(projection))
        .where((polygon) => polygon.normal.z > 0.0 || !enableCulling)
        .forEach((polygon) {
      final offsets = polygon.positions
          .map((position) => Offset(position.x, position.y))
          .toList();
      final path = Path()..addPolygon(offsets, false);

      if (outlineMode != OutlineMode.off && polygon.outlined) {
        outline = Path.combine(PathOperation.union, outline, path);
      } else if (outlineMode == OutlineMode.occluded) {
        // Remove current path from outline, so that the outline outlines
        // only the visible, un-obscured part of the geometry
        // rather than simply the whole geometry.
        // Is is quite performance heavy when having a lot of polygons.
        outline = Path.combine(PathOperation.difference, outline, path);
      }

      canvas.drawPath(path, Paint()..color = polygon.color);
    });

    canvas.drawPath(outline, outlinePaint);
  }
}

/// Marking a geometry as outlined adds it to the overhaul set of
/// outlined geometry. All outlined geometry share a common color and
/// a common outline path, that's why one cannot set the outline color
/// of a single geometry.
enum OutlineMode {
  /// No outlining at all.
  off,

  /// Outline is draw on top off all other geometry.
  overlay,

  /// Outline is occluded by obscuring geometry.
  /// The actual path is still closed, drawn around occluding geometry.
  /// This is quite performance expensive when drawing a lot of geometry.
  occluded,
}

/// Camera position.
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

  Polygon(this.positions, this.color) {
    assert(positions.length >= 3, "Each polygon must have at least 3 vertices");
  }
}

/// A polygon wrapper adding pipeline processing functionality to it.
/// It bundles per-geometry features like outlining into the polygons,
/// as they are decoupled from their geometries in order to perform
/// depth sorting.
class ProcessingPolygon extends Polygon
    implements Comparable<ProcessingPolygon> {
  /// Whether or not to add this polygon to the outline path.
  final bool outlined;

  /// Not used right now.
  final bool culled;

  ProcessingPolygon(
    final List<Vector3> positions,
    final Color color,
    this.outlined,
    this.culled,
  ) : super(positions, color);

  /// This polygon's gravitational center.
  Vector3 get barycenter =>
      positions.reduce((a, b) => a + b) / positions.length.toDouble();

  /// Normal vector, standing perpendicular on top of the plane this polygon
  /// is forming.
  Vector3 get normal => (positions[2] - positions[0])
      .cross(positions[1] - positions[0])
      .normalized();

  /// Return a transformed version of this polygon.
  /// To transform the polygon using perspective matrices,
  /// use [perspectiveTransformed] instead.
  ProcessingPolygon transformed(final Matrix4 matrix) => ProcessingPolygon(
      positions.map((v) => matrix.transformed3(v)).toList(),
      color,
      outlined,
      culled);

  /// Return a transformed version of this polygon,
  /// taking perspective division into account.
  ProcessingPolygon perspectiveTransformed(final Matrix4 matrix) =>
      ProcessingPolygon(
          positions.map((v) => matrix.perspectiveTransform(v)).toList(),
          color,
          outlined,
          culled);

  /// Return a re-colored version of this polygon.
  /// [lightDirection] defines the direction of parallel light rays,
  /// used to illuminate the polygon.
  ///
  /// [lightDirection] is assumed to be in the *same coordinate space*
  /// as this polygon.
  ProcessingPolygon illuminated(final Vector3 lightDirection) {
    final luminance = normal.dot(lightDirection);
    final softenLuminance = remap(luminance, -1.0, 1.0, -0.2, 1.2);
    return ProcessingPolygon(
        positions,
        Color.lerp(Color(0xff000000), color, softenLuminance),
        outlined,
        culled);
  }

  /// Performs a depth comparison.
  ///
  /// The polygon which's barycenter has a higher z coordinate
  /// is occluding the other one.
  @override
  int compareTo(final ProcessingPolygon other) =>
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
  final List<Polygon> polygons;
  final bool outlined;
  final Matrix4 transform;

  /// Create a geometry from a set of points to be transformed through
  /// [rotation], [translation] and [scale], and a default color.
  Geometry({
    @required final List<Polygon> polygons,
    final Color color,
    this.outlined = false,
    Rotation rotation,
    Vector3 translation,
    Vector3 scale,
  })  : transform = rotation?.transform ??
            Matrix4.identity() *
                Matrix4.translation(translation ?? Vector3.zero()),
        polygons = polygons
            .map((poly) => Polygon(
                  poly.positions,
                  poly.color ?? color ?? Color(0xff000000),
                ))
            .toList();
}

class Rotation {
  final Matrix4 transform;

  Rotation.fromEuler(final Angle yaw, final Angle pitch, final Angle roll)
      : transform = Matrix4.rotationY(yaw.radians) *
            Matrix4.rotationZ(pitch.radians) *
            Matrix4.rotationX(roll.radians);
}

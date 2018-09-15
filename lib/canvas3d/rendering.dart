import 'dart:ui';

import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:tesserapp/generic/number_range.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3, Matrix4;

/// A polygon wrapper adding pipeline processing functionality to it.
/// It bundles per-geometry features like outlining into the polygons,
/// as they are decoupled from their geometries in order to perform
/// depth sorting.
class ProcessingPolygon implements Comparable<ProcessingPolygon> {
  
  final List<Vector3> points;
  final Color color;

  ProcessingPolygon.fromPolygon(
      final Polygon polygon,
      this.color,
      ) : points = polygon.points.map((v) => Vector3(v.x, v.y, v.z)).toList();

  ProcessingPolygon(
      this.points,
      this.color,
      );

  /// This polygon's gravitational center.
  Vector3 get barycenter =>
      points.reduce((a, b) => a + b) / points.length.toDouble();

  /// Normal vector, standing perpendicular on top of the plane this polygon
  /// is forming.
  Vector3 get normal => (points[2] - points[0])
      .cross(points[1] - points[0])
      .normalized();

  /// Return a transformed version of this polygon.
  /// To transform the polygon using perspective matrices,
  /// use [perspectiveTransformed] instead.
  ProcessingPolygon transformed(final Matrix4 matrix) => ProcessingPolygon(
      points.map((v) => matrix.transformed3(v)).toList(),
      color);

  /// Return a transformed version of this polygon,
  /// taking perspective division into account.
  ProcessingPolygon perspectiveTransformed(final Matrix4 matrix) =>
      ProcessingPolygon(
          points.map((v) => matrix.perspectiveTransform(v)).toList(),
          color);

  /// Return a re-colored version of this polygon.
  /// [lightDirection] defines the direction of parallel light rays,
  /// used to illuminate the polygon.
  ///
  /// [lightDirection] is assumed to be in the *same coordinate space*
  /// as this polygon.
  ProcessingPolygon illuminated(final Vector3 lightDirection) {
    final luminance = normal.dot(lightDirection).abs();
    final softenLuminance = remap(luminance, 0.0, 1.0, 0.2, 1.2);
    return ProcessingPolygon(
        points,
        Color.lerp(Color(0xff000000), color, softenLuminance));
  }

  /// Performs a depth comparison.
  ///
  /// The polygon which's barycenter has a higher z coordinate
  /// is occluding the other one.
  @override
  int compareTo(final ProcessingPolygon other) =>
      barycenter.z > other.barycenter.z ? 1 : -1;
}

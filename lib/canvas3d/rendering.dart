import 'dart:ui';

import 'package:tesserapp/generic/number_range.dart';
import 'package:tesserapp/geometry/polygon.dart';
import 'package:tesserapp/geometry/vector.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3, Matrix4;

/// A polygon wrapper adding pipeline processing functionality to it.
/// It bundles per-geometry features like outlining into the polygons,
/// as they are decoupled from their geometries in order to perform
/// depth sorting.
class ProcessingPolygon implements Comparable<ProcessingPolygon> {
  final Iterable<Vector> sourcePoints;
  final Iterable<Vector3> points;
  final Color color;
  final Vector3 barycenter;
  final Vector3 normal;

  ProcessingPolygon.fromPolygon(
    final Polygon polygon,
    final Color color,
  ) : this(polygon.points, polygon.points.map((v) => Vector3(v.x, v.y, v.z)),
            color);

  ProcessingPolygon(
    this.sourcePoints,
    this.points,
    this.color,
  )   : barycenter = points.reduce((a, b) => a + b) / points.length.toDouble(),
        normal = (points.length >= 3)
            ? (points.elementAt(2) - points.elementAt(0))
                .cross(points.elementAt(1) - points.elementAt(0))
                .normalized()
            : Vector3.zero();

  String _vectorToString(final Vector v) =>
      "(${v.x.toStringAsFixed(1)}, ${v.y.toStringAsFixed(1)}, ${v.z.toStringAsFixed(1)})";

  String _vector3ToString(final Vector3 v) =>
      "(${v.x.toStringAsFixed(1)}, ${v.y.toStringAsFixed(1)}, ${v.z.toStringAsFixed(1)})";

  @override
  String toString() =>
      "ProcessingPolygon(barycenter=${_vector3ToString(barycenter)}, "
      "points=${sourcePoints.map(_vectorToString).join(", ")})";

  /// Return a transformed version of this polygon.
  /// To transform the polygon using perspective matrices,
  /// use [perspectiveTransformed] instead.
  ProcessingPolygon transformed(final Matrix4 matrix) =>
      ProcessingPolygon(sourcePoints, points.map((v) => matrix.transformed3(v)), color);

  /// Return a transformed version of this polygon,
  /// taking perspective division into account.
  ProcessingPolygon perspectiveTransformed(final Matrix4 matrix) =>
      ProcessingPolygon(sourcePoints,
          points.map((v) => matrix.perspectiveTransform(v)), color);

  /// Return a re-colored version of this polygon.
  /// [lightDirection] defines the direction of parallel light rays,
  /// used to illuminate the polygon.
  ///
  /// [lightDirection] is assumed to be in the *same coordinate space*
  /// as this polygon.
  ProcessingPolygon illuminated(final Vector3 lightDirection) {
    final luminance = normal.dot(lightDirection).abs();
    final softenLuminance = remap(luminance, 0.0, 1.0, 0.2, 1.2);
    return ProcessingPolygon(sourcePoints,
        points, Color.lerp(Color(0xff000000), color, softenLuminance));
  }

  /// Performs a depth comparison.
  ///
  /// The polygon which's barycenter has a higher z coordinate
  /// is occluding the other one, i.e. sorted after it.
  @override
  int compareTo(final ProcessingPolygon other) =>
      barycenter.z > other.barycenter.z ? 1 : -1;
}

import 'dart:math';
import 'dart:ui';

import 'package:flutter/material.dart';
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
  var taggedAsIntersecting = false;

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
  ProcessingPolygon transformed(final Matrix4 matrix) => ProcessingPolygon(
      sourcePoints, points.map((v) => matrix.transformed3(v)), color);

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
    return ProcessingPolygon(sourcePoints, points,
        Color.lerp(Color(0xff000000), color, softenLuminance));
  }

  /// Performs a depth comparison.
  ///
  /// The polygon which's barycenter has a higher z coordinate
  /// is occluding the other one, i.e. sorted after it.
  ///
  /// Polygons are expected to not intersect each other.
  /// Cyclic occluding is not supported and will result into
  /// incorrect sorting, as this simple algorithm is not able
  /// to split polygons.
  @override
  int compareTo(final ProcessingPolygon other) {
    const occludingOther = 1;
    const occludedByOther = -1;

    // Check if both polygons occupy different z-ranges.
    final zMin = points.map((v) => v.z).reduce((a, b) => min(a, b));
    final zMax = points.map((v) => v.z).reduce((a, b) => max(a, b));
    final zMinOther = other.points.map((v) => v.z).reduce((a, b) => min(a, b));
    final zMaxOther = other.points.map((v) => v.z).reduce((a, b) => max(a, b));
    if (zMin > zMaxOther) {
      return occludingOther;
    }
    if (zMax < zMinOther) {
      return occludedByOther;
    }

    // Otherwise, check if other polygon lies outside of this polygon.
    // Compute plane equation to check for this.

    // Normal is taken is such a manner that is guaranteed to point into
    // positive z direction, i.e. against the view direction.

    // Plane equation:
    // ax + bx + cx - d = 0
    // Where a = n.x, b = n.y, c = n.z
    final planeEquation = (final ProcessingPolygon polygon) {
      final n = polygon.normal.z < 0 ? polygon.normal : -polygon.normal;
      final d = n.dot(polygon.points.first);
      return (final Vector3 v) => n.x * v.x + n.y * v.y + n.z * v.z - d;
    };

    // Mathematically spoken, a points lies "outside" of a plane if that
    // equation results into something greater than 0.
    // I add a small margin, to avoid flickering when points occupy the very
    // same space, which is quite common as polygons are composited
    // to seamless hulls.

    const margin = 0.0001;

    final otherIsOutside =
        other.points.every((v) => planeEquation(this)(v) < margin);
    final otherIsInside =
        other.points.every((v) => planeEquation(this)(v) > -margin);
    final thisIsOutside = points.every((v) => planeEquation(other)(v) < margin);
    final thisIsInside = points.every((v) => planeEquation(other)(v) > -margin);

    if (otherIsOutside || thisIsInside) {
      return occludingOther;
    }

    if (thisIsOutside || otherIsInside) {
      return occludedByOther;
    }

    taggedAsIntersecting = true;

    return occludingOther;
  }
}

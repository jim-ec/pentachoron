import 'dart:math';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:tesserapp/generic/number_range.dart';
import 'package:tesserapp/geometry/polygon.dart';
import 'package:tesserapp/geometry/vector.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3, Matrix4;

typedef double _PlaneEquation(final Vector3 v);

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
  
  /// True if this polygons intersects with an other polygon.
  /// Set during depth sorting, i.e. a call to [compareTo].
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
  /// This polygon should reside in projection space in order to construct
  /// proper normal vectors.
  ///
  /// Polygons are expected to not intersect each other.
  /// Cyclic occluding is not supported and will result into
  /// incorrect sorting, as this simple algorithm is not able
  /// to split polygons.
  ///
  /// The sorting algorithm is taken from this [SigGraph Letter](https://www.siggraph.org/education/materials/HyperGraph/scanline/visibility/painter.htm),
  /// although it's not fully implemented.
  ///
  /// Intersecting polygons cannot be sorted correctly and are flagged
  /// as being [taggedAsIntersecting]. They can be removed in the later stages
  /// of the painter's pipeline as an optimization, as intersection should
  /// only appear *within* volumes and therefore represent invisible geometry.
  ///
  /// Cyclic occluding polygons lead to infinite sorting.
  @override
  int compareTo(final ProcessingPolygon other) {
    const occludingOther = 1;
    const occludedByOther = -1;

    // Check if both polygons occupy different z-ranges.
    // If they do, it's trivial to compare the occupied z-ranges and
    // order the polygons accordingly.
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

    // Otherwise, check if both polygon lying completely on one side
    // relative to the plane equation of the other polygon.
    //
    // Plane equation:
    // ax + bx + cx - d = 0
    // Where a = n.x, b = n.y, c = n.z
    //
    // If the result is greater than 0, the point lies in front of the plane.

    // I add a small margin, to avoid flickering when points occupy the very
    // same space, which is quite common as polygons are composited
    // to seamless hulls.
    const margin = 0.0001;

    if (other.points.every((v) => _planeEquation(v) < margin) ||
        points.every((v) => other._planeEquation(v) > -margin)) {
      return occludingOther;
    }

    if (points.every((v) => other._planeEquation(v) < margin) ||
        other.points.every((v) => _planeEquation(v) > -margin)) {
      return occludedByOther;
    }

    taggedAsIntersecting = true;

    return 0;
  }

  /// Returns a function forming the plane equation for [polygon].
  _PlaneEquation get _planeEquation {
    // Normal is taken is such a manner that is guaranteed to point into
    // positive z direction, i.e. against the view direction.
    final n = normal.z < 0 ? normal : -normal;
    final d = n.dot(points.first);
    return (final Vector3 v) => n.x * v.x + n.y * v.y + n.z * v.z - d;
  }
}

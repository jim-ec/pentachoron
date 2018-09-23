import 'dart:math';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:Pentachoron/generic/number_range.dart';
import 'package:Pentachoron/geometry/matrix.dart';
import 'package:Pentachoron/geometry/polygon.dart';
import 'package:Pentachoron/geometry/tolerance.dart';
import 'package:Pentachoron/geometry/vector.dart';

/// A polygon wrapper adding pipeline processing functionality to it.
/// It bundles per-geometry features like outlining into the polygons,
/// as they are decoupled from their geometries in order to perform
/// depth sorting.
class IlluminatedPolygon implements Comparable<IlluminatedPolygon> {
  
  /// Resulting polygon in current space.
  final Polygon polygon;

  /// Color of this polygon.
  final Color color;

  IlluminatedPolygon(
    this.polygon,
    final Color color,
      final Vector lightDirection,
  ) : color = Color.lerp(Color(0xff000000), color, remap(Vector.dot(polygon.normal, lightDirection).abs(), 0.0, 1.0, 0.2, 1.2));

  IlluminatedPolygon.transformed(
      final IlluminatedPolygon other,
      final Matrix matrix
      )
      : polygon = other.polygon.transformed(matrix),
        color = other.color;

  /// Computes the polygon representing this' perspective division.
  /// The z-component is negated, after which the x and y component
  /// are divided through that negated z value.
  /// The v value is than kept as-is, i.e. it need not to be -1.0
  /// after the division.
  IlluminatedPolygon.perspectiveDivision(
      final IlluminatedPolygon other,
      )
      : polygon = other.polygon.map((v) => -Vector(v.x / v.z, v.y / v.z, v.z)),
        color = other.color;

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
  /// Intersecting and cyclic occluding polygons cannot be sorted correctly and
  /// are flagged as being [taggedAsIntersecting].
  /// They can be removed in the later stages of the painter's pipeline as an
  /// optimization, as intersection should only appear *within* volumes and
  /// therefore represent invisible geometry.
  @override
  int compareTo(final IlluminatedPolygon other) {
    const occludingOther = 1;
    const occludedByOther = -1;
    const undecidable = 0;

    // Check if both polygons occupy different z-ranges.
    // If they do, it's trivial to compare the occupied z-ranges and
    // order the polygons accordingly.
    final zMin = polygon.points.map((v) => v.z).reduce((a, b) => min(a, b));
    final zMax = polygon.points.map((v) => v.z).reduce((a, b) => max(a, b));
    final zMinOther =
        other.polygon.points.map((v) => v.z).reduce((a, b) => min(a, b));
    final zMaxOther =
        other.polygon.points.map((v) => v.z).reduce((a, b) => max(a, b));
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

    final e0 = (polygon.normal.z < 0)
        ? polygon.planeEquation
        : Polygon.flip(polygon).planeEquation;

    final e1 = (other.polygon.normal.z < 0)
        ? other.polygon.planeEquation
        : Polygon.flip(other.polygon).planeEquation;

    if (other.polygon.points.every((v) => e0(v) < tolerance) ||
        polygon.points.every((v) => e1(v) > -tolerance)) {
      return occludingOther;
    }

    if (polygon.points.every((v) => e1(v) < tolerance) ||
        other.polygon.points.every((v) => e0(v) > -tolerance)) {
      return occludedByOther;
    }

    return undecidable;
  }
}

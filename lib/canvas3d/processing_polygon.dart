import 'dart:math';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:tesserapp/generic/number_range.dart';
import 'package:tesserapp/geometry/matrix.dart';
import 'package:tesserapp/geometry/polygon.dart';
import 'package:tesserapp/geometry/tolerance.dart';
import 'package:tesserapp/geometry/vector.dart';

/// A polygon wrapper adding pipeline processing functionality to it.
/// It bundles per-geometry features like outlining into the polygons,
/// as they are decoupled from their geometries in order to perform
/// depth sorting.
class ProcessingPolygon implements Comparable<ProcessingPolygon> {
  /// Resulting polygon in current space.
  final Polygon polygon;

  /// Color of this polygon.
  final Color color;

  ProcessingPolygon(
    this.polygon,
    this.color,
  );

  /// Computes the polygon representing this' perspective division.
  /// The z-component is negated, after which the x and y component
  /// are divided through that negated z value.
  /// The v value is than kept as-is, i.e. it need not to be -1.0
  /// after the division.
  ProcessingPolygon get perspectiveDivision => ProcessingPolygon(
        polygon.map((v) => -Vector(v.x / v.z, v.y / v.z, v.z)),
        color,
      );

  /// Return a transformed version of this polygon.
  /// To transform the polygon using perspective matrices,
  /// use [perspectiveTransformed] instead.
  ProcessingPolygon transformed(final Matrix matrix) =>
      ProcessingPolygon(polygon.map((v) => matrix.transform(v)), color);

  /// Return a re-colored version of this polygon.
  /// [lightDirection] defines the direction of parallel light rays,
  /// used to illuminate the polygon.
  ///
  /// [lightDirection] is assumed to be in the same space as this polygon.
  ProcessingPolygon illuminated(final Vector lightDirection) {
    final luminance = Vector.dot(polygon.normal, lightDirection).abs();
    final softenLuminance = remap(luminance, 0.0, 1.0, 0.2, 1.2);
    return ProcessingPolygon(
      polygon,
      Color.lerp(Color(0xff000000), color, softenLuminance),
    );
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
  /// Intersecting and cyclic occluding polygons cannot be sorted correctly and
  /// are flagged as being [taggedAsIntersecting].
  /// They can be removed in the later stages of the painter's pipeline as an
  /// optimization, as intersection should only appear *within* volumes and
  /// therefore represent invisible geometry.
  @override
  int compareTo(final ProcessingPolygon other) {
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
        : polygon.flip.planeEquation;

    final e1 = (other.polygon.normal.z < 0)
        ? other.polygon.planeEquation
        : other.polygon.flip.planeEquation;

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

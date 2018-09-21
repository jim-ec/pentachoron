import 'dart:math';

import 'package:tesserapp/geometry/polygon.dart';
import 'package:tesserapp/geometry/tolerance.dart';

const _undecidable = 0;
const _occludingOther = 1;
const _occludedByOther = -1;

int depthCompareBarycenter(final Polygon a, final Polygon b) {
  if (a.barycenter.z < b.barycenter.z) {
    return _occludingOther;
  } else {
    return _occludedByOther;
  }
}

int depthComparePrecise(final Polygon a, final Polygon b) {
  // Check if both polygons occupy different z-ranges.
  // If they do, it's trivial to compare the occupied z-ranges and
  // order the polygons accordingly.
  final zMinA = a.points.map((v) => v.z).reduce((a, b) => min(a, b));
  final zMaxA = a.points.map((v) => v.z).reduce((a, b) => max(a, b));
  final zMinB = b.points.map((v) => v.z).reduce((a, b) => min(a, b));
  final zMaxB = b.points.map((v) => v.z).reduce((a, b) => max(a, b));
  if (zMinA > zMaxB) {
    return _occludingOther;
  }
  if (zMaxA < zMinB) {
    return _occludedByOther;
  }

  // Otherwise, check if both polygon lying completely on one side
  // relative to the plane equation of the other polygon.
  //
  // Plane equation:
  // ax + bx + cx - d = 0
  // Where a = n.x, b = n.y, c = n.z
  //
  // If the result is greater than 0, the point lies in front of the plane.

  final e0 = (a.normal.z < 0) ? a.planeEquation : Polygon.flip(a).planeEquation;

  final e1 = (b.normal.z < 0) ? b.planeEquation : Polygon.flip(b).planeEquation;

  if (b.points.every((v) => e0(v) < tolerance) ||
      a.points.every((v) => e1(v) > -tolerance)) {
    return _occludingOther;
  }

  if (a.points.every((v) => e1(v) < tolerance) ||
      b.points.every((v) => e0(v) > -tolerance)) {
    return _occludedByOther;
  }

  return _undecidable;
}

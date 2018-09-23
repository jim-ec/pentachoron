import 'dart:math';

import 'package:meta/meta.dart';
import 'package:pentachoron/geometry/drawable.dart';
import 'package:pentachoron/geometry/line.dart';
import 'package:pentachoron/geometry/matrix.dart';
import 'package:pentachoron/geometry/vector.dart';

/// The base four-dimensional geometry, in the same manner as the triangle
/// forms the base for 2d geometries and the tetrahedron for 3d geometries.
///
/// Reference:
/// https://en.wikipedia.org/wiki/5-cell
///
/// A pentachoron is defined through 5 cells.
@immutable
class Pentachoron implements Drawable {
  final Iterable<Vector> points;

  @override
  Iterable<Line> lines(final Matrix matrix) {
    final p = matrix.transformAll(points);
    return [
      Line.fromPoints(p.elementAt(0), p.elementAt(1)),
      Line.fromPoints(p.elementAt(0), p.elementAt(2)),
      Line.fromPoints(p.elementAt(0), p.elementAt(3)),
      Line.fromPoints(p.elementAt(0), p.elementAt(4)),
      Line.fromPoints(p.elementAt(1), p.elementAt(2)),
      Line.fromPoints(p.elementAt(1), p.elementAt(3)),
      Line.fromPoints(p.elementAt(1), p.elementAt(4)),
      Line.fromPoints(p.elementAt(2), p.elementAt(3)),
      Line.fromPoints(p.elementAt(2), p.elementAt(4)),
      Line.fromPoints(p.elementAt(3), p.elementAt(4)),
    ];
  }

  Pentachoron(this.points) {
    assert(points.length == 5, "Each pentachoron must have 5 points");
  }

  /// Construct a pentachoron with the edge length 2.
  /// The base cell is origin-centered.
  Pentachoron.simple()
      : this([
          Vector(1.0, 1.0, 1.0, -1.0 / sqrt(5.0)),
          Vector(1.0, -1.0, -1.0, -1.0 / sqrt(5.0)),
          Vector(-1.0, 1.0, -1.0, -1.0 / sqrt(5.0)),
          Vector(-1.0, -1.0, 1.0, -1.0 / sqrt(5.0)),
          Vector(0.0, 0.0, 0.0, sqrt(5.0) - 1.0 / sqrt(5.0)),
        ]);

//  Tetrahedron intersected()
}

import 'dart:math';

import 'package:meta/meta.dart';
import 'package:tesserapp/geometry/drawable.dart';
import 'package:tesserapp/geometry/line.dart';
import 'package:tesserapp/geometry/matrix.dart';
import 'package:tesserapp/geometry/vector.dart';
import 'package:tesserapp/geometry/volume.dart';

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
  final Iterable<Line> lines;

  Pentachoron(this.points)
      : lines = [
          Line.fromPoints(points.elementAt(0), points.elementAt(1)),
          Line.fromPoints(points.elementAt(0), points.elementAt(2)),
          Line.fromPoints(points.elementAt(0), points.elementAt(3)),
          Line.fromPoints(points.elementAt(0), points.elementAt(4)),
          Line.fromPoints(points.elementAt(1), points.elementAt(2)),
          Line.fromPoints(points.elementAt(1), points.elementAt(3)),
          Line.fromPoints(points.elementAt(1), points.elementAt(4)),
          Line.fromPoints(points.elementAt(2), points.elementAt(3)),
          Line.fromPoints(points.elementAt(2), points.elementAt(4)),
          Line.fromPoints(points.elementAt(3), points.elementAt(4)),
        ] {
    assert(points.length == 5, "Each pentachoron must have 5 points");
  }

  /// Construct a pentachoron with the edge length 2.
  /// The base cell is origin-centered.
  Pentachoron.simple(
    final Matrix matrix,
  ) : this(matrix.transformAll([
          Vector(1.0, 1.0, 1.0, -1.0 / sqrt(5.0)),
          Vector(1.0, -1.0, -1.0, -1.0 / sqrt(5.0)),
          Vector(-1.0, 1.0, -1.0, -1.0 / sqrt(5.0)),
          Vector(-1.0, -1.0, 1.0, -1.0 / sqrt(5.0)),
          Vector(0.0, 0.0, 0.0, sqrt(5.0) - 1.0 / sqrt(5.0)),
        ]));

  @override
  Volume get intersection {
    final intersectingPoints =
        lines.map((line) => line.intersection).where((line) => line != null);
    return Volume(intersectingPoints);
  }

//  Tetrahedron intersected()
}

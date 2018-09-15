import 'dart:math';

import 'package:meta/meta.dart';
import 'package:tesserapp/geometry/polygon.dart';
import 'package:tesserapp/geometry/vector.dart';

String get timesSymbol => "\u{22c5}";

String get lambdaSymbol => "\u{03bb}";

String get assignedSymbol => "\u{2254}";

@immutable
class Line {
  final Vector a, d;

  Line.fromDirection(this.a, this.d);

  Line.fromPoints(final Vector a, final Vector b)
      : this.fromDirection(a, b - a);

  Vector call(final double lambda) => a + d * lambda;

  @override
  String toString() => "g : x = $a + $lambdaSymbol$d";

  Vector intersection(final int componentIndex) {
    final lambda = -a[componentIndex] / d[componentIndex];
    if (lambda >= 0.0 && lambda <= 1.0 && lambda.isFinite) {
      return this(lambda);
    } else {
      return null;
    }
  }
}

@immutable
class Tetrahedron {
  final Iterable<Vector> points;
  final Iterable<Line> lines;

  Tetrahedron(this.points)
      : lines = [
          Line.fromPoints(points.elementAt(0), points.elementAt(3)),
          Line.fromPoints(points.elementAt(1), points.elementAt(3)),
          Line.fromPoints(points.elementAt(2), points.elementAt(3)),
          Line.fromPoints(points.elementAt(0), points.elementAt(1)),
          Line.fromPoints(points.elementAt(1), points.elementAt(2)),
          Line.fromPoints(points.elementAt(2), points.elementAt(0))
        ] {
    assert(points.length == 4, "Each tetrahedron must have 4 points");
  }

  Polygon intersection(final int componentIndex) => Polygon(lines
      .map((line) => line.intersection(componentIndex))
      .where((line) => line != null));
}

/// The base four-dimensional geometry, in the same manner as the triangle
/// forms the base for 2d geometries and the tetrahedron for 3d geometries.
///
/// Reference:
/// https://en.wikipedia.org/wiki/5-cell
///
/// A pentachoron is defined through 5 cells.
@immutable
class Pentachoron {
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
  Pentachoron.simple()
      : this([
          Vector(1.0, 1.0, 1.0, -1.0 / sqrt(5.0)),
          Vector(1.0, -1.0, -1.0, -1.0 / sqrt(5.0)),
          Vector(-1.0, 1.0, -1.0, -1.0 / sqrt(5.0)),
          Vector(-1.0, -1.0, 1.0, -1.0 / sqrt(5.0)),
          Vector(0.0, 0.0, 0.0, sqrt(5.0) - 1.0 / sqrt(5.0)),
        ]);

  Tetrahedron intersection(final int componentIndex) {
    final intersectingPoints = lines
        .map((line) => line.intersection(componentIndex))
        .where((line) => line != null);

    assert(intersectingPoints.length <= 4, "Impossible count of intersections");

    if (intersectingPoints.length < 3) {
      return null;
    } else {
      return Tetrahedron(intersectingPoints);
    }
  }

//  Tetrahedron intersected()
}

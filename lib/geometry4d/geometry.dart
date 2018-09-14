import 'dart:math';

import 'package:meta/meta.dart';
import 'package:tesserapp/geometry4d/vector.dart';

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
  
  Vector get intersection {
    final lambda = -a.z / d.z;
    if (lambda >= 0.0 && lambda <= 1.0 && lambda.isFinite) {
      return this(lambda);
    } else {
      return null;
    }
  }
}

@immutable
class Triangle {
  final List<Vector> points;

  Triangle(this.points) {
    assert(points.length == 3);
  }
}

@immutable
class Tetrahedron {
  final List<Vector> points;
  final List<Line> lines;

  Tetrahedron(this.points)
      : lines = [
          Line.fromPoints(points[0], points[3]),
          Line.fromPoints(points[1], points[3]),
          Line.fromPoints(points[2], points[3]),
          Line.fromPoints(points[0], points[1]),
          Line.fromPoints(points[1], points[2]),
          Line.fromPoints(points[2], points[0])
        ] {
    assert(points.length == 4, "Each tetrahedron must have 4 points");
  }

  Triangle get intersection {
    final intersectingPoints = lines
        .map((line) => line.intersection)
        .where((line) => line != null)
        .toList();

    assert(intersectingPoints.length <= 3, "Impossible count of intersections");

    if (intersectingPoints.length < 3) {
      return null;
    } else {
      return Triangle(intersectingPoints);
    }
  }
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
  final List<Vector> points;
  final List<Tetrahedron> cells;

  Pentachoron(this.points)
      : cells = [
          Tetrahedron([points[0], points[1], points[2], points[3]]),
          Tetrahedron([points[0], points[1], points[2], points[4]]),
          Tetrahedron([points[1], points[2], points[3], points[4]]),
          Tetrahedron([points[2], points[3], points[1], points[4]]),
          Tetrahedron([points[3], points[1], points[2], points[4]])
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

//  Tetrahedron intersected()
}

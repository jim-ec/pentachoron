import 'dart:math';

import 'package:meta/meta.dart';

String get times => "\u{22c5}";

String get lambda => "\u{03bb}";

@immutable
class Vector {
  final double x, y, z, w;

  const Vector(
    this.x,
    this.y,
    this.z, [
    this.w = 0.0,
  ]);

  const Vector.zero() : this.of(0.0);

  const Vector.of(final double c) : this(c, c, c, c);

  const Vector.ofX(final double c) : this(c, 0.0, 0.0, 0.0);

  const Vector.ofY(final double c) : this(0.0, c, 0.0, 0.0);

  const Vector.ofZ(final double c) : this(0.0, 0.0, c, 0.0);

  const Vector.ofW(final double c) : this(0.0, 0.0, 0.0, c);

  Vector.cross(final Vector a, final Vector b)
      : w = 0.0,
        x = a.y * b.z - a.z - b.y,
        y = a.z * b.x - a.x * b.z,
        z = a.x * b.y - a.y * b.x {
    assert(a.w == b.w, "Only 3d vectors can be crossed");
  }

  static double scalar(final Vector a, final Vector b) =>
      a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;

  Vector operator +(final Vector other) =>
      Vector(x + other.x, y + other.y, z + other.z, w + other.w);

  Vector operator *(final double c) => Vector(x * c, y * c, z * c, w * c);

  Vector operator -(final Vector other) => this + -other;

  Vector operator -() => Vector(-x, -y, -z, -w);

  @override
  String toString() => "[${x.toStringAsFixed(1)}, "
      "${y.toStringAsFixed(1)}, "
      "${z.toStringAsFixed(1)}, "
      "${w.toStringAsFixed(1)}]";

  double get length => sqrt(Vector.scalar(this, this));

  Vector get normalized => Vector(x / length, y / length, z / length);
}

@immutable
class Line {
  final Vector a, d;

  Line.fromDirection(this.a, this.d);

  Line.fromPoints(final Vector a, final Vector b)
      : this.fromDirection(a, b - a);

  Vector call(final double lambda) => a + d * lambda;

  @override
  String toString() => "g: x = $a + $lambda$d";
}

@immutable
class Plane {
  final Vector n;
  final double b;

  Plane.fromCoordinates({
    this.n,
    this.b,
  }) {
    assert(n.length != 0.0, "Normal vector of plane cannot have the length 0");
  }

  Plane.fromNormal({
    final Vector a,
    final Vector n,
  }) : this.fromCoordinates(n: n, b: Vector.scalar(a, n));

  Plane.fromTangents({
    final Vector a,
    final Vector tangent,
    final Vector bitangent,
  }) : this.fromNormal(a: a, n: Vector.cross(tangent, bitangent));

  Plane get normalized => Plane.fromCoordinates(n: n.normalized, b: b);

  /// Intersect a line with a plane, returning the point of intersection.
  /// If [line] is parallel to [plane], the result is `null`.
  Vector intersected(final Line line) {
    if (Vector.scalar(line.d, n) == 0.0) {
      return null;
    }

    final a = line.a;
    final d = line.d;
    
    final lambda = -(n.x * a.x + n.y * a.y + n.z * a.z - b) /
        (n.x * d.x + n.y * d.y + n.z * d.z);
    
    if(lambda < 0.0 || lambda > 1.0) {
      return null;
    }

    return line(lambda);
  }

  @override
  String toString() => "E: ${n.x.toStringAsFixed(1)}x "
      "+ ${n.y.toStringAsFixed(1)}y "
      "+ ${n.z.toStringAsFixed(1)}z - "
      "${b.toStringAsFixed(1)} = 0";
}

@immutable
class Volume {
  final Vector a, normal, binormal;

  Volume.fromNormals({this.a, this.normal, this.binormal});

  Volume.fromTangents({
    final Vector a,
    final Vector tangent,
    final Vector bitangent,
    final Vector tritangent,
  }) : this.fromNormals(
            a: a,
            normal: Vector.cross(tangent, bitangent),
            binormal: Vector.cross(bitangent, tritangent));

  /// Intersect a line with a plane, returning the point of intersection.
  /// If [line] is parallel to [plane], the result is `null`.
//  Vector intersected(final Line line) {
//    if (Vector.scalar(line.d, normal) == 0.0 ||
//        Vector.scalar(line.d, binormal) == 0.0) {
//      return null;
//    }
//
//    final a = line.a;
//    final d = line.d;
//
//    return line(-(n.x * a.x + n.y * a.y + n.z * a.z - b) /
//        (n.x * d.x + n.y * d.y + n.z * d.z));
//  }
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

  Triangle intersected(final Plane plane) {
    final intersectingPoints = lines
        .map((line) => plane.intersected(line))
        .where((line) => line != null)
        .toList();
    
    if(intersectingPoints.length < 3) {
      return null;
    }
    else {
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

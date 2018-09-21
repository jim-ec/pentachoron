import 'package:meta/meta.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry/matrix.dart';
import 'package:tesserapp/geometry/tetrahedron.dart';
import 'package:tesserapp/geometry/vector.dart';

typedef double _PlaneEquation(final Vector v);

/// A polygon consists of an arbitrary count of vertices.
///
/// All vertices must share the same mathematical plane, i.e. the polygon has
/// a single normal vector.
@immutable
class Polygon {
  final Iterable<Vector> points;

  final Vector normal;

  final _PlaneEquation planeEquation;

  final Vector barycenter;
  
  // TODO: delete as misleading
  Polygon.fromFixedPointsAndNormal(this.points, this.normal)
      : barycenter = points.reduce((a, b) => a + b) / points.length.toDouble(),
        planeEquation = (() {
          if (points.length < 3) {
            return (v) => double.nan;
          }

          final Vector n = normal;
          final double d = Vector.dot(n, points.first);

          return (final v) => n.x * v.x + n.y * v.y + n.z * v.z - d;
        })();

  Polygon.fromFixedPoints(final Iterable<Vector> points)
      : this.fromFixedPointsAndNormal(
            points,
            points.length >= 3
                ? Vector.cross(
                    points.elementAt(1) - points.elementAt(0),
                    points.elementAt(2) - points.elementAt(0),
                  ).normalized
                : Vector.zero());

  Polygon.fromUnsortedPoints(final Iterable<Vector> points)
      : this.fromFixedPoints(_sortPolygonPoints(points));
  
  factory Polygon.flip(final Polygon other) {
    if(other.points.length < 3) return other;
    else {
      return Polygon.fromFixedPoints([
        other.points.elementAt(0),
        other.points.elementAt(2),
        other.points.elementAt(1),
      ] + other.points.skip(3).toList(growable: false));
    }
  }

  static Iterable<Vector> _sortPolygonPoints(final Iterable<Vector> points) {
    if (points.length <= 3) return points;
    final barycenter = Vector.barycenter(points);

    final sortedAngles = points.map((v) {
      final delta = barycenter - v;
      return _PointAngle(v, Angle.atanFullTurn(delta.y, delta.x));
    }).toList(growable: false)
      ..sort();
    return sortedAngles.map((v) => v.v);
  }

  Polygon map(Vector f(Vector v)) => Polygon.fromFixedPoints(points.map(f));

  /// Return a transformed version of this polygon.
  Polygon transformed(final Matrix matrix) => map((v) => matrix.transform(v));

  static Iterable<Polygon> tetrahedron(final Tetrahedron t) => t != null
      ? [
          Polygon.fromUnsortedPoints([
            t.points.elementAt(0),
            t.points.elementAt(1),
            t.points.elementAt(2),
          ]),
          Polygon.fromUnsortedPoints([
            t.points.elementAt(0),
            t.points.elementAt(1),
            t.points.elementAt(3),
          ]),
          Polygon.fromUnsortedPoints([
            t.points.elementAt(1),
            t.points.elementAt(2),
            t.points.elementAt(3),
          ]),
          Polygon.fromUnsortedPoints([
            t.points.elementAt(2),
            t.points.elementAt(0),
            t.points.elementAt(3),
          ]),
        ]
      : [];

  static Iterable<Polygon> cube({
    final Vector center,
    final double sideLength,
  }) {
    final a = sideLength / 2;
    final positions = [
      center + Vector(a, a, a),
      center + Vector(a, a, -a),
      center + Vector(a, -a, a),
      center + Vector(a, -a, -a),
      center + Vector(-a, a, a),
      center + Vector(-a, a, -a),
      center + Vector(-a, -a, a),
      center + Vector(-a, -a, -a),
    ];

    return [
      Polygon.fromUnsortedPoints(
          [positions[0], positions[1], positions[3], positions[2]]),
      Polygon.fromUnsortedPoints(
          [positions[1], positions[5], positions[7], positions[3]]),
      Polygon.fromUnsortedPoints(
          [positions[5], positions[4], positions[6], positions[7]]),
      Polygon.fromUnsortedPoints(
          [positions[4], positions[0], positions[2], positions[6]]),
      Polygon.fromUnsortedPoints(
          [positions[0], positions[4], positions[5], positions[1]]),
      Polygon.fromUnsortedPoints(
          [positions[2], positions[3], positions[7], positions[6]]),
    ];
  }

  static Iterable<Polygon> pyramid(
          {final double edgeLength, final double height}) =>
      [
        Polygon.fromUnsortedPoints([
          Vector(edgeLength / 2, edgeLength / 2, 0.0),
          Vector(edgeLength / 2, -edgeLength / 2, 0.0),
          Vector(-edgeLength / 2, -edgeLength / 2, 0.0),
          Vector(-edgeLength / 2, edgeLength / 2, 0.0),
        ]),
        Polygon.fromUnsortedPoints([
          Vector(-edgeLength / 2, -edgeLength / 2, 0.0),
          Vector(-edgeLength / 2, edgeLength / 2, 0.0),
          Vector(0.0, 0.0, height),
        ]),
        Polygon.fromUnsortedPoints([
          Vector(edgeLength / 2, -edgeLength / 2, 0.0),
          Vector(-edgeLength / 2, -edgeLength / 2, 0.0),
          Vector(0.0, 0.0, height),
        ]),
        Polygon.fromUnsortedPoints([
          Vector(edgeLength / 2, edgeLength / 2, 0.0),
          Vector(edgeLength / 2, -edgeLength / 2, 0.0),
          Vector(0.0, 0.0, height),
        ]),
        Polygon.fromUnsortedPoints([
          Vector(-edgeLength / 2, edgeLength / 2, 0.0),
          Vector(edgeLength / 2, edgeLength / 2, 0.0),
          Vector(0.0, 0.0, height),
        ]),
      ];
}

class _PointAngle implements Comparable<_PointAngle> {
  final Vector v;
  final Angle angle;

  _PointAngle(this.v, this.angle);

  @override
  int compareTo(_PointAngle other) => angle < other.angle ? -1 : 1;

  @override
  String toString() => "Angle to $v amounts $angle";
}

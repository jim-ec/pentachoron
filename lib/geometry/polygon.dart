import 'package:meta/meta.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry/tetrahedron.dart';
import 'package:tesserapp/geometry/vector.dart';

/// A polygon consists of an arbitrary count of vertices.
///
/// All vertices must share the same mathematical plane, i.e. the polygon has
/// a single normal vector.
@immutable
class Polygon {
  final Iterable<Vector> points;

  Polygon(final Iterable<Vector> points) : points = _sortPolygonPoints(points);

  static Iterable<Vector> _sortPolygonPoints(final Iterable<Vector> points) {
    if (points.length <= 3) return points;
    final barycenter = Vector.barycenter(points);

    final sortedAngles = points.map((v) {
      final delta = barycenter - v;
      return _PointAngle(v, Angle.atan360(delta.y, delta.x));
    }).toList(growable: false)
      ..sort();
    return sortedAngles.map((v) => v.v);
  }
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

Iterable<Polygon> tetrahedron(final Tetrahedron t) => t != null
    ? [
        Polygon([
          t.points.elementAt(0),
          t.points.elementAt(1),
          t.points.elementAt(2),
        ]),
        Polygon([
          t.points.elementAt(0),
          t.points.elementAt(1),
          t.points.elementAt(3),
        ]),
        Polygon([
          t.points.elementAt(1),
          t.points.elementAt(2),
          t.points.elementAt(3),
        ]),
        Polygon([
          t.points.elementAt(2),
          t.points.elementAt(0),
          t.points.elementAt(3),
        ]),
      ]
    : [];

Iterable<Polygon> cube({
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
    Polygon([positions[0], positions[1], positions[3], positions[2]]),
    Polygon([positions[1], positions[5], positions[7], positions[3]]),
    Polygon([positions[5], positions[4], positions[6], positions[7]]),
    Polygon([positions[4], positions[0], positions[2], positions[6]]),
    Polygon([positions[0], positions[4], positions[5], positions[1]]),
    Polygon([positions[2], positions[3], positions[7], positions[6]]),
  ];
}

Iterable<Polygon> pyramid({final double edgeLength, final double height}) => [
      Polygon([
        Vector(edgeLength / 2, edgeLength / 2, 0.0),
        Vector(edgeLength / 2, -edgeLength / 2, 0.0),
        Vector(-edgeLength / 2, -edgeLength / 2, 0.0),
        Vector(-edgeLength / 2, edgeLength / 2, 0.0),
      ]),
      Polygon([
        Vector(-edgeLength / 2, -edgeLength / 2, 0.0),
        Vector(-edgeLength / 2, edgeLength / 2, 0.0),
        Vector(0.0, 0.0, height),
      ]),
      Polygon([
        Vector(edgeLength / 2, -edgeLength / 2, 0.0),
        Vector(-edgeLength / 2, -edgeLength / 2, 0.0),
        Vector(0.0, 0.0, height),
      ]),
      Polygon([
        Vector(edgeLength / 2, edgeLength / 2, 0.0),
        Vector(edgeLength / 2, -edgeLength / 2, 0.0),
        Vector(0.0, 0.0, height),
      ]),
      Polygon([
        Vector(-edgeLength / 2, edgeLength / 2, 0.0),
        Vector(edgeLength / 2, edgeLength / 2, 0.0),
        Vector(0.0, 0.0, height),
      ]),
    ];

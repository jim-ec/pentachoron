import 'package:meta/meta.dart';
import 'package:tesserapp/geometry4d/vector.dart';

/// A polygon consists of an arbitrary count of vertices.
///
/// All vertices must share the same mathematical plane, i.e. the polygon has
/// a single normal vector.
@immutable
class Polygon {
  final List<Vector> points;
  Polygon(this.points);
}

List<Polygon> cube({
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

List<Polygon> pyramid({final double edgeLength, final double height}) => [
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


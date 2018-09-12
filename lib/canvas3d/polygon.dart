import 'package:meta/meta.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3;

/// A polygon consists of an arbitrary count of vertices.
///
/// All vertices must share the same mathematical plane, i.e. the polygon has
/// a single normal vector.
@immutable
class Polygon {
  final List<Vector3> positions;

  Polygon(this.positions) {
    assert(positions.length >= 3, "Each polygon must have at least 3 vertices");
  }

  Polygon get flippedNormal => Polygon(positions.reversed.toList());
}

List<Polygon> cube({
  final Vector3 center,
  final double sideLength,
}) {
  final a = sideLength / 2;
  final positions = [
    center + Vector3(a, a, a),
    center + Vector3(a, a, -a),
    center + Vector3(a, -a, a),
    center + Vector3(a, -a, -a),
    center + Vector3(-a, a, a),
    center + Vector3(-a, a, -a),
    center + Vector3(-a, -a, a),
    center + Vector3(-a, -a, -a),
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
        Vector3(-edgeLength / 2, edgeLength / 2, 0.0),
        Vector3(-edgeLength / 2, -edgeLength / 2, 0.0),
        Vector3(edgeLength / 2, -edgeLength / 2, 0.0),
        Vector3(edgeLength / 2, edgeLength / 2, 0.0),
      ]),
      Polygon([
        Vector3(-edgeLength / 2, -edgeLength / 2, 0.0),
        Vector3(-edgeLength / 2, edgeLength / 2, 0.0),
        Vector3(0.0, 0.0, height),
      ]),
      Polygon([
        Vector3(edgeLength / 2, -edgeLength / 2, 0.0),
        Vector3(-edgeLength / 2, -edgeLength / 2, 0.0),
        Vector3(0.0, 0.0, height),
      ]),
      Polygon([
        Vector3(edgeLength / 2, edgeLength / 2, 0.0),
        Vector3(edgeLength / 2, -edgeLength / 2, 0.0),
        Vector3(0.0, 0.0, height),
      ]),
      Polygon([
        Vector3(-edgeLength / 2, edgeLength / 2, 0.0),
        Vector3(edgeLength / 2, edgeLength / 2, 0.0),
        Vector3(0.0, 0.0, height),
      ]),
    ];


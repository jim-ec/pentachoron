import 'package:quiver/iterables.dart';
import 'package:pentachoron/geometry/polygon.dart';
import 'package:pentachoron/geometry/tolerance.dart';
import 'package:pentachoron/geometry/vector.dart';

/// A volume of arbitrary shape.
/// Its polygonal hull is defined through a set of points.
class Volume {
  final Iterable<Vector> points;
  final Iterable<Polygon> hull;

  Volume(this.points) : hull = _generatePolygonsFromPointCloud(points);

  static Iterable<Polygon> _generatePolygonsFromPointCloud(
    final Iterable<Vector> points,
  ) {
    if (points.length < 3) {
      return [];
    }

    final barycenter =
        points.reduce((a, b) => a + b) / points.length.toDouble();
    final polygons = <Polygon>[];
    for (final a in range(0, points.length)) {
      for (final b in range(a + 1, points.length)) {
        for (final c in range(b + 1, points.length)) {
          final polygon = Polygon.fromUnsortedPoints([
            points.elementAt(a),
            points.elementAt(b),
            points.elementAt(c),
          ]);

          final solutions = points.map((v) {
            return polygon.planeEquation(v);
          });
          final allPositive = solutions.every((s) => s > -tolerance);
          final allNegative = solutions.every((s) => s < tolerance);

          final alignedPolygon = polygon.planeEquation(barycenter) < 0
            ? polygon
            : Polygon.flip(polygon);

          final colliding = polygons
            .where((other) => 
              (alignedPolygon.normal - other.normal).length < tolerance &&
              Vector.dot(alignedPolygon.normal, alignedPolygon.points.first) - Vector.dot(other.normal, other.points.first) < tolerance)
            .any((p) => alignedPolygon.alignedTrianglesOverlap(p));

          if ((allPositive || allNegative) && !colliding) {
            polygons.add(alignedPolygon);
          }
        }
      }
    }
    return polygons;
  }
}

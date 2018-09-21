import 'package:quiver/iterables.dart';
import 'package:tesserapp/geometry/polygon.dart';
import 'package:tesserapp/geometry/tolerance.dart';
import 'package:tesserapp/geometry/vector.dart';

/// A volume of arbitrary shape.
/// Its polygonal hull is defined through a set of points.
class Volume {
  final Iterable<Vector> points;
  final Iterable<Polygon> hull;

  Volume(this.points) : hull = _generatePolygonsFromPointCloud(points);

  static Iterable<Polygon> _generatePolygonsFromPointCloud(
    final Iterable<Vector> points,
  ) {
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
          
          if(allPositive || allNegative) {
            polygons.add(polygon);
          }
        }
      }
    }
    return polygons;
  }
}

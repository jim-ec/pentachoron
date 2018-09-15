import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry4d/geometry.dart';
import 'package:tesserapp/geometry4d/vector.dart';

Polygon polygonOfPoints(final List<Vector> points) =>
    Polygon(_sortPolygonPoints(points));

List<Polygon> polygonsOfPointLists(final List<List<Vector>> pointLists) =>
    pointLists.map((points) => polygonOfPoints(points));

@deprecated
List<Polygon> polygonsOfTetrahedron(final Tetrahedron t) => t != null
    ? [
        Polygon([
          Vector(t.points[0].x, t.points[0].y, t.points[0].z),
          Vector(t.points[1].x, t.points[1].y, t.points[1].z),
          Vector(t.points[2].x, t.points[2].y, t.points[2].z),
        ]),
        Polygon([
          Vector(t.points[0].x, t.points[0].y, t.points[0].z),
          Vector(t.points[1].x, t.points[1].y, t.points[1].z),
          Vector(t.points[3].x, t.points[3].y, t.points[3].z),
        ]),
        Polygon([
          Vector(t.points[1].x, t.points[1].y, t.points[1].z),
          Vector(t.points[2].x, t.points[2].y, t.points[2].z),
          Vector(t.points[3].x, t.points[3].y, t.points[3].z),
        ]),
        Polygon([
          Vector(t.points[2].x, t.points[2].y, t.points[2].z),
          Vector(t.points[0].x, t.points[0].y, t.points[0].z),
          Vector(t.points[3].x, t.points[3].y, t.points[3].z),
        ]),
      ]
    : [];

List<Vector> _sortPolygonPoints(final List<Vector> points) {
  if (points.length <= 3) return points;
  final barycenter = Vector.barycenter(points);

  final sortedAngles = points.map((v) {
    final delta = barycenter - v;
    return _PointAngle(v, Angle.atan360(delta.y, delta.x));
  }).toList();
  sortedAngles.sort();
  return sortedAngles.map((v) => v.v).toList();
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

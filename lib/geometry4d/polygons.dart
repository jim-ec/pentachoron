import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry4d/geometry.dart';
import 'package:tesserapp/geometry4d/vector.dart';

Polygon polygonOfPoints(final Iterable<Vector> points) =>
    Polygon(_sortPolygonPoints(points));

Iterable<Polygon> polygonsOfPointLists(
        final Iterable<Iterable<Vector>> pointLists) =>
    pointLists.map((points) => polygonOfPoints(points));

@deprecated
Iterable<Polygon> polygonsOfTetrahedron(final Tetrahedron t) => t != null
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

Iterable<Vector> _sortPolygonPoints(final Iterable<Vector> points) {
  if (points.length <= 3) return points;
  final barycenter = Vector.barycenter(points);

  final sortedAngles = points.map((v) {
    final delta = barycenter - v;
    return _PointAngle(v, Angle.atan360(delta.y, delta.x));
  }).toList()
    ..sort();
  return sortedAngles.map((v) => v.v);
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

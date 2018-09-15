import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry4d/geometry.dart';
import 'package:tesserapp/geometry4d/vector.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3;

Polygon polygonOfPoints(final List<Vector> points) =>
    Polygon(_sortPolygonPoints(points).map((v) => _toCanvasVector(v)).toList());

List<Polygon> polygonsOfPointLists(final List<List<Vector>> pointLists) =>
    pointLists.map((points) => polygonOfPoints(points));

@deprecated
List<Polygon> polygonsOfTetrahedron(final Tetrahedron t) => t != null
    ? [
        Polygon([
          Vector3(t.points[0].x, t.points[0].y, t.points[0].z),
          Vector3(t.points[1].x, t.points[1].y, t.points[1].z),
          Vector3(t.points[2].x, t.points[2].y, t.points[2].z),
        ]),
        Polygon([
          Vector3(t.points[0].x, t.points[0].y, t.points[0].z),
          Vector3(t.points[1].x, t.points[1].y, t.points[1].z),
          Vector3(t.points[3].x, t.points[3].y, t.points[3].z),
        ]),
        Polygon([
          Vector3(t.points[1].x, t.points[1].y, t.points[1].z),
          Vector3(t.points[2].x, t.points[2].y, t.points[2].z),
          Vector3(t.points[3].x, t.points[3].y, t.points[3].z),
        ]),
        Polygon([
          Vector3(t.points[2].x, t.points[2].y, t.points[2].z),
          Vector3(t.points[0].x, t.points[0].y, t.points[0].z),
          Vector3(t.points[3].x, t.points[3].y, t.points[3].z),
        ]),
      ]
    : [];

Vector3 _toCanvasVector(final Vector v) => Vector3(v.x, v.y, v.z);

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

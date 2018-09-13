
import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:tesserapp/geometry4d/geometry.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3;

List<Polygon> polygonsOfTriangle(final Triangle t) => [
  Polygon([
    Vector3(t.points[0].x, t.points[0].y, t.points[0].z),
    Vector3(t.points[1].x, t.points[1].y, t.points[1].z),
    Vector3(t.points[2].x, t.points[2].y, t.points[2].z),
  ]),
];

List<Polygon> polygonsOfTetrahedron(final Tetrahedron t) => [
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
];
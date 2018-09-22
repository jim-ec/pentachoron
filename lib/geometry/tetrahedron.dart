import 'package:meta/meta.dart';
import 'package:tesserapp/geometry/line.dart';
import 'package:tesserapp/geometry/polygon.dart';
import 'package:tesserapp/geometry/vector.dart';

@immutable
class Tetrahedron {
  final Iterable<Line> lines;

  Tetrahedron(final Iterable<Vector> points)
      : lines = [
          Line.fromPoints(points.elementAt(0), points.elementAt(3)),
          Line.fromPoints(points.elementAt(1), points.elementAt(3)),
          Line.fromPoints(points.elementAt(2), points.elementAt(3)),
          Line.fromPoints(points.elementAt(0), points.elementAt(1)),
          Line.fromPoints(points.elementAt(1), points.elementAt(2)),
          Line.fromPoints(points.elementAt(2), points.elementAt(0)),
        ] {
    assert(points.length == 4, "Each tetrahedron must have 4 points");
  }

  Polygon get intersection => Polygon.fromUnsortedPoints(lines
      .map((line) => line.intersection)
      .where((line) => line != null));
}

import 'package:meta/meta.dart';
import 'package:tesserapp/geometry/line.dart';
import 'package:tesserapp/geometry/polygon.dart';
import 'package:tesserapp/geometry/vector.dart';

@immutable
class Tetrahedron {
  final Iterable<Vector> points;
  final Iterable<Line> lines;

  Tetrahedron(this.points)
      : lines = [
          Line.fromPoints(points.elementAt(0), points.elementAt(3)),
          Line.fromPoints(points.elementAt(1), points.elementAt(3)),
          Line.fromPoints(points.elementAt(2), points.elementAt(3)),
          Line.fromPoints(points.elementAt(0), points.elementAt(1)),
          Line.fromPoints(points.elementAt(1), points.elementAt(2)),
          Line.fromPoints(points.elementAt(2), points.elementAt(0))
        ] {
    assert(points.length == 4, "Each tetrahedron must have 4 points");
  }

  Polygon intersection(final int componentIndex) => Polygon(lines
      .map((line) => line.intersection(componentIndex))
      .where((line) => line != null));
}

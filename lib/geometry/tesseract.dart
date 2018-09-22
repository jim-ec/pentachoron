import 'package:meta/meta.dart';
import 'package:quiver/iterables.dart';
import 'package:tesserapp/geometry/drawable.dart';
import 'package:tesserapp/geometry/line.dart';
import 'package:tesserapp/geometry/matrix.dart';
import 'package:tesserapp/geometry/vector.dart';

@immutable
class Tesseract implements Drawable {
  final Iterable<Vector> cubeA, cubeB;

  Tesseract(this.cubeA, this.cubeB) {
    assert(cubeA.length == 8);
    assert(cubeB.length == 8);
  }

  @override
  Iterable<Line> lines(final Matrix matrix) {
    final p0 = matrix.transformAll(cubeA);
    final p1 = matrix.transformAll(cubeB);

    return range(8)
        .map((i) => Line.fromPoints(p0.elementAt(i), p1.elementAt(i)));
  }

  factory Tesseract.extruded(
    final Iterable<Vector> points,
    final Vector d,
  ) {
    assert(points.length == 8);

    return Tesseract(points, points.map((v) => v + d));
  }

  factory Tesseract.simple() {
    return Tesseract.extruded([
      Vector(0.5, 0.5, 0.5, -0.5),
      Vector(0.5, 0.5, -0.5, -0.5),
      Vector(0.5, -0.5, 0.5, -0.5),
      Vector(0.5, -0.5, -0.5, -0.5),
      Vector(-0.5, 0.5, 0.5, -0.5),
      Vector(-0.5, 0.5, -0.5, -0.5),
      Vector(-0.5, -0.5, 0.5, -0.5),
      Vector(-0.5, -0.5, -0.5, -0.5),
    ], Vector.ofW(1.0));
  }
}

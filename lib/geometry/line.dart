import 'package:meta/meta.dart';
import 'package:pentachoron/geometry/vector.dart';

@immutable
class Line {
  final Vector a, d;

  Line.fromDirection(this.a, this.d);

  Line.fromPoints(final Vector a, final Vector b)
      : this.fromDirection(a, b - a);

  Vector call(final double lambda) => a + d * lambda;

  @override
  String toString() => "g : x = $a + \u{03bb}$d";

  Vector get intersection {
    final lambda = -a.w / d.w;
    if (lambda >= 0.0 && lambda <= 1.0 && lambda.isFinite) {
      return this(lambda);
    } else {
      return null;
    }
  }
}

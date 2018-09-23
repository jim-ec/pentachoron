import 'package:Pentachoron/geometry/line.dart';
import 'package:Pentachoron/geometry/matrix.dart';

abstract class Drawable {
  Iterable<Line> lines(final Matrix matrix);
}

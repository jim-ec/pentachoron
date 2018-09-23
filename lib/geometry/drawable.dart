import 'package:pentachoron/geometry/line.dart';
import 'package:pentachoron/geometry/matrix.dart';

abstract class Drawable {
  Iterable<Line> lines(final Matrix matrix);
}

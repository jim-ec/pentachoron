import 'package:tesserapp/geometry/line.dart';
import 'package:tesserapp/geometry/matrix.dart';

abstract class Drawable {
  Iterable<Line> lines(final Matrix matrix);
}

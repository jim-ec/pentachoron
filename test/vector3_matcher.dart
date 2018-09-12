import 'package:flutter_test/flutter_test.dart';
import 'package:tesserapp/generic/range.dart';
import 'package:vector_math/vector_math_64.dart';

class Vector3Matcher extends Matcher {
  final Vector3 expected;
  final double margin;

  Vector3Matcher(
    this.expected, {
    this.margin = 0.0001,
  });

  Vector3Matcher.of(
      final double x,
      final double y,
      final double z, {
        final double margin = 0.0001,
      }) : this(Vector3(x, y, z), margin: margin);

  Vector3Matcher.normalized(
      final double x,
      final double y,
      final double z, {
        final double margin = 0.0001,
      }) : this(Vector3(x, y, z).normalized(), margin: margin);

  @override
  Description describe(final Description description) => StringDescription();

  @override
  bool matches(final item, final Map matchState) {
    final actual = item as Vector3;

    for (var i in range(to: 3)) {
      if (actual.storage[i] < expected.storage[i] - margin ||
          actual.storage[i] > expected.storage[i] + margin) {
        return false;
      }
    }

    return true;
  }
}

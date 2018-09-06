

import 'package:flutter_test/flutter_test.dart';

class DoubleMatcher extends Matcher {
  final double expected;
  final double margin;
  
  DoubleMatcher(
      this.expected, {
        this.margin = 0.0001,
      });
  
  @override
  Description describe(final Description description) => StringDescription();
  
  @override
  bool matches(final item, final Map matchState) {
    final actual = item as double;
    return actual > expected - margin && actual < expected + margin;
  }
}

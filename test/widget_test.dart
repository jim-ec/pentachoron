import 'package:flutter_test/flutter_test.dart';

import 'package:tesserapp/generic/number_range.dart';

class DoubleMatcher extends Matcher {
  final double expected;
  final double margin;

  DoubleMatcher(
    this.expected, {
    this.margin = 0.0001,
  });

  @override
  Description describe(final Description description) =>
      StringDescription("Matches two double-precision floating points");

  @override
  bool matches(final item, final Map matchState) {
    final actual = item as double;
    return actual > expected - margin && actual < expected + margin;
  }
}

void main() {
  test('Number range remap', () {
    expect(remap(1.0, -1.0, 1.0, 0.0, 1.0), DoubleMatcher(1.0));
    expect(remap(-1.0, -1.0, 1.0, 0.0, 1.0), DoubleMatcher(0.0));
    expect(remap(0.0, -1.0, 1.0, 0.0, 1.0), DoubleMatcher(0.5));
  });

//  testWidgets('', (WidgetTester tester) async {
//    await tester.pumpWidget(TesserApp());
//  });
}

import 'package:flutter_test/flutter_test.dart';
import 'package:tesserapp/generic/number_range.dart';

import 'double_matcher.dart';

void main() {
  test("Number range remap", () {
    expect(remap(1.0, -1.0, 1.0, 0.0, 1.0), DoubleMatcher(1.0));
    expect(remap(-1.0, -1.0, 1.0, 0.0, 1.0), DoubleMatcher(0.0));
    expect(remap(0.0, -1.0, 1.0, 0.0, 1.0), DoubleMatcher(0.5));
  });
}

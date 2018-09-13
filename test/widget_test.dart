import 'package:flutter_test/flutter_test.dart';
import 'package:tesserapp/generic/number_range.dart';

import 'double_matcher.dart';

void main() {
  test("Number range remap", () {
    expect(remap(1.0, -1.0, 1.0, 0.0, 1.0), DoubleMatcher(1.0));
    expect(remap(-1.0, -1.0, 1.0, 0.0, 1.0), DoubleMatcher(0.0));
    expect(remap(0.0, -1.0, 1.0, 0.0, 1.0), DoubleMatcher(0.5));
  });

  group("Geometry", () {

//    test("", () {
//      final tetrahedron = Tetrahedron([
//        Vector.zero(),
//        Vector.ofX(1.0),
//        Vector.ofY(1.0),
//        Vector.ofZ(1.0),
//      ]);
//
//      final plane = Plane.fromNormal(a: Vector.ofZ(0.5), n: Vector.ofZ(1.0));
//
//      print(intersect(Line.fromPoints(Vector.ofX(1.0), Vector.ofZ(1.0)), plane));
//
//      final intersection = tetrahedron
//          .intersected(plane);
//
//      print(intersection.points);
//    });
  });

//  testWidgets('', (WidgetTester tester) async {
//    await tester.pumpWidget(TesserApp());
//  });
}

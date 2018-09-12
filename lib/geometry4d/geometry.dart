import 'dart:math';

import 'package:meta/meta.dart';

@immutable
class Vertex4 {
  final double x, y, z, w;

  const Vertex4(this.x, this.y, this.z, this.w);
}

@immutable
class Tetrahedron4 {
  final Vertex4 base0, base1, base2, tip;

  const Tetrahedron4(this.base0, this.base1, this.base2, this.tip);
  
//  List<Polygon> get polygons => [
//    Polygon()
//  ]
}

/// The base four-dimensional geometry, in the same manner as the triangle
/// forms the base for 2d geometries and the tetrahedron for 3d geometries.
///
/// Reference:
/// https://en.wikipedia.org/wiki/5-cell
///
/// A pentachoron is defined through 5 cells:
@immutable
class Pentachoron4 {
  final Vertex4 base0, base1, base2, baseTip, tip;
  final Tetrahedron4 baseCell, cell0, cell1, cell2, cell3;

  Pentachoron4(this.base0, this.base1, this.base2, this.baseTip, this.tip)
      : baseCell = Tetrahedron4(base0, base1, base2, baseTip),
        cell0 = Tetrahedron4(base0, base1, base2, tip),
        cell1 = Tetrahedron4(base1, base2, baseTip, tip),
        cell2 = Tetrahedron4(base2, baseTip, base1, tip),
        cell3 = Tetrahedron4(baseTip, base1, base2, tip);

  /// Construct a pentachoron with the edge length 2.
  /// The base cell is origin-centered.
  Pentachoron4.simple()
      : this(
          Vertex4(1.0 / sqrt(10), 1.0 / sqrt(6), -2.0 / sqrt(3), 0.0),
          Vertex4(1.0 / sqrt(10), -sqrt(3.0 / 2.0), 0.0, 0.0),
          Vertex4(-2.0 * sqrt(2.0 / 5.0), 0.0, 0.0, 0.0),
          Vertex4(1.0 / sqrt(10), 1.0 / sqrt(6), 1.0 / sqrt(3), 1.0),
          Vertex4(1.0 / sqrt(10), 1.0 / sqrt(6), 1.0 / sqrt(3), -1.0),
        );
}

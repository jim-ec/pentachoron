import 'dart:math';

import 'package:meta/meta.dart';
import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3;

@immutable
class Vertex4 {
  final double x, y, z, w;

  const Vertex4(this.x, this.y, this.z, this.w);
}

@immutable
class Tetrahedron4 {
  final Vertex4 base0, base1, base2, tip;

  const Tetrahedron4(this.base0, this.base1, this.base2, this.tip);

  List<Polygon> get polygons => [
        Polygon([
          Vector3(base0.x, base0.y, base0.z),
          Vector3(base1.x, base1.y, base1.z),
          Vector3(base2.x, base2.y, base2.z),
        ]),
        Polygon([
          Vector3(base0.x, base0.y, base0.z),
          Vector3(base1.x, base1.y, base1.z),
          Vector3(tip.x, tip.y, tip.z),
        ]),
        Polygon([
          Vector3(base1.x, base1.y, base1.z),
          Vector3(base2.x, base2.y, base2.z),
          Vector3(tip.x, tip.y, tip.z),
        ]),
        Polygon([
          Vector3(base2.x, base2.y, base2.z),
          Vector3(base0.x, base0.y, base0.z),
          Vector3(tip.x, tip.y, tip.z),
        ]),
      ];
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
          Vertex4(1.0, 1.0, 1.0, -1.0 / sqrt(5.0)),
          Vertex4(1.0, -1.0, -1.0, -1.0 / sqrt(5.0)),
          Vertex4(-1.0, 1.0, -1.0, -1.0 / sqrt(5.0)),
          Vertex4(-1.0, -1.0, 1.0, -1.0 / sqrt(5.0)),
          Vertex4(0.0, 0.0, 0.0, sqrt(5.0) - 1.0 / sqrt(5.0)),
        );
}

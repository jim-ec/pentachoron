import 'dart:ui';

import 'package:meta/meta.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3, Matrix4;

class Geometry {
  final Color color;
  final List<Polygon> polygons;
  final Matrix4 transform;

  /// Create a geometry from a set of points to be transformed through
  /// [rotation], [translation] and [scale], and a default color.
  Geometry({
    @required this.polygons,
    @required this.color,
    Rotation rotation,
    Vector3 translation,
    Vector3 scale,
  }) : transform = Matrix4.translation(translation ?? Vector3.zero()) *
            (rotation ?? Rotation.zero()).transform;
}

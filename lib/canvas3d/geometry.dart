import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:meta/meta.dart';
import 'package:tesserapp/geometry/polygon.dart';

class Geometry {
  final Color color;
  final Iterable<Polygon> polygons;

  /// Create a geometry from a set of points to be transformed through
  /// [rotation], [translation] and [scale], and a default color.
  Geometry({
    @required this.polygons,
    @required this.color,
  });
}

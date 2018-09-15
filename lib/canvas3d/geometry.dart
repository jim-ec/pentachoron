import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:meta/meta.dart';
import 'package:tesserapp/canvas3d/canvas3d.dart';
import 'package:tesserapp/canvas3d/polygon.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:vector_math/vector_math_64.dart' show Vector3, Matrix4;

class Geometry {
  final Color color;
  final Iterable<Polygon> polygons;
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

Iterable<Geometry> get axisIndicator => <Geometry>[
      Geometry(
        color: Colors.red,
        translation: Vector3(0.15, 0.0, 0.0),
        rotation: Rotation.fromEuler(
            Angle.zero(), Angle.fromDegrees(90.0), Angle.zero()),
        polygons: pyramid(edgeLength: 0.3, height: 2.0),
      ),
      Geometry(
        color: Colors.lightGreen,
        translation: Vector3(0.0, 0.15, 0.0),
        rotation: Rotation.fromEuler(
            Angle.fromDegrees(-90.0), Angle.zero(), Angle.zero()),
        polygons: pyramid(edgeLength: 0.3, height: 2.0),
      ),
      Geometry(
        color: Colors.blue,
        translation: Vector3(0.0, 0.0, 0.15),
        polygons: pyramid(edgeLength: 0.3, height: 2.0),
      ),
    ];

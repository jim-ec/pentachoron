import 'dart:math';
import 'dart:typed_data';

import 'package:meta/meta.dart';

@immutable
class Vector {
  final Float32x4 components;
  
  double get x => components.x;
  double get y => components.y;
  double get z => components.z;
  double get w => components.w;

  Vector(
    final double x,
      final double y, [
        final double z = 0.0,
        final double w = 0.0,
  ]) : components = Float32x4(x, y, z, w);

  const Vector.zero() : this.of(0.0);

  const Vector.of(final double c) : this(c, c, c, c);

  const Vector.ofX(final double c) : this(c, 0.0, 0.0, 0.0);

  const Vector.ofY(final double c) : this(0.0, c, 0.0, 0.0);

  const Vector.ofZ(final double c) : this(0.0, 0.0, c, 0.0);

  const Vector.ofW(final double c) : this(0.0, 0.0, 0.0, c);

  factory Vector.barycenter(final Iterable<Vector> points) =>
      points.reduce((a, b) => a + b) / points.length.toDouble();

  double operator [](final int index) {
    assert(index >= 0 && index < 4, "Invalid vector index $index");
    switch (index) {
      case 0:
        return x;
      case 1:
        return y;
      case 2:
        return z;
      case 3:
        return w;
      default:
        return null;
    }
  }

  factory Vector.generate(double f(int index)) {
    final components = Iterable.generate(4, f).iterator;
    return Vector(
      (components..moveNext()).current,
      (components..moveNext()).current,
      (components..moveNext()).current,
      (components..moveNext()).current,
    );
  }

  factory Vector.cross(final Vector a, final Vector b) {
    assert((a.w - b.w).abs() < 0.01, "Only 3d vectors can be crossed");
    return Vector(a.y * b.z - a.z * b.y,
        a.z * b.x - a.x * b.z,
        a.x * b.y - a.y * b.x,
        0.0);
  }

  static double dot(final Vector a, final Vector b) =>
      a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;

  Vector operator +(final Vector other) =>
      Vector(x + other.x, y + other.y, z + other.z, w + other.w);

  Vector operator *(final double c) => Vector(x * c, y * c, z * c, w * c);

  Vector operator /(final double c) => Vector(x / c, y / c, z / c, w / c);

  Vector operator -(final Vector other) => this + -other;

  Vector operator -() => Vector(-x, -y, -z, -w);

  /// Create a string representing this vector.
  /// Z and w component are only included if they differ from 0 by more than
  /// one tenth.
  @override
  String toString() => "[${x.toStringAsFixed(1)}"
      ", ${y.toStringAsFixed(1)}"
      "${z.abs() >= 0.1 ? ", ${z.toStringAsFixed(1)}" : ""}"
      "${w.abs() >= 0.1 ? ", ${w.toStringAsFixed(1)}" : ""}"
      "]";

  double get length => sqrt(Vector.dot(this, this));

  Vector get normalized => Vector(x / length, y / length, z / length);
}

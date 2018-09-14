import 'dart:math';

import 'package:meta/meta.dart';

@immutable
class Vector {
  final double x, y, z, w;
  
  const Vector(
      this.x,
      this.y,
      this.z, [
        this.w = 0.0,
      ]);
  
  const Vector.zero() : this.of(0.0);
  
  const Vector.of(final double c) : this(c, c, c, c);
  
  const Vector.ofX(final double c) : this(c, 0.0, 0.0, 0.0);
  
  const Vector.ofY(final double c) : this(0.0, c, 0.0, 0.0);
  
  const Vector.ofZ(final double c) : this(0.0, 0.0, c, 0.0);
  
  const Vector.ofW(final double c) : this(0.0, 0.0, 0.0, c);
  
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
  
  Vector.cross(final Vector a, final Vector b)
      : w = 0.0,
        x = a.y * b.z - a.z - b.y,
        y = a.z * b.x - a.x * b.z,
        z = a.x * b.y - a.y * b.x {
    assert(a.w == b.w, "Only 3d vectors can be crossed");
  }
  
  static double scalar(final Vector a, final Vector b) =>
      a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
  
  Vector operator +(final Vector other) =>
      Vector(x + other.x, y + other.y, z + other.z, w + other.w);
  
  Vector operator *(final double c) => Vector(x * c, y * c, z * c, w * c);
  
  Vector operator -(final Vector other) => this + -other;
  
  Vector operator -() => Vector(-x, -y, -z, -w);
  
  @override
  String toString() => "[${x.toStringAsFixed(1)}, "
      "${y.toStringAsFixed(1)}, "
      "${z.toStringAsFixed(1)}, "
      "${w.toStringAsFixed(1)}]";
  
  double get length => sqrt(Vector.scalar(this, this));
  
  Vector get normalized => Vector(x / length, y / length, z / length);
}

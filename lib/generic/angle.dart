import 'dart:math' as math;

import 'package:meta/meta.dart';

@immutable
class Angle implements Comparable<Angle> {
  final double _storage;

  const Angle.zero() : _storage = 0.0;

  const Angle.fromDegrees(final double degrees)
      : _storage = degrees / 180.0 * math.pi;

  const Angle.fromRadians(final double radians) : _storage = radians;

  const Angle.fromPi(final double pi) : _storage = pi * math.pi;

  const Angle.fromGradians(final double gradians)
      : _storage = gradians / 200.0 * math.pi;

  double get degrees => (_storage / math.pi) * 180.0;

  double get radians => _storage;

  double get overPi => _storage / math.pi;

  double get gradians => (_storage / math.pi) * 200.0;

  Angle operator +(final Angle other) =>
      Angle.fromRadians(radians + other.radians);

  Angle operator -(final Angle other) =>
      Angle.fromRadians(radians - other.radians);

  Angle operator -() => Angle.fromRadians(-radians);

  Angle operator *(final double scale) => Angle.fromRadians(radians * scale);

  Angle operator /(final double scale) => Angle.fromRadians(radians / scale);

  @override
  String toString() => "$degrees°";

  String toStringRadians() => "${radians}rad";

  bool operator <(final Angle other) => _storage < other._storage;

  bool operator >(final Angle other) => !(this < other) && this != other;

  @override
  int compareTo(Angle other) => this == other ? 0 : this > other ? 1 : -1;
}

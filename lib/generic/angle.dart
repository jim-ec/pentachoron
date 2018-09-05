import 'dart:math' as math;

import 'package:meta/meta.dart';

@immutable
class Angle {
  final double _storage;

  const Angle.fromDegrees(final double degrees)
      : _storage = degrees / 180.0 * math.pi;

  const Angle.fromRadians(final double radians) : _storage = radians;

  const Angle.fromPi(final double pi) : _storage = pi * math.pi;

  const Angle.zero() : _storage = 0.0;

  get degrees => (_storage / math.pi) * 180.0;

  get radians => _storage;

  Angle operator +(final Angle other) =>
      Angle.fromRadians(radians + other.radians);

  @override
  String toString() => "$degrees°";

  String toStringRadians() => "${radians}rad";
}

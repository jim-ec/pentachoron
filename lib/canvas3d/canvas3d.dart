import 'package:flutter/material.dart';
import 'package:tesserapp/canvas3d/geometry.dart';
import 'package:tesserapp/canvas3d/painter.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:vector_math/vector_math_64.dart';

class Canvas3d extends StatelessWidget {
  /// Camera position, in global space.
  final CameraPosition cameraPosition;

  /// List of geometry to be drawn.
  final List<Geometry> geometries;

  /// Color of the outline, if drawn.
  final Color outlineColor;

  /// Vertical field of view in radians.
  /// The value is only used when rendering perspective projection.
  final Angle fov;

  /// Direction of global light.
  /// A vector longer than 1.0 increases light intensity.
  final Vector3 lightDirection;

  Canvas3d({
    Key key,
    @required this.cameraPosition,
    @required this.geometries,
    @required this.outlineColor,
    @required this.fov,
    @required this.lightDirection,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: Canvas3dPainter(this),
        ),
      );
}

/// Camera position.
class CameraPosition {
  final Vector3 eye, focus, up;

  CameraPosition({
    this.eye,
    this.focus,
    up,
  }) : up = up ?? Vector3(0.0, 0.0, 1.0);

  CameraPosition.fromOrbitEuler({
    final double distance,
    final Angle polar,
    final Angle azimuth,
  }) : this(
            focus: Vector3.zero(),
            eye: Rotation.fromEuler(polar, azimuth, Angle.zero())
                .transform
                .transform3(Vector3(distance, 0.0, 0.0)));
}

class Rotation {
  final Matrix4 transform;

  Rotation.fromEuler(final Angle yaw, final Angle pitch, final Angle roll)
      : transform = Matrix4.rotationZ(yaw.radians) *
            Matrix4.rotationY(pitch.radians) *
            Matrix4.rotationX(roll.radians);

  static Rotation zero() =>
      Rotation.fromEuler(Angle.zero(), Angle.zero(), Angle.zero());
}

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

  /// How to draw the outline.
  final OutlineMode outlineMode;

  /// Color of the outline, if drawn.
  final Color outlineColor;

  /// Anti aliasing for polygons and the outline path.
  final bool antiAliasing;

  /// If enabled, geometry is drawn using an orthographic projection
  /// rather then using a perspective projection.
  final bool orthographicProjection;

  /// Vertical field of view in radians.
  /// The value is only used when rendering perspective projection.
  final Angle fov;

  /// The frustum side length.
  /// The value is only used when rendering orthographic projection.
  final double frustumSize;

  /// Maximum distance at which polygons remains visible.
  /// Defaults to [double.infinity], i.e. there is no limit in view distance.
  /// This might be inefficient if there is a large area of geometry to render.
  final double viewDistance;

  /// Direction of global light:
  final Vector3 lightDirection;

  /// Space of light direction:
  final LightSpace lightSpace;

  Canvas3d({
    Key key,
    @required this.cameraPosition,
    @required this.geometries,
    this.outlineMode = OutlineMode.off,
    this.outlineColor,
    this.orthographicProjection = false,
    this.fov = const Angle.fromDegrees(60.0),
    this.frustumSize = 10.0,
    final Vector3 lightDirection,
    this.antiAliasing = true,
    this.viewDistance = double.infinity,
    this.lightSpace = LightSpace.global,
  })  : lightDirection =
            lightDirection?.normalized() ?? Vector3(1.0, 0.8, 0.2).normalized(),
        super(key: key) {
    if (outlineMode != OutlineMode.off) {
      assert(
          outlineColor != null,
          "If outline mode is not off, "
          "a non-null outline color must be specified");
    }
  }

  @override
  Widget build(BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: Canvas3dPainter(this),
        ),
      );
}

/// Marking a geometry as outlined adds it to the overhaul set of
/// outlined geometry. All outlined geometry share a common color and
/// a common outline path, that's why one cannot set the outline color
/// of a single geometry.
enum OutlineMode {
  /// No outlining at all.
  off,

  /// Outline is draw on top off all other geometry.
  overlay,

  /// Outline is occluded by obscuring geometry.
  /// The actual path is still closed, drawn around occluding geometry.
  /// This is quite performance expensive when drawing a lot of geometry.
  occluded,
}

/// How to cull faces. Culling refers to how to keep or drop polygons which
/// are facing either to the camera or away from it.
///
/// The facing is determined by the polygons normal.
/// A normal facing the same direction as the camera is considered back
/// facing, while facing towards the camera indicates front facing.
///
/// This seriously influences performance, as it can drastically reduce
/// the amount of polygons which need to be drawn.
///
/// Cases when disabling culling might be necessary:
///
///  - Drawing semi-transparent geometry, as this makes back-facing polygons
///     visible to the camera.
///  - Drawing flat geometry, like sprites.
///
enum CullMode {
  /// No facing check is performed.
  /// Disabling culling should definitely be avoided when drawing solid
  /// geometry.
  off,

  /// Drops back facing geometry.
  backFacing,

  /// Drops front facing geometry.
  /// This is rarely necessary and might only be useful when dealing with
  /// flipped normals.
  frontFacing,
}

/// The space in which [DrawParameters.lightDirection] is to be interpreted.
enum LightSpace {
  /// The light direction is given in global coordinates.
  global,

  /// The light direction is given in view space coordinates.
  /// This effectively binds the light to the current camera orientation.
  view,
}

/// Camera position.
class CameraPosition {
  final Vector3 eye, focus, up;

  CameraPosition({
    this.eye,
    this.focus,
    up,
  }) : up = up ?? Vector3(0.0, 1.0, 0.0);

  CameraPosition.fromOrbitEuler({
    final double distance,
    final Angle polar,
    final Angle azimuth,
  }) : this(
            focus: Vector3.zero(),
            eye: Matrix4.rotationY(polar.radians)
                .multiplied(Matrix4.rotationZ(azimuth.radians))
                .transform3(Vector3(distance, 0.0, 0.0)));
}

class Rotation {
  final Matrix4 transform;

  Rotation.fromEuler(final Angle yaw, final Angle pitch, final Angle roll)
      : transform = Matrix4.rotationY(yaw.radians) *
            Matrix4.rotationZ(pitch.radians) *
            Matrix4.rotationX(roll.radians);

  static Rotation zero() =>
      Rotation.fromEuler(Angle.zero(), Angle.zero(), Angle.zero());
}

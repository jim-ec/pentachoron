part of canvas3d;

/// All modifiable options to affect rendering of the canvas.
class DrawParameters {
  /// Camera position, in global space.
  final CameraPosition cameraPosition;
  
  /// List of geometry to be drawn.
  final List<Geometry> geometries;
  
  /// How to draw the outline.
  final OutlineMode outlineMode;
  
  /// Color of the outline, if drawn.
  final Color outlineColor;
  
  /// If enabled, back facing polygons are not drawn at all.
  /// This improves performance, as fewer vertices have to processed
  /// and fewer polygons needs to be drawn.
  /// On the other side, enabling culling can increase artifacts at the
  /// polygon edges due to anti-aliasing.
  final bool enableCulling;
  
  /// If enabled, geometry is drawn using an orthographic projection
  /// rather then using a perspective projection.
  final bool orthographicProjection;
  
  /// Vertical field of view in radians.
  /// The value is only used when rendering perspective projection.
  final fov;
  
  /// The frustum side length.
  /// The value is only used when rendering orthographic projection.
  final frustumSize;
  
  /// Direction of global light:
  final lightDirection;
  
  DrawParameters({
    @required this.cameraPosition,
    @required this.geometries,
    this.outlineMode = OutlineMode.off,
    this.outlineColor = const Color(0x0),
    this.enableCulling = true,
    this.orthographicProjection = false,
    this.fov = const Angle.fromDegrees(60.0),
    this.frustumSize = 10.0,
    final Vector3 lightDirection,
  }) : lightDirection =
      lightDirection?.normalized() ?? Vector3(1.0, 0.8, 0.2).normalized() {
    if (outlineMode != OutlineMode.off) {
      assert(
      outlineColor != null,
      "If outline mode is not off, "
          "a non-null outline color must be specified");
    }
  }
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
      eye: Matrix4
          .rotationY(polar.radians)
          .multiplied(Matrix4.rotationZ(azimuth.radians))
          .transform3(Vector3(distance, 0.0, 0.0)));
}

class Rotation {
  final Matrix4 transform;
  
  Rotation.fromEuler(final Angle yaw, final Angle pitch, final Angle roll)
      : transform = Matrix4.rotationY(yaw.radians) *
      Matrix4.rotationZ(pitch.radians) *
      Matrix4.rotationX(roll.radians);
}

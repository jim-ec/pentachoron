part of canvas3d;

/// A polygon wrapper adding pipeline processing functionality to it.
/// It bundles per-geometry features like outlining into the polygons,
/// as they are decoupled from their geometries in order to perform
/// depth sorting.
class _ProcessingPolygon extends Polygon
    implements Comparable<_ProcessingPolygon> {
  /// Whether or not to add this polygon to the outline path.
  final bool outlined;
  
  /// Not used right now.
  final bool culled;
  
  _ProcessingPolygon(
      final List<Vector3> positions,
      final Color color,
      this.outlined,
      this.culled,
      ) : super(positions, color);
  
  /// This polygon's gravitational center.
  Vector3 get barycenter =>
      positions.reduce((a, b) => a + b) / positions.length.toDouble();
  
  /// Normal vector, standing perpendicular on top of the plane this polygon
  /// is forming.
  Vector3 get normal => (positions[2] - positions[0])
      .cross(positions[1] - positions[0])
      .normalized();
  
  /// Return a transformed version of this polygon.
  /// To transform the polygon using perspective matrices,
  /// use [perspectiveTransformed] instead.
  _ProcessingPolygon transformed(final Matrix4 matrix) => _ProcessingPolygon(
      positions.map((v) => matrix.transformed3(v)).toList(),
      color,
      outlined,
      culled);
  
  /// Return a transformed version of this polygon,
  /// taking perspective division into account.
  _ProcessingPolygon perspectiveTransformed(final Matrix4 matrix) =>
      _ProcessingPolygon(
          positions.map((v) => matrix.perspectiveTransform(v)).toList(),
          color,
          outlined,
          culled);
  
  /// Return a re-colored version of this polygon.
  /// [lightDirection] defines the direction of parallel light rays,
  /// used to illuminate the polygon.
  ///
  /// [lightDirection] is assumed to be in the *same coordinate space*
  /// as this polygon.
  _ProcessingPolygon illuminated(final Vector3 lightDirection) {
    final luminance = normal.dot(lightDirection);
    final softenLuminance = remap(luminance, -1.0, 1.0, -0.2, 1.2);
    return _ProcessingPolygon(
        positions,
        Color.lerp(Color(0xff000000), color, softenLuminance),
        outlined,
        culled);
  }
  
  /// Performs a depth comparison.
  ///
  /// The polygon which's barycenter has a higher z coordinate
  /// is occluding the other one.
  @override
  int compareTo(final _ProcessingPolygon other) =>
      barycenter.z > other.barycenter.z ? 1 : -1;
}

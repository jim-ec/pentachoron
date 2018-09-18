import 'dart:math';

import 'package:meta/meta.dart';
import 'package:quiver/iterables.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry/vector.dart';

enum RotationPlane {
  aroundX,
  aroundY,
  aroundZ,
  onXY,
  onYZ,
  onXZ,
  onXQ,
}

/// A row-major, 5D transformation matrix.
/// Suitable to transform 4D points.
@immutable
class Matrix {
  final Iterable<Iterable<double>> rows;

  Matrix.generate(double f(int row, int col))
      : rows = Iterable.generate(
            5, (final row) => Iterable.generate(5, (final col) => f(row, col)));

  Matrix.fromRows(this.rows) {
    assert(rows.length == 5, "A 5D matrix must have 5 rows");
    assert(rows.every((col) => col.length == 5),
        "A 5D matrix must be have 5 columns");
  }

  Matrix.identity()
      : this.fromRows([
          [1.0, 0.0, 0.0, 0.0, 0.0],
          [0.0, 1.0, 0.0, 0.0, 0.0],
          [0.0, 0.0, 1.0, 0.0, 0.0],
          [0.0, 0.0, 0.0, 1.0, 0.0],
          [0.0, 0.0, 0.0, 0.0, 1.0],
        ]);

  Matrix.translation(final Vector v)
      : this.fromRows([
          [1.0, 0.0, 0.0, 0.0, 0.0],
          [0.0, 1.0, 0.0, 0.0, 0.0],
          [0.0, 0.0, 1.0, 0.0, 0.0],
          [0.0, 0.0, 0.0, 1.0, 0.0],
          [v.x, v.y, v.z, v.w, 1.0],
        ]);

  double at(final int row, final int col) => rows.elementAt(row).elementAt(col);

  Matrix operator *(final Matrix rhs) =>
      Matrix.generate((final row, final col) => range(5)
          .map((final i) => this.at(row, i) * rhs.at(i, col))
          .reduce((a, b) => a + b));

  /// Create a transform matrix by chaining all transform matrices.
  /// This is the same behaviour as successively multiplying all the matrices
  /// in the same order they are given here.
  /// Since the matrices are row-major, all the transformations are applied
  /// intuitively in the same order as they appear in the list.
  factory Matrix.chain(final Iterable<Matrix> matrices) => matrices.isEmpty
      ? Matrix.identity()
      : matrices.reduce((acc, matrix) => acc * matrix);

  factory Matrix.rotation(final RotationPlane plane, final Angle angle) {
    int a, b;
    switch (plane) {
      case RotationPlane.aroundX:
      case RotationPlane.onYZ:
        a = 1;
        b = 2;
        break;
      case RotationPlane.aroundY:
      case RotationPlane.onXZ:
        a = 2;
        b = 0;
        break;
      case RotationPlane.aroundZ:
      case RotationPlane.onXY:
        a = 0;
        b = 1;
        break;
      case RotationPlane.onXQ:
        a = 0;
        b = 3;
        break;
    }
    return Matrix.generate((final row, final col) {
      if (row == a && col == a) return cos(angle.radians);
      if (row == a && col == b) return sin(angle.radians);
      if (row == b && col == a) return -sin(angle.radians);
      if (row == b && col == b) return cos(angle.radians);
      if (row == col) {
        return 1.0;
      } else {
        return 0.0;
      }
    });
  }

  Vector transform(final Vector v) => Vector.generate((final int col) =>
      range(4).map((i) => v[i] * this.at(i, col)).reduce((a, b) => a + b) +
      at(4, col));

  Iterable<Vector> transformAll(final Iterable<Vector> vectors) =>
      vectors.map((v) => transform(v));

  @override
  String toString() {
    final rowToString = (final num index) =>
        rows.elementAt(index).map((final c) => c.toStringAsFixed(1)).join(", ");
    return "[ " + range(5).map(rowToString).join(" | ") + " ]";
  }
}

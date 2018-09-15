import 'dart:math';

import 'package:meta/meta.dart';
import 'package:quiver/iterables.dart';
import 'package:tesserapp/generic/angle.dart';
import 'package:tesserapp/geometry4d/vector.dart';

enum RotationPlane {
  aroundX,
  aroundY,
  aroundZ,
  onXQ,
}

/// A row-major, 5D transformation matrix.
/// Suitable to transform 4D points.
@immutable
class Matrix {
  final List<List<double>> rows;

  Matrix.generate(double f(int row, int col))
      : rows = List.generate(
            5, (final row) => List.generate(5, (final col) => f(row, col)));

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

  List<double> operator [](final int row) => rows[row];

  Matrix operator *(final Matrix rhs) =>
      Matrix.generate((final row, final col) => range(5)
          .map((final i) => this[row][i] * rhs[i][col])
          .reduce((a, b) => a + b));

  /// Create a transform matrix by chaining all transform matrices.
  /// This is the same behaviour as successively multiplying all the matrices
  /// in the same order they are given here.
  /// Since the matrices are row-major, all the transformations are applied
  /// intuitively in the same order as they appear in the list.
  factory Matrix.chain(final List<Matrix> matrices) => matrices.isEmpty
      ? Matrix.identity()
      : matrices.reduce((acc, matrix) => acc * matrix);

  factory Matrix.rotation(final RotationPlane plane, final Angle angle) {
    final matrix = Matrix.identity();
    int a, b;
    switch (plane) {
      case RotationPlane.aroundX:
        a = 1;
        b = 2;
        break;
      case RotationPlane.aroundY:
        a = 2;
        b = 0;
        break;
      case RotationPlane.aroundZ:
        a = 0;
        b = 1;
        break;
      case RotationPlane.onXQ:
        a = 0;
        b = 3;
        break;
    }
    matrix[a][a] = cos(angle.radians);
    matrix[a][b] = sin(angle.radians);
    matrix[b][a] = -sin(angle.radians);
    matrix[b][b] = cos(angle.radians);
    return matrix;
  }

  Vector transform(final Vector v) => Vector.generate((final int col) =>
      range(4).map((i) => v[i] * this[i][col]).reduce((a, b) => a + b) +
      this[4][col]);

  List<Vector> transformAll(final List<Vector> vectors) =>
      vectors.map((v) => transform(v)).toList();

  @override
  String toString() {
    final rowToString = (final num index) =>
        rows[index].map((final c) => c.toStringAsFixed(1)).join(", ");
    return "[ " + range(5).map(rowToString).join(" | ") + " ]";
  }
}

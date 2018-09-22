import 'dart:math';
import 'dart:typed_data';

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
  onYQ,
  onZQ,
}

/// A row-major, 5D transformation matrix.
/// Suitable to transform 4D points.
@immutable
class Matrix {
  final Float64List buffer;

  Matrix.fromList(final List<double> list)
      : buffer = UnmodifiableFloat64ListView(Float64List.fromList(list));

  factory Matrix.generate(double f(int row, int col)) =>
      Matrix.fromRows(Iterable.generate(5,
              (final row) => Iterable.generate(5, (final col) => f(row, col)))
          .toList());

  factory Matrix.fromRows(final Iterable<Iterable<double>> rows) {
    assert(rows.length == 5, "A 5D matrix must have 5 rows");
    assert(rows.every((col) => col.length == 5),
        "A 5D matrix must be have 5 columns");

    return Matrix.fromList(rows.expand((i) => i).toList(growable: false));
  }

  factory Matrix.identity() => Matrix.fromRows([
        [1.0, 0.0, 0.0, 0.0, 0.0],
        [0.0, 1.0, 0.0, 0.0, 0.0],
        [0.0, 0.0, 1.0, 0.0, 0.0],
        [0.0, 0.0, 0.0, 1.0, 0.0],
        [0.0, 0.0, 0.0, 0.0, 1.0],
      ]);

  factory Matrix.translation(final Vector v) => Matrix.fromRows([
        [1.0, 0.0, 0.0, 0.0, 0.0],
        [0.0, 1.0, 0.0, 0.0, 0.0],
        [0.0, 0.0, 1.0, 0.0, 0.0],
        [0.0, 0.0, 0.0, 1.0, 0.0],
        [v.x, v.y, v.z, v.w, 1.0],
      ]);

  Matrix get transpose =>
      Matrix.generate((final row, final col) => at(col, row));

  double at(final int row, final int col) => buffer.elementAt(row * 5 + col);

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
      case RotationPlane.onYQ:
        a = 1;
        b = 3;
        break;
      case RotationPlane.onZQ:
        a = 2;
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
      range(4).map((i) => v[i] * at(i, col)).reduce((a, b) => a + b) +
      at(4, col));

  Vector transformFast(final Vector v) => Vector(
        v[0] * at(0, 0) +
            v[1] * at(1, 0) +
            v[2] * at(2, 0) +
            v[3] * at(3, 0) +
            at(4, 0),
        v[0] * at(0, 1) +
            v[1] * at(1, 1) +
            v[2] * at(2, 1) +
            v[3] * at(3, 1) +
            at(4, 1),
        v[0] * at(0, 2) +
            v[1] * at(1, 2) +
            v[2] * at(2, 2) +
            v[3] * at(3, 2) +
            at(4, 2),
        v[0] * at(0, 3) +
            v[1] * at(1, 3) +
            v[2] * at(2, 3) +
            v[3] * at(3, 3) +
            at(4, 3),
      );

  Iterable<Vector> transformAll(final Iterable<Vector> vectors) =>
      vectors.map((v) => transform(v));

  String toStringLong() {
    final leadingChar = (final int row) {
      switch (row) {
        case 0:
          return "\u23A1 ";
        case 4:
          return "\u23A3 ";
        default:
          return "\u23A2 ";
      }
    };
    final trailingChar = (final int row) {
      switch (row) {
        case 0:
          return " \u23A4";
        case 4:
          return " \u23A6";
        default:
          return " \u23A5";
      }
    };
    return range(5)
        .map((final row) =>
            leadingChar(row) +
            range(5)
                .map((final col) => at(row, col))
                .map((final c) => c >= 0.0
                    ? " " + c.toStringAsFixed(1)
                    : c.toStringAsFixed(1))
                .join(" \u23A2 ") +
            trailingChar(row))
        .join("\n");
  }
}

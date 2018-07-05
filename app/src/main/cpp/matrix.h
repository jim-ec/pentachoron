//
// Created by jimec on 7/1/2018.
//

#ifndef TESSERAPP_ROTATION_H
#define TESSERAPP_ROTATION_H

#include <functional>
#include <tuple>
#include <vector>
#include <algorithm>
#include <numeric>

#include "range.h"
#include "glm/matrix.hpp"

using namespace whoshuu;

template<glm::length_t N>
struct Matrix : glm::mat<N, N, double> {};

template<glm::length_t N>
struct Vector : glm::vec<N, double> {};

template<glm::length_t N>
Matrix<N> matrix(std::function<double(int, int)> const &initializer) {
    Matrix<N> result;
    for (auto c : range(N)) {
        for (auto r : range(N)) {
            result[c][r] = initializer(r, c);
        }
    }
    return result;
};

struct MatrixLocation {
    int row;
    int col;
};

template<glm::length_t N>
Matrix<N> identity(
        std::vector<std::pair<MatrixLocation, double>> const &values
) {
    return matrix<N>([values = std::move(values)](int const row, int const col) {
        for (auto cellValue : values) {
            if (cellValue.first.row == row && cellValue.first.col == col) {
                return cellValue.second;
            }
        }
        if (row == col)
            return 1.0;
        else return 0.0;
    });
}

/*

enum class RotationPlane(inline val a: Int, inline val b: Int) {
    AROUND_X(1, 2),
    AROUND_Y(2, 0),
    AROUND_Z(0, 1),
    XQ(0, 3)
}

fun rotation(size: Int, plane: RotationPlane, radians: Radians) =
        identity(size, values = mapOf(
                plane.a to plane.a to cos(radians),
                plane.a to plane.b to sin(radians),
                plane.b to plane.a to -sin(radians),
                plane.b to plane.b to cos(radians)
        ))
 */

using Radians = double;

enum class RotationPlane {
    AROUND_X,
    AROUND_Y,
    AROUND_Z,
    XQ
};

inline std::pair<int, int> rotationAxis(
        RotationPlane const plane
) {
    switch (plane) {
        case RotationPlane::AROUND_X:
            return {1, 2};
        case RotationPlane::AROUND_Y:
            return {2, 0};
        case RotationPlane::AROUND_Z:
            return {0, 1};
        case RotationPlane::XQ:
            return {0, 3};
    }
}

template<glm::length_t N>
Matrix<N> rotation(
        RotationPlane const plane,
        Radians const radians
) {
    auto axis = rotationAxis(plane);
    return identity<N>({{
                                std::make_pair(MatrixLocation{axis.first, axis.first},
                                        std::cos(radians)),
                                std::make_pair(MatrixLocation{axis.first, axis.second},
                                        std::sin(radians)),
                                std::make_pair(MatrixLocation{axis.second, axis.first},
                                        -std::sin(radians)),
                                std::make_pair(MatrixLocation{axis.second, axis.second},
                                        std::cos(radians))
                        }});
}

/*
fun translation(size: Int, v: VectorN) =
        if (v.dimension != size - 1)
            throw RuntimeException("Invalid vector dimension")
        else
            quadratic(size) { row, col ->
                when (row) {
                    col -> 1.0
                    size - 1 -> v[col]
                    else -> 0.0
                }
            }
 */

template<glm::length_t N>
Matrix<N> translation(
        Vector<N> const v
) {
    return matrix<N>([v](int const row, int const col) {
        if (row == col) {
            return 1.0;
        } else if (row == N - 1) {
            return v[col];
        } else {
            return 0.0;
        }
    });
}

template<glm::length_t N>
double operator[](
        Matrix<N> const &matrix,
        MatrixLocation const location
) {
    return matrix[location.row * N + location.col];
}

/*
operator fun Matrix.times(rhs: Matrix) =
        if (cols != rhs.rows)
            throw RuntimeException("Cannot multiply matrices")
        else
            Matrix(rows, rhs.cols) { row, col ->
                (0 until cols).sumByDouble { this[row, it] * rhs[it, col] }
            }
 */

template<glm::length_t N>
Matrix<N> operator*(
        Matrix<N> const lhs,
        Matrix<N> const rhs
) {
    return matrix<N>([lhs, rhs](int const row, int const col) {
        double sum = 0.0;
        for (auto const i : range(N)) {
            sum += lhs[MatrixLocation{row, i}] * rhs[MatrixLocation{i, col}];
        }
        return sum;
    });
}

template<glm::length_t N, class... TRest>
Matrix<N> transformChain(
        Matrix<N> const matrix,
        TRest... rest
) {
    return matrix * transformChain(rest...);
}

template<glm::length_t N>
Matrix<N> transformChain(
        Matrix<N> const matrix
) {
    return matrix;
}

#endif //TESSERAPP_ROTATION_H

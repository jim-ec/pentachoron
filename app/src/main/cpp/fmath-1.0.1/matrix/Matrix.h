#ifndef FMATH_MATRIX_MATRIX_H
#define FMATH_MATRIX_MATRIX_H

#include <functional>
#include <tuple>
#include <vector>
#include <algorithm>
#include <numeric>

#include <utility/Range.h>
#include <common/Dimension.h>
#include <common/Primitives.h>

namespace fmath {
    
    using Row = Dimension;
    using Col = Dimension;

/**
 * Specifies a coefficient location within a matrix.
 */
    struct MatrixLocation {
        Row row;
        Col col;
    };

/**
 * An immutable, row-major n-dimensional matrix.
 * @tparam T Element type.
 * @tparam N Side-length.
 */
    template<class T, Dimension N>
    struct Matrix {
        
        /**
         * Side-length of matrix.
         * @return
         */
        constexpr static auto size() {
            return N;
        }
        
        /**
         * 2-dimensional array containing all the coefficients.
         */
        std::array<std::array<T, N>, N> coefficients;
        
        /**
         * Calls f for each coefficient location.
         */
        auto forEachCoefficient(
                std::function<void(
                        Row,
                        Col
                )> f
        ) const -> void {
            for (auto const row : range(size())) {
                for (auto const col : range(size())) {
                    f(row, col);
                }
            }
        }
        
        /**
         * Create a new matrix, initializing all the coefficients through a callback.
         * @param initializer Called for each cell location, returning the value associated to that cell.
         */
        explicit Matrix(
                std::function<T(
                        Row,
                        Col
                )> initializer
        ) {
            static_assert(N > 0, "Invalid matrix dimension");
            forEachCoefficient([&](
                    Row const row,
                    Col const col
            ) {
                coefficients[row][col] = initializer(row, col);
            });
        }
        
        /**
         * Access a single coefficient.
         * @param location Location of cell to access.
         */
        auto operator[](
                MatrixLocation const location
        ) const -> T {
            return coefficients[location.row][location.col];
        }
        
    };

/**
 * Serializes a matrix.
 * @param os Output stream to serialize to.
 * @param m Matrix to be serialized.
 * @return Output stream.
 */
    template<class T, Dimension N>
    std::ostream &operator<<(
            std::ostream &os,
            Matrix<T, N> const m
    ) {
        os << "[ ";
        for (auto const row : range(N)) {
            for (auto const col : range(N)) {
                os << m[{row, col}];
                if (col < N - 1)
                    os << ", ";
            }
            if (row < N - 1)
                os << " | ";
        }
        os << " ]";
        return os;
    }

/**
 * Create an identity matrix, while additionally initializing cells given in form of a list.
 * @param values Explicitly initialized cells, expressed through a pair of the targeting location and the actual value.
 * @return The created matrix.
 */
    template<class T, Dimension N>
    auto identity(
            std::vector<std::pair<MatrixLocation, T>> const &values
    ) -> Matrix<T, N> {
        return Matrix<T, N>([values = std::move(values)](
                Row const row,
                Col const col
        ) {
            for (auto const cellValue : values) {
                if (cellValue.first.row == row && cellValue.first.col == col) {
                    return cellValue.second;
                }
            }
            if (row == col)
                return one<T>();
            else
                return zero<T>();
        });
    }
    
}

#endif //FMATH_MATRIX_MATRIX_H

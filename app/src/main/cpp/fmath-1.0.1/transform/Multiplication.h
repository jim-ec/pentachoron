#ifndef FMATH_TRANSFORM_MULTIPLICATION_H
#define FMATH_TRANSFORM_MULTIPLICATION_H

#include <matrix/Matrix.h>
#include <common/Primitives.h>
#include <vector/Vector.h>
#include <vector/Arithmetic.h>
#include <numeric>

namespace fmath {

/**
 * Multiply two matrices.
 */
    template<class T, Dimension N>
    auto operator*(
            Matrix<T, N> const lhs,
            Matrix<T, N> const rhs
    ) -> Matrix<T, N> {
        return Matrix<T, N>([lhs, rhs](
                Row const row,
                Col const col
        ) {
            return std::accumulate(range(N).begin(), range(N).end(), zero<T>(), [=](
                    T const acc,
                    decltype(N) const i
            ) {
                return acc + lhs[{row, i}] * rhs[{i, col}];
            });
        });
    }

/**
 * Multiply a vector with a matrix.
 */
    template<class T, Dimension N>
    auto operator*(
            Vector<T, N - 1> const lhs,
            Matrix<T, N> const rhs
    ) {
        T const w = std::accumulate(range(N - 1).begin(), range(N - 1).end(), zero<T>(), [=](
                T const acc,
                decltype(N) const i
        ) {
            return acc + lhs[i] * rhs[{i, rhs.size() - 1}];
        }) + rhs[{N - 1, rhs.size() - 1}];
        
        return Vector<T, N - 1>([lhs, rhs](Dimension const col) {
            return std::accumulate(range(N - 1).begin(), range(N - 1).end(), zero<T>(), [=](
                    T const acc,
                    decltype(N) const i
            ) {
                return acc + lhs[i] * rhs[{i, col}];
            }) + rhs[{N - 1, col}];
        }) / w;
    };

/**
 * A convenient method for multiplying a bunch of matrices.
 * Same order of execution applies.
 */
    template<class T, Dimension N, class... TRest>
    auto transformChain(
            Matrix<T, N> const matrix,
            TRest... rest
    ) -> Matrix<T, N> {
        return matrix * transformChain(rest...);
    }

/**
 * A convenient method for multiplying a bunch of matrices.
 * Same order of execution applies.
 */
    template<class T, Dimension N>
    auto transformChain(
            Matrix<T, N> const matrix
    ) -> Matrix<T, N> {
        return matrix;
    }
    
}

#endif //FMATH_TRANSFORM_MULTIPLICATION_H

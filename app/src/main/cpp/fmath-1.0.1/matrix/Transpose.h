#include <common/Dimension.h>
#include "Matrix.h"

#ifndef FMATH_MATRIX_TRANSPOSE_H
#define FMATH_MATRIX_TRANSPOSE_H

namespace fmath {

/**
 * Create a transpose of a matrix.
 */
    template<class T, Dimension N>
    auto transpose(
            Matrix <T, N> const m
    ) -> Matrix <T, N> {
        return Matrix<T, N>{
                [m](
                        Row const row,
                        Col const col
                ) {
                    return m[{col, row}];
                }};
    };
    
}

#endif //FMATH_MATRIX_TRANSPOSE_H

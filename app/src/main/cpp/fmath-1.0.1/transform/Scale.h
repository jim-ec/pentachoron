#ifndef FMATH_TRANSFORM_SCALE_H
#define FMATH_TRANSFORM_SCALE_H

#include <common/Dimension.h>
#include <vector/Vector.h>
#include <matrix/Matrix.h>

namespace fmath {
    
    template<class T, Dimension N>
    auto scale(
            Vector <T, N> const v
    ) -> Matrix<T, N + 1> {
        return Matrix<T, N + 1>([v](
                Row const row,
                Col const col
        ) {
            if (row == col) {
                if (row < N) {
                    return v[row];
                } else {
                    return one<T>();
                }
            } else {
                return zero<T>();
            }
        });
    }
    
}

#endif //FMATH_TRANSFORM_SCALE_H

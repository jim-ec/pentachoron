#ifndef FMATH_TRANSFORM_TRANSLATION_H
#define FMATH_TRANSFORM_TRANSLATION_H

#include <matrix/Matrix.h>
#include <vector/Vector.h>
#include <common/Primitives.h>

namespace fmath {
    
    template<class T, Dimension N>
    auto translation(
            Vector <T, N> const v
    ) -> Matrix<T, N + 1> {
        return Matrix<T, N + 1>([v](
                Row const row,
                Col const col
        ) {
            if (row == col) {
                return one<T>();
            } else if (row == N) {
                return v[col];
            } else {
                return zero<T>();
            }
        });
    }
    
}

#endif //FMATH_TRANSFORM_TRANSLATION_H

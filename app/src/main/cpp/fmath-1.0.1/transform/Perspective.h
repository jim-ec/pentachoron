#ifndef FMATH_TRANSFORM_PERSPECTIVE_H
#define FMATH_TRANSFORM_PERSPECTIVE_H

#include <matrix/Matrix.h>

namespace fmath {

/**
 * Create a perspective matrix.
 * @param near Near plane.
 * @param far Far plane.
 */
    template<class T>
    auto perspective(
            T const near,
            T const far
    ) -> Matrix<T, 4> {
        return identity<T, 4>({{MatrixLocation{2, 3}, -1.0},
                               {MatrixLocation{3, 3}, 0.0},
                               {MatrixLocation{2, 2}, -far / (far - near)},
                               {MatrixLocation{3, 2}, -(far * near) / (far - near)}});
    }
    
}

#endif //FMATH_TRANSFORM_PERSPECTIVE_H

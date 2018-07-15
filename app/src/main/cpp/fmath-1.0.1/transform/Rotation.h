#ifndef FMATH_TRANSFORM_ROTATION_H
#define FMATH_TRANSFORM_ROTATION_H

#include <utility>
#include <cmath>
#include <matrix/Matrix.h>

namespace fmath {
    
    using Radians = double;
    
    Radians operator "" _pi(unsigned long long int const radiansOverPi) {
        return static_cast<Radians>(radiansOverPi * M_PI);
    }
    
    Radians operator "" _pi(long double const radiansOverPi) {
        return static_cast<Radians>(radiansOverPi * M_PI);
    }
    
    inline Radians radians(double const degrees) {
        return static_cast<Radians>((degrees / 180.0) * M_PI);
    }
    
    Radians operator "" _deg(unsigned long long int const degrees) {
        return radians(degrees);
    }
    
    Radians operator "" _deg(long double const degrees) {
        return radians(static_cast<double>(degrees));
    }
    
    enum class RotationPlane {
        AROUND_X, AROUND_Y, AROUND_Z, XQ
    };
    
    inline auto rotationAxis(
            RotationPlane const plane
    ) -> std::pair<Dimension, Dimension> {
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
    
    template<class T, Dimension N>
    auto rotation(
            RotationPlane const plane,
            Radians const radians
    ) -> Matrix <T, N> {
        auto const axis = rotationAxis(plane);
        return identity<T, N>({{MatrixLocation{axis.first, axis.first},   std::cos(radians)},
                               {MatrixLocation{axis.first, axis.second},  std::sin(radians)},
                               {MatrixLocation{axis.second, axis.first},  -std::sin(radians)},
                               {MatrixLocation{axis.second, axis.second}, std::cos(radians)}});
    }
    
}

#endif //FMATH_TRANSFORM_ROTATION_H

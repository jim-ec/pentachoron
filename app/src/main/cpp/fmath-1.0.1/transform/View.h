#ifndef FMATH_TRANSFORM_VIEW_H
#define FMATH_TRANSFORM_VIEW_H

#include <matrix/Matrix.h>
#include <vector/Arithmetic.h>
#include <matrix/Transpose.h>
#include <transform/Translation.h>
#include <transform/Scale.h>
#include <transform/Multiplication.h>
#include <transform/Rotation.h>

namespace fmath {
    
    template<class T>
    auto lookAt(
            T const distance,
            Vector3d<T> const refUp
    ) -> fmath::Matrix<T, 4> {
        auto const forward = vector3d(one<T>(), zero<T>(), zero<T>());
        auto const right = cross(normalized(refUp), forward);
        auto const up = cross(forward, right);
        
        auto const viewDirection = transpose(identity<T, 4>({{{0, 0}, right.x()},
                                                             {{0, 1}, right.y()},
                                                             {{0, 2}, right.z()},
                                                             {{1, 0}, up.x()},
                                                             {{1, 1}, up.y()},
                                                             {{1, 2}, up.z()},
                                                             {{2, 0}, forward.x()},
                                                             {{2, 1}, forward.y()},
                                                             {{2, 2}, forward.z()}}));
        
        return translation<T, 3>(vector3d(-distance, zero<T>(), zero<T>())) * viewDirection;
    }
    
    template<class T>
    auto aspectRatioCorrection(
            T const aspectRatio
    ) -> fmath::Matrix<T, 4> {
        return scale<T, 3>(
                aspectRatio > 1 ? vector3d(one<T>() / aspectRatio, one<T>(), one<T>()) : vector3d(
                        one<T>(),
                        aspectRatio,
                        one<T>()));
    }
    
    template<class T>
    inline auto fovXScale(
            Radians const fovX
    ) -> fmath::Matrix<T, 4> {
        T const necessaryViewportWidth = std::tan(static_cast<T>(fovX) / static_cast<T>(2.0));
        return scale(vector3d(one<T>() / necessaryViewportWidth, one<T>() / necessaryViewportWidth,
                one<T>()));
    }
    
    template<class T>
    auto view(
            T const horizontalRotation,
            T const verticalRotation,
            T const distance,
            T const aspectRatio,
            Radians const fovX = 90_deg
    ) -> fmath::Matrix<T, 4> {
        return transformChain<T, 4>(rotation<T, 4>(RotationPlane::AROUND_Y, horizontalRotation),
                rotation<T, 4>(RotationPlane::AROUND_Z, verticalRotation),
                lookAt(distance, vector3d(zero<T>(), one<T>(), zero<T>())),
                aspectRatioCorrection(aspectRatio),
                fovXScale<T>(fovX));
    }
    
}

#endif //FMATH_TRANSFORM_VIEW_H

//
// Created by jimec on 7/5/2018.
//

#ifndef FMATH_PRIMITIVES_H
#define FMATH_PRIMITIVES_H

namespace fmath {

/**
 * Return the value representing zero.
 * @tparam T Type which representation of zero is queried.
 * @return Zero representing value.
 */
    template<class T>
    auto zero() -> T;

/**
 * Zero representation for double precision floating-points.
 */
    template<>
    inline constexpr auto zero<double>() -> double {
        return 0.0;
    }

/**
 * Zero representation for single precision floating-points.
 */
    template<>
    inline constexpr auto zero<float>() -> float {
        return 0.0f;
    }

/**
 * Zero representation for integers.
 */
    template<>
    inline constexpr auto zero<int>() -> int {
        return 0;
    }

/**
 * Return the value representing one.
 * @tparam T Type which representation of one is queried.
 * @return One representing value.
 */
    template<class T>
    auto one() -> T;

/**
 * One representation for double precision floating-points.
 */
    template<>
    inline auto one<double>() -> double {
        return 1.0;
    }

/**
 * One representation for single precision floating-points.
 */
    template<>
    inline auto one<float>() -> float {
        return 1.0f;
    }

/**
 * One representation for integers.
 */
    template<>
    inline auto one<int>() -> int {
        return 1;
    }
    
}

#endif //FMATH_PRIMITIVES_H

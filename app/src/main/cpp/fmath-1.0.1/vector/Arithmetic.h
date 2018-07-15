#include <common/Dimension.h>
#include <common/Primitives.h>
#include <vector/Vector.h>
#include <numeric>
#include <cmath>

#ifndef FMATH_VECTOR_ARITHMETIC_H
#define FMATH_VECTOR_ARITHMETIC_H

namespace fmath {
    
    template<class T, Dimension N, class Combiner>
    auto combineVectorsComponentwise(
            Vector <T, N> const a,
            Vector <T, N> const b,
            Combiner &&combiner
    ) -> Vector <T, N> {
        return Vector<T, N> {
                [a, b, combiner](Dimension const i) {
                    return combiner(a[i], b[i]);
                }};
    }
    
    template<class T, Dimension N>
    auto operator+(
            Vector <T, N> const a,
            Vector <T, N> const b
    ) -> Vector <T, N> {
        return combineVectorsComponentwise(a, b, [](
                typename Vector<T, N>::ComponentType const x,
                typename Vector<T, N>::ComponentType const y
        ) {
            return x + y;
        });
    }
    
    template<class T, Dimension N>
    auto operator-(
            Vector <T, N> const a,
            Vector <T, N> const b
    ) -> Vector <T, N> {
        return combineVectorsComponentwise(a, b, [](
                typename Vector<T, N>::ComponentType const x,
                typename Vector<T, N>::ComponentType const y
        ) {
            return x - y;
        });
    }
    
    template<class T, Dimension N>
    auto operator/(
            Vector <T, N> const a,
            typename Vector<T, N>::ComponentType const h
    ) -> Vector <T, N> {
        return Vector<T, N> {
                [a, h](Dimension const i) {
                    return a[i] / h;
                }};
    }
    
    template<class T, Dimension N>
    auto operator*(
            Vector <T, N> const a,
            typename Vector<T, N>::ComponentType const h
    ) -> Vector <T, N> {
        return Vector<T, N> {
                [a, h](Dimension const i) {
                    return a[i] * h;
                }};
    }
    
    template<class T, Dimension N>
    auto operator*(
            Vector <T, N> const a,
            Vector <T, N> const b
    ) -> typename Vector<T, N>::ComponentType {
        return std::inner_product(a.components.begin(),
                a.components.end(),
                b.components.begin(), zero<typename Vector<T, N>::ComponentType>());
    }
    
    template<class T, Dimension N>
    auto length(
            Vector <T, N> const v
    ) -> typename Vector<T, N>::ComponentType {
        return std::sqrt(v * v);
    }
    
    template<class T, Dimension N>
    auto normalized(
            Vector <T, N> const v
    ) -> Vector <T, N> {
        return Vector<T, N> {
                [v, oneOverLength = one<typename Vector<T, N>::ComponentType>() / length(v)](
                        Dimension const i) {
                    return v[i] * oneOverLength;
                }};
    }
    
    template<class T>
    auto cross(
            Vector3d <T> const lhs,
            Vector3d <T> const rhs
    ) -> Vector3d <T> {
        return Vector3d<T> {
                lhs.y() * rhs.z() - lhs.z() * rhs.y(),
                lhs.z() * rhs.x() - lhs.x() * rhs.z(),
                lhs.x() * rhs.y() - lhs.y() * rhs.x()
        };
    }
    
}

#endif //FMATH_VECTOR_ARITHMETIC_H

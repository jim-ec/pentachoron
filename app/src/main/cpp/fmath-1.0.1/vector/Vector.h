#ifndef FMATH_VECTOR_VECTOR_H
#define FMATH_VECTOR_VECTOR_H

#include <array>
#include <utility/Range.h>
#include <common/Dimension.h>
#include <functional>
#include <ostream>

namespace fmath {
    
    template<class T, Dimension N>
    struct Vector {
        
        using ComponentType = T;
        
        constexpr static auto size() {
            return N;
        }
        
        std::array<ComponentType, N> components;
        
        explicit Vector(
                std::function<double(Dimension)> initializer
        ) {
            static_assert(N > 0, "Invalid vector dimension");
            for (auto i : range(size())) {
                components[i] = initializer(i);
            }
        }
        
        Vector(
                std::initializer_list<ComponentType> il
        ) {
            if (il.size() != size()) {
                throw std::runtime_error{"Invalid vector component initialization"};
            }
            
            std::copy(il.begin(), il.end(), components.begin());
        }
        
        template<class Target>
        explicit operator Vector<Target, N>() const {
            return Vector<Target, N>([=](Dimension const i) {
                return static_cast<Target>(components[i]);
            });
        };
        
        constexpr auto operator[](
                Dimension const index
        ) const -> T {
            return components[index < N && index >= 0 ? index : throw std::logic_error{
                    "Vector component index out of bounds"
            }];
        }
        
        constexpr auto x() const {
            return components[0];
        }
        
        constexpr auto y() const {
            return components[1];
        }
        
        constexpr auto z() const {
            return components[2];
        }
        
        constexpr auto q() const {
            return components[3];
        }
        
    };
    
    template<class T, Dimension N>
    std::ostream &operator<<(
            std::ostream &os,
            Vector<T, N> const v
    ) {
        os << "( ";
        for (auto const col : range(N)) {
            os << v[col];
            if (col < N - 1)
                os << ", ";
        }
        os << " )";
        return os;
    }
    
    template<class T> using Vector3d = Vector<T, 3>;
    
    template<class T>
    constexpr auto vector3d(
            T const x,
            T const y,
            T const z
    ) -> Vector3d<T> {
        return Vector3d<T>{{x, y, z}};
    }
    
    template<class T> using Vector4d = Vector<T, 4>;
    
    template<class T>
    constexpr auto vector4d(
            T const x,
            T const y,
            T const z,
            T const q
    ) -> Vector4d<T> {
        return Vector4d<T>{{x, y, z, q}};
    }
    
}

#endif //FMATH_VECTOR_VECTOR_H

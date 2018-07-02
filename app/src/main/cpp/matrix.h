//
// Created by jimec on 7/1/2018.
//

#ifndef TESSERAPP_ROTATION_H
#define TESSERAPP_ROTATION_H

#include <functional>
#include <array>
#include <tuple>
#include <initializer_list>
#include <algorithm>

#include "range.h"
#include "glm/matrix.hpp"

using namespace whoshuu;

template<glm::length_t N> using Matrix = glm::mat<N, N, double>

template<glm::length_t N>
Matrix<N> matrix(std::function<double(int, int)> const &initializer) {
    Matrix<N> result;
    for (auto c : range(N)) {
        for (auto r : range(N)) {
            result[c][r] = initializer(r, c);
        }
    }
    return result;
};

template<glm::length_t N>
Matrix<N> identity(std::initializer_list<std::pair<std::pair<int, int>, double>> const &values) {
    return matrix<N>([&values](int row, int col) {
        for (auto cellValue : values) {
            if (cellValue.first.first == row && cellValue.first.second == col) {
                return cellValue.second;
            }
        }
        if (row == col)
            return 1.0;
        else return 0.0;
    });
}

template<class Key, class Value, int Size>
using StaticMapping = std::array<std::pair<Key, Value>, Size>;

template<class Key, class Value, class... Args>
auto staticMapping(Args... args) {
    return StaticMapping{{args...}};
};

/*
 * enum class RotationPlane(inline val a: Int, inline val b: Int) {
    AROUND_X(1, 2),
    AROUND_Y(2, 0),
    AROUND_Z(0, 1),
    XQ(0, 3)
}
 */

enum RotationPlane {
    AROUND_X,
    AROUND_Y,
    AROUND_Z,
    XQ
};

inline auto RotationPlaneMapping = staticMapping(
        std::make_pair(AROUND_X, std::make_pair(1, 2)),
        std::make_pair(AROUND_Y, std::make_pair(2, 0)),
        std::make_pair(AROUND_Z, std::make_pair(0, 1)),
        std::make_pair(XQ, std::make_pair(0, 3))
);

template<class Key, class Value>
Value findMapping(Key const &key) {

};

#endif //TESSERAPP_ROTATION_H

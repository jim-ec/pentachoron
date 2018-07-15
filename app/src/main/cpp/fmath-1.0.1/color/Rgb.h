//
// Created by jimec on 7/8/2018.
//

#ifndef FMATH_COLOR_RGB_H
#define FMATH_COLOR_RGB_H

namespace fmath {

/**
 * Store RGB color components, normalizing them between 0.0 and 1.0.
 */
    template<class T>
    struct Rgb {
        T red;
        T green;
        T blue;
    };

/**
 * Decodes a serialized color integer into a RGB tuple.
 * Ignores alpha values.
 *
 * @param code
 * Integer containing the red, green and blue components.
 * The layout is expected to be: 0xRRGGBB, i.e. the last three bytes are evaluated.
 * Each encoded color component is expected to be normalized between 0 and 255, i.e. the complete value
 * range of a single byte.
 *
 * @return Extracted RGB, converted into a floating-point layout.
 */
    template<class T>
    auto decodeRgb(
            int const code
    ) -> Rgb<T> {
        return {
                static_cast<T>((code >> 16) & 0xff) / static_cast<T>(255),
                static_cast<T>((code >> 8) & 0xff) / static_cast<T>(255),
                static_cast<T>((code >> 0) & 0xff) / static_cast<T>(255)
        };
    }
    
}

#endif //FMATH_COLOR_RGB_H

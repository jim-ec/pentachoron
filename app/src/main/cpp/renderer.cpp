#include <jni.h>

#include <GLES2/gl2.h>

#include <vector/Vector.h>
#include <transform/Multiplication.h>
#include <transform/Rotation.h>
#include <transform/Translation.h>
#include <color/Rgb.h>

#ifdef __cplusplus
extern "C" {
#endif

namespace {
    jclass transformClass;
    jfieldID transformFieldIdRotationX,
            transformFieldIdRotationY,
            transformFieldIdRotationZ,
            transformFieldIdRotationQ,
            transformFieldIdTranslationX,
            transformFieldIdTranslationY,
            transformFieldIdTranslationZ,
            transformFieldIdTranslationQ;
    
    static const std::size_t VECTORS_PER_VERTEX = 2;
    
    /**
     * Vertex, containing a position and a color.
     * Memory is laid out so that the instances can be uploaded directly to GL.
     */
    struct Vertex {
        Vector3d<float> const position;
        float const w;
        Rgb<float> const color;
        float const alpha;
        
        Vertex(
                Vector3d<float> const position,
                Rgb<float> const color
        ) : position{position}, w{1.0f}, color{color}, alpha{1.0f} {}
    };
}

JNIEXPORT auto JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_init(
        JNIEnv *env,
        jobject
) -> void {
    transformClass = reinterpret_cast<jclass>(env->NewGlobalRef(
            env->FindClass("io/jim/tesserapp/cpp/Transform")));
    transformFieldIdRotationX = env->GetFieldID(transformClass, "rotationX", "D");
    transformFieldIdRotationY = env->GetFieldID(transformClass, "rotationY", "D");
    transformFieldIdRotationZ = env->GetFieldID(transformClass, "rotationZ", "D");
    transformFieldIdRotationQ = env->GetFieldID(transformClass, "rotationQ", "D");
    transformFieldIdTranslationX = env->GetFieldID(transformClass, "translationX", "D");
    transformFieldIdTranslationY = env->GetFieldID(transformClass, "translationY", "D");
    transformFieldIdTranslationZ = env->GetFieldID(transformClass, "translationZ", "D");
    transformFieldIdTranslationQ = env->GetFieldID(transformClass, "translationQ", "D");
}

JNIEXPORT auto JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_deinit(
        JNIEnv *env,
        jobject
) -> void {
    env->DeleteGlobalRef(transformClass);
}

static auto modelMatrix(
        double const rotationX,
        double const rotationY,
        double const rotationZ,
        double const rotationQ,
        double const translationX,
        double const translationY,
        double const translationZ,
        double const translationQ
) -> Matrix<double, 5> {
    return transformChain<double, 5>(
            rotation<double, 5>(RotationPlane::AROUND_X, rotationX),
            rotation<double, 5>(RotationPlane::AROUND_Y, rotationY),
            rotation<double, 5>(RotationPlane::AROUND_Z, rotationZ),
            rotation<double, 5>(RotationPlane::XQ, rotationQ),
            translation(vector4d(
                    translationX,
                    translationY,
                    translationZ,
                    translationQ
            ))
    );
} ;

JNIEXPORT auto JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_drawGeometry(
        JNIEnv *env,
        jobject,
        jobject positionsBuffer,
        jobject transform,
        jint color,
        jboolean isFourDimensional
) -> void {
    
    auto const matrix = modelMatrix(
            env->GetDoubleField(transform, transformFieldIdRotationX),
            env->GetDoubleField(transform, transformFieldIdRotationY),
            env->GetDoubleField(transform, transformFieldIdRotationZ),
            env->GetDoubleField(transform, transformFieldIdRotationQ),
            env->GetDoubleField(transform, transformFieldIdTranslationX),
            env->GetDoubleField(transform, transformFieldIdTranslationY),
            env->GetDoubleField(transform, transformFieldIdTranslationZ),
            env->GetDoubleField(transform, transformFieldIdTranslationQ)
    );
    
    auto const visualized = [isFourDimensional, matrix](
            Vector4d<double> const v) -> Vector3d<double> {
        auto const transformed = v * matrix;
        if (isFourDimensional) {
            return Vector3d<double>{[transformed](Dimension const i) {
                return transformed[i] / transformed.q();
            }};
        } else {
            return vector3d(transformed.x(), transformed.y(), transformed.z());
        }
    };
    
    auto const pointCounts =
            static_cast<std::size_t>(env->GetDirectBufferCapacity(positionsBuffer)) / 4;
    auto const positionsData =
            reinterpret_cast<double *>(env->GetDirectBufferAddress(positionsBuffer));
    
    std::vector<Vertex> vertices;
    vertices.reserve(pointCounts);
    
    for (auto const i : range(pointCounts)) {
        vertices.push_back(Vertex{
                static_cast<Vector3d<float>>(visualized(vector4d(
                        positionsData[i * 4 + 0],
                        positionsData[i * 4 + 1],
                        positionsData[i * 4 + 2],
                        positionsData[i * 4 + 3]
                ))),
                decodeRgb<float>(color)
        });
    }
    
    // Actual buffer has already been bound on Kotlin side.
    glBufferData(GL_ARRAY_BUFFER,
            vertices.size() * sizeof(Vertex),
            vertices.data(),
            GL_DYNAMIC_DRAW);
    
    glDrawArrays(GL_LINES, 0, static_cast<GLsizei>(vertices.size() * VECTORS_PER_VERTEX));
}

#ifdef __cplusplus
}
#endif

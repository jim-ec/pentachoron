#include <jni.h>

#include <vector/Vector.h>
#include <transform/Multiplication.h>
#include <transform/Rotation.h>
#include <transform/Translation.h>

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
    jclass geometryClass;
    jfieldID geometryFieldIdName,
            geometryFieldIdIsFourDimensional,
            geometryFieldIdPositions;
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
    
    geometryClass = reinterpret_cast<jclass>(env->NewGlobalRef(
            env->FindClass("io/jim/tesserapp/geometry/Geometry")));
    geometryFieldIdName = env->GetFieldID(geometryClass, "name", "Ljava/lang/String;");
    geometryFieldIdIsFourDimensional = env->GetFieldID(geometryClass, "isFourDimensional", "Z");
    geometryFieldIdPositions = env->GetFieldID(geometryClass, "positions",
            "Ljava/nio/DoubleBuffer;");
}

JNIEXPORT auto JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_deinit(
        JNIEnv *env,
        jobject
) -> void {
    env->DeleteGlobalRef(transformClass);
    env->DeleteGlobalRef(geometryClass);
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
        jobject geometry,
        jobject transform
) -> void {
    
    auto const name = std::string{env->GetStringUTFChars(reinterpret_cast<jstring>(
            env->GetObjectField(geometry, geometryFieldIdName)), nullptr)};
    
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
    
    auto const visualized = [
            isFourDimensional = static_cast<bool>(
                    env->GetBooleanField(geometry, geometryFieldIdIsFourDimensional)),
            matrix
    ](Vector4d<double> const v) -> Vector4d<double> {
        auto const transformed = v * matrix;
        if (isFourDimensional) {
            return transformed / transformed.q();
        } else {
            return transformed;
        }
    };
    
    auto const positionsBuffer = env->GetObjectField(geometry, geometryFieldIdPositions);
    auto const pointCounts =
            static_cast<std::size_t>(env->GetDirectBufferCapacity(positionsBuffer)) / 4;
    auto const positionsData =
            reinterpret_cast<double *>(env->GetDirectBufferAddress(positionsBuffer));
    
    std::vector<Vector4d<double>> points;
    points.reserve(pointCounts);
    
    for (auto const i : range(pointCounts)) {
        points.push_back(visualized(vector4d(
                positionsData[i * 4 + 0],
                positionsData[i * 4 + 1],
                positionsData[i * 4 + 2],
                positionsData[i * 4 + 3]
        )));
    }
}

#ifdef __cplusplus
}
#endif

#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnusedImportStatement"

#include <android/log.h>

#define LOG(...) \
    __android_log_print(ANDROID_LOG_VERBOSE, __FILE__, __VA_ARGS__)

#pragma clang diagnostic pop

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
            transformFieldIdRotationQ;
    jfieldID transformFieldIdTranslationX,
            transformFieldIdTranslationY,
            transformFieldIdTranslationZ,
            transformFieldIdTranslationQ;
}

JNIEXPORT auto JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_init(
        JNIEnv *env,
        jobject obj
) -> void {
    transformClass = env->FindClass("io/jim/tesserapp/cpp/Transform");
    transformFieldIdRotationX = env->GetFieldID(transformClass, "rotationX", "D");
    transformFieldIdRotationY = env->GetFieldID(transformClass, "rotationY", "D");
    transformFieldIdRotationZ = env->GetFieldID(transformClass, "rotationZ", "D");
    transformFieldIdRotationQ = env->GetFieldID(transformClass, "rotationQ", "D");
    transformFieldIdTranslationX = env->GetFieldID(transformClass, "translationX", "D");
    transformFieldIdTranslationY = env->GetFieldID(transformClass, "translationY", "D");
    transformFieldIdTranslationZ = env->GetFieldID(transformClass, "translationZ", "D");
    transformFieldIdTranslationQ = env->GetFieldID(transformClass, "translationQ", "D");
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
        jobject obj,
        jobject geometry,
        jobject transform
) -> jobject {
    
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
    
    auto const rawMatrixClass = env->FindClass("io/jim/tesserapp/cpp/RawMatrix");
    auto const rawMatrixConstructor = env->GetMethodID(rawMatrixClass, "<init>", "()V");
    auto rawMatrixObj = env->NewObject(rawMatrixClass, rawMatrixConstructor);
    auto const field = env->GetObjectField(rawMatrixObj,
            env->GetFieldID(rawMatrixClass, "coefficients", "[D"));
    auto const rawMatrixCoefficients = reinterpret_cast<jdoubleArray>(field);
    
    auto const data = env->GetDoubleArrayElements(rawMatrixCoefficients, JNI_FALSE);
    
    matrix.forEachCoefficient([&](Row const row, Col const col) {
        data[row * 5 + col] = matrix[{row, col}];
    });
    
    env->ReleaseDoubleArrayElements(rawMatrixCoefficients, data, 0);
    
    return rawMatrixObj;
}

#ifdef __cplusplus
}
#endif

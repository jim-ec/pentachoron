#include <jni.h>

#include <vector/Vector.h>
#include <transform/Multiplication.h>
#include <common/Dimension.h>
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

JNIEXPORT void JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_init(
        JNIEnv *env,
        jobject obj
) {
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

JNIEXPORT void JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_drawGeometry(
        JNIEnv *env,
        jobject obj,
        jobject geometry,
        jobject transform
) {
    
    auto const modelMatrix = transformChain<double, Dimension{5}>(
            rotation<double, 5>(RotationPlane::AROUND_X,
                    env->GetDoubleField(transform, transformFieldIdRotationX)),
            rotation<double, 5>(RotationPlane::AROUND_Y,
                    env->GetDoubleField(transform, transformFieldIdRotationY)),
            rotation<double, 5>(RotationPlane::AROUND_Z,
                    env->GetDoubleField(transform, transformFieldIdRotationZ)),
            rotation<double, 5>(RotationPlane::XQ,
                    env->GetDoubleField(transform, transformFieldIdRotationQ)),
            translation(vector4d(
                    env->GetDoubleField(transform, transformFieldIdTranslationX),
                    env->GetDoubleField(transform, transformFieldIdTranslationY),
                    env->GetDoubleField(transform, transformFieldIdTranslationZ),
                    env->GetDoubleField(transform, transformFieldIdTranslationQ)
            ))
    );
    
}

#ifdef __cplusplus
}
#endif

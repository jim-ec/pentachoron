#include <jni.h>

#include <vector>
#include <tuple>
#include <string>

static void foo() {
    std::vector<std::pair<std::string, int>> v{{{"Jim", 20}, {"Alena", 12}}};
    
    auto [a, b] = v[0];
}

extern "C" JNIEXPORT void JNICALL
Java_io_jim_tesserapp_ui_view_Renderer_drawGeometry(
        JNIEnv *env,
        jobject,
        jobject geometry
) {
}

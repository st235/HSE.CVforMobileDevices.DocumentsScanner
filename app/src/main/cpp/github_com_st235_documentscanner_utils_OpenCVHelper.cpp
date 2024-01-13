#include "github_com_st235_documentscanner_utils_OpenCVHelper.h"

JNIEXPORT jstring JNICALL Java_github_com_st235_documentscanner_utils_OpenCVHelper_helloWorld(JNIEnv* env, jclass clazz) {
    return env->NewStringUTF("Hello OpenCV");
}

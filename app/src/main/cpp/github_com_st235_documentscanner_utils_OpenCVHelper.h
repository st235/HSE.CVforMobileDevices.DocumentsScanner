#ifndef DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_OPENCVHELPER_H
#define DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_OPENCVHELPER_H

#import <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_OpenCVHelper_wrapPerspective(
        JNIEnv* env, jclass clazz,
        jlong image, jfloatArray corners,
        jlong out);

JNIEXPORT jfloatArray JNICALL Java_github_com_st235_documentscanner_utils_OpenCVHelper_findCorners(
        JNIEnv* env, jclass clazz, jlong image);

#ifdef __cplusplus
}
#endif

#endif //DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_OPENCVHELPER_H

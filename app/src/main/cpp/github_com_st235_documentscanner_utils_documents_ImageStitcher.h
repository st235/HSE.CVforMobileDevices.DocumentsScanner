#ifndef DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_DOCUMENTS_IMAGESTITCHER_H
#define DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_DOCUMENTS_IMAGESTITCHER_H

#include <jni.h>

extern "C" {

JNIEXPORT jlong JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageStitcher_init(
        JNIEnv* env, jclass clazz);

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageStitcher_deinit(
        JNIEnv* env, jclass clazz,
        jlong stitcherPointer);

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageStitcher_stitch(
        JNIEnv* env, jclass clazz,
        jlong stitcherPointer,
        jlongArray images,
        jlong out);
    
};


#endif //DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_DOCUMENTS_IMAGESTITCHER_H

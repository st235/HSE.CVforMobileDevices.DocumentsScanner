#ifndef DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_DOCUMENTS_DOCUMENTSCANNER_H
#define DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_DOCUMENTS_DOCUMENTSCANNER_H

#import <jni.h>

extern "C" {

JNIEXPORT jlong JNICALL Java_github_com_st235_documentscanner_utils_documents_DocumentScanner_init(
        JNIEnv* env, jclass clazz);

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_DocumentScanner_deinit(
        JNIEnv* env, jclass clazz,
        jlong scannerPointer);

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_DocumentScanner_wrapPerspective(
        JNIEnv* env, jclass clazz,
        jlong scannerPointer,
        jlong image, jfloatArray corners,
        jlong out);

JNIEXPORT jfloatArray JNICALL Java_github_com_st235_documentscanner_utils_documents_DocumentScanner_findCorners(
        JNIEnv* env, jclass clazz,
        jlong scannerPointer,
        jlong image);

}

#endif //DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_DOCUMENTS_DOCUMENTSCANNER_H

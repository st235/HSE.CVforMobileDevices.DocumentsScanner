#ifndef DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_DOCUMENTS_IMAGEPROCESSOR_H
#define DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_DOCUMENTS_IMAGEPROCESSOR_H

#import <jni.h>

extern "C" {

JNIEXPORT jlong JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_init(
        JNIEnv* env, jclass clazz);
    
JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_deinit(
        JNIEnv* env, jclass clazz,
        jlong processorPointer);

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_rotate90(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image,
        jlong out);

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_binarization(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image, jint mode,
        jlong out);

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_filter(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image, jint mode,
        jlong out);

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_denoise(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image, jint mode,
        jlong out);

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_enhanceContrast(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image, jint mode,
        jlong out);

}


#endif //DOCUMENTSCANNER_GITHUB_COM_ST235_DOCUMENTSCANNER_UTILS_DOCUMENTS_IMAGEPROCESSOR_H

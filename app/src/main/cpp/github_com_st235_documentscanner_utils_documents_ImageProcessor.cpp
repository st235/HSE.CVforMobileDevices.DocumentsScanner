#include "github_com_st235_documentscanner_utils_documents_ImageProcessor.h"

#include <cstdint>
#include <opencv2/core.hpp>

#include "processor/ImageProcessor.h"

JNIEXPORT jlong JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_init(
        JNIEnv* env, jclass clazz) {
    return reinterpret_cast<jlong>(new ImageProcessor());
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_deinit(
        JNIEnv* env, jclass clazz,
        jlong processorPointer) {
    auto* ptr = reinterpret_cast<ImageProcessor*>(processorPointer);
    delete ptr;
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_rotate90(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image,
        jlong out) {
    auto* ptr = reinterpret_cast<ImageProcessor*>(processorPointer);

    cv::Mat& matrixIn = *reinterpret_cast<cv::Mat*>(image);
    cv::Mat& matrixOut = *reinterpret_cast<cv::Mat*>(out);

    ptr->rotate90(matrixIn, matrixOut);
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_binarization(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image, jint mode,
        jlong out) {
    auto* ptr = reinterpret_cast<ImageProcessor*>(processorPointer);

    cv::Mat& matrixIn = *reinterpret_cast<cv::Mat*>(image);
    cv::Mat& matrixOut = *reinterpret_cast<cv::Mat*>(out);

    ptr->binarization(matrixIn, static_cast<ImageProcessor::BINARIZATION>(mode), matrixOut);
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_filter(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image, jint mode,
        jlong out) {
    auto* ptr = reinterpret_cast<ImageProcessor*>(processorPointer);

    cv::Mat& matrixIn = *reinterpret_cast<cv::Mat*>(image);
    cv::Mat& matrixOut = *reinterpret_cast<cv::Mat*>(out);

    ptr->filter(matrixIn, static_cast<ImageProcessor::FILTER>(mode), matrixOut);
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_denoise(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image, jint mode,
        jlong out) {
    auto* ptr = reinterpret_cast<ImageProcessor*>(processorPointer);

    cv::Mat& matrixIn = *reinterpret_cast<cv::Mat*>(image);
    cv::Mat& matrixOut = *reinterpret_cast<cv::Mat*>(out);

    ptr->denoise(matrixIn, static_cast<ImageProcessor::DENOISING>(mode), matrixOut);
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageProcessor_enhanceContrast(
        JNIEnv* env, jclass clazz,
        jlong processorPointer,
        jlong image, jint mode,
        jlong out) {
    auto* ptr = reinterpret_cast<ImageProcessor*>(processorPointer);

    cv::Mat& matrixIn = *reinterpret_cast<cv::Mat*>(image);
    cv::Mat& matrixOut = *reinterpret_cast<cv::Mat*>(out);

    ptr->enhanceContrast(matrixIn, static_cast<ImageProcessor::CONTRAST>(mode), matrixOut);
}

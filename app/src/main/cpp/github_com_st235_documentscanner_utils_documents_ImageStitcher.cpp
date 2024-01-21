#include "github_com_st235_documentscanner_utils_documents_ImageStitcher.h"

#include <cstdint>
#include <opencv2/core.hpp>
#include <vector>

#include "processor/ImageStitcher.h"

JNIEXPORT jlong JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageStitcher_init(
        JNIEnv* env, jclass clazz) {
    return reinterpret_cast<jlong>(new ImageStitcher());
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageStitcher_deinit(
        JNIEnv* env, jclass clazz,
        jlong stitcherPointer) {
    auto* ptr = reinterpret_cast<ImageStitcher*>(stitcherPointer);
    delete ptr;
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_ImageStitcher_stitch(
        JNIEnv* env, jclass clazz,
        jlong stitcherPointer,
        jlongArray images,
        jlong out) {
    auto* ptr = reinterpret_cast<ImageStitcher*>(stitcherPointer);

    std::vector<cv::Mat> matricesIn;
    cv::Mat& matrixOut = *reinterpret_cast<cv::Mat*>(out);

    auto imagesLength = static_cast<uint32_t>(env->GetArrayLength(images));
    auto* rawMatPointers = env->GetLongArrayElements(images, nullptr);
    for (int i = 0; i < imagesLength; i++) {
        const auto& matIn = *reinterpret_cast<cv::Mat*>(rawMatPointers[i]);
        matricesIn.push_back(matIn);
    }
    env->ReleaseLongArrayElements(images,  rawMatPointers, 0);
    ptr->stitch(matricesIn, matrixOut);
}

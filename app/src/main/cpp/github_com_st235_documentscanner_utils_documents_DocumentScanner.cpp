#include "github_com_st235_documentscanner_utils_documents_DocumentScanner.h"

#include <opencv2/core.hpp>

#include "scanner/DocumentScanner.h"

JNIEXPORT jlong JNICALL Java_github_com_st235_documentscanner_utils_documents_DocumentScanner_init(
        JNIEnv* env, jclass clazz) {
    const auto* scanner = new scanner::DocumentScanner();
    return reinterpret_cast<jlong>(scanner);
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_DocumentScanner_deinit(
        JNIEnv* env, jclass clazz,
        jlong scannerPointer) {
    const auto* scanner = reinterpret_cast<scanner::DocumentScanner*>(scannerPointer);
    delete scanner;
}

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_documents_DocumentScanner_wrapPerspective(
        JNIEnv* env, jclass clazz,
        jlong scannerPointer,
        jlong image, jfloatArray jcorners,
        jlong out) {
    const auto cornersLength = static_cast<uint32_t>(env->GetArrayLength(jcorners));

    if (cornersLength != 8) {
        return;
    }

    const auto& scanner = *reinterpret_cast<scanner::DocumentScanner*>(scannerPointer);
    cv::Mat& matrixIn = *reinterpret_cast<cv::Mat*>(image);
    cv::Mat& matrixOut = *reinterpret_cast<cv::Mat*>(out);

    float corners[8];
    auto* rawCorners = env->GetFloatArrayElements(jcorners, nullptr);
    for (int i = 0; i < 8; i++) {
        corners[i] = static_cast<float>(rawCorners[i]);
    }
    env->ReleaseFloatArrayElements(jcorners,  rawCorners, 0);

    scanner.wrapPerspective(matrixIn, corners, matrixOut);
}

JNIEXPORT jfloatArray JNICALL Java_github_com_st235_documentscanner_utils_documents_DocumentScanner_findCorners(
        JNIEnv* env, jclass clazz,
        jlong scannerPointer,
        jlong image) {
    const auto& scanner = *reinterpret_cast<scanner::DocumentScanner*>(scannerPointer);
    cv::Mat& matrixIn = *reinterpret_cast<cv::Mat*>(image);

    float corners[8];
    if (!scanner.findCorners(matrixIn, corners)) {
        return nullptr;
    }

    jfloat array[8];
    array[0] = static_cast<jfloat>(corners[0]);
    array[1] = static_cast<jfloat>(corners[1]);
    array[2] = static_cast<jfloat>(corners[2]);
    array[3] = static_cast<jfloat>(corners[3]);
    array[4] = static_cast<jfloat>(corners[4]);
    array[5] = static_cast<jfloat>(corners[5]);
    array[6] = static_cast<jfloat>(corners[6]);
    array[7] = static_cast<jfloat>(corners[7]);

    jfloatArray result = env->NewFloatArray(8);
    if (!result) {
        return nullptr;
    }

    env->SetFloatArrayRegion(result, 0, 8, array);
    return result;
}

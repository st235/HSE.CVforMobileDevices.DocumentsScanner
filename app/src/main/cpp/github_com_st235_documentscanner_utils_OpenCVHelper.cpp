#include "github_com_st235_documentscanner_utils_OpenCVHelper.h"

#include <cstdint>
#include <math.h>
#include <vector>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_OpenCVHelper_helloWorld(
        JNIEnv* env, jclass clazz,
        jlong image, jfloatArray jcorners,
        jlong out) {
    const auto cornersLength = static_cast<int32_t>(env->GetArrayLength(jcorners));

    if (cornersLength != 8) {
        return;
    }

    cv::Mat& matrixIn = *reinterpret_cast<cv::Mat*>(image);
    cv::Mat& matrixOut = *reinterpret_cast<cv::Mat*>(out);

    float corners[8];
    auto* rawCorners = env->GetFloatArrayElements(jcorners, nullptr);
    for (int i = 0; i < 8; i++) {
        corners[i] = static_cast<float>(rawCorners[i]);
    }
    env->ReleaseFloatArrayElements(jcorners,  rawCorners, 0);

    float ltx = corners[0];
    float lty = corners[1];
    float rtx = corners[2];
    float rty = corners[3];
    float rbx = corners[4];
    float rby = corners[5];
    float lbx = corners[6];
    float lby = corners[7];

    // Order is left top, right top, right bottom, left bottom
    std::vector<cv::Point2f> source_corners;
    std::vector<cv::Point2f> destination_corners;

    source_corners.push_back(cv::Point2f(ltx, lty));
    source_corners.push_back(cv::Point2f(rtx, rty));
    source_corners.push_back(cv::Point2f(rbx, rby));
    source_corners.push_back(cv::Point2f(lbx, lby));

    float width = std::max(sqrtf(pow(rtx - ltx, 2) + pow(rty - lty, 2)),
                      sqrtf(pow(rbx - lbx, 2) + pow(rby - lby, 2)));
    float height = std::max(sqrtf(pow(rtx - rbx, 2) + pow(rty - rby, 2)),
                       sqrtf(pow(ltx - lbx, 2) + pow(lty - lby, 2)));

    destination_corners.push_back(cv::Point2f(0, 0));
    destination_corners.push_back(cv::Point2f(width, 0));
    destination_corners.push_back(cv::Point2f(width, height));
    destination_corners.push_back(cv::Point2f(0, height));

    cv::Mat M = cv::getPerspectiveTransform(source_corners, destination_corners);

    cv::Size warped_image_size = cv::Size(cvRound(width), cvRound(height));
    cv::warpPerspective(matrixIn, matrixOut, M, warped_image_size);
}

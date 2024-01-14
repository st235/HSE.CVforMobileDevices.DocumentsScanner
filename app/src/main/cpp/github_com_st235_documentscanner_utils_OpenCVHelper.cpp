#include "github_com_st235_documentscanner_utils_OpenCVHelper.h"

#include <cstdint>
#include <math.h>
#include <vector>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

JNIEXPORT void JNICALL Java_github_com_st235_documentscanner_utils_OpenCVHelper_wrapPerspective(
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

    // Order is left top, right top, right bottom, left bottom.
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

JNIEXPORT jfloatArray JNICALL Java_github_com_st235_documentscanner_utils_OpenCVHelper_findCorners(
        JNIEnv* env, jclass clazz, jlong image) {
    cv::Mat& matrixIn = *reinterpret_cast<cv::Mat*>(image);

    const auto& kernel =
            cv::getStructuringElement(cv::MORPH_RECT, cv::Size(5, 5));

    cv::Mat tempImage;
    cv::morphologyEx(matrixIn, tempImage, cv::MORPH_CLOSE, kernel,  /* anchor= */ cv::Point(-1, -1),  /* iterations= */ 3);

//    cv::Mat mask = cv::Mat::zeros(tempImage.rows, tempImage.cols, CV_8UC1);
//    cv::Mat bgModel = cv::Mat::zeros(1, 65, CV_64FC1);
//    cv::Mat fgModel = cv::Mat::zeros(1, 65, CV_64FC1);
//
//    cv::Rect rect = cv::Rect(20, 20, tempImage.cols - 20, tempImage.rows - 20);
//    cv::cvtColor(tempImage, tempImage, cv::COLOR_BGR2RGB);
//    cv::grabCut(tempImage, mask, rect, bgModel, fgModel, 5, cv::GC_INIT_WITH_RECT);
//    // 0 = cv::GC_BGD, 1 = cv::GC_FGD, 2 = cv::PR_BGD, 3 = cv::GC_PR_FGD.
//    cv::Mat mask2 = (mask == 1) + (mask == 3);
//    cv::Mat dest;

    cv::Mat tempImage2 = tempImage;
//    tempImage.copyTo(tempImage2, mask2);

    cv::cvtColor(tempImage2, tempImage2, cv::COLOR_RGB2GRAY);
    cv::blur(tempImage2, tempImage2, cv::Size(11,11));
    cv::Canny(tempImage2, tempImage2, 0, 100, /* kernel size= */ 3);
    cv::morphologyEx(tempImage2, tempImage2, cv::MORPH_DILATE, kernel);

    std::vector<std::vector<cv::Point>> contours;
    std::vector<cv::Vec4i> hierarchy;
    findContours(tempImage2, contours, hierarchy, cv::RETR_TREE, cv::CHAIN_APPROX_SIMPLE);

    std::sort(contours.begin(), contours.end(), [](const std::vector<cv::Point>& one, const std::vector<cv::Point>& another) {
        return cv::contourArea(one) > cv::contourArea(another);
    });

    std::vector<cv::Point> approxCurve;
    for (size_t i = 0; i < contours.size(); i++) {
        const auto& contour = contours[i];
        double archLength = cv::arcLength(contour, /* closed= */ true);

        approxCurve.clear();
        cv::approxPolyDP(contour, approxCurve, 0.02 * archLength, /* closed= */ true);

        if (approxCurve.size() == 4) {
            break;
        }
    }

    if (approxCurve.size() != 4) {
        return nullptr;
    }

    std::sort(approxCurve.begin(), approxCurve.end(), [](const cv::Point& one, const cv::Point& another) {
        return one.x < another.x;
    });

    size_t topLeftIndex = 0;
    size_t bottomLeftIndex = 1;
    if (approxCurve[1].y < approxCurve[0].y) {
        topLeftIndex = 1;
        bottomLeftIndex = 0;
    }
    size_t topRightIndex = 2;
    size_t bottomRightIndex = 3;
    if (approxCurve[3].y < approxCurve[2].y) {
        topRightIndex = 3;
        bottomRightIndex = 2;
    }

    jfloat array[8];
    array[0] = static_cast<jfloat>(approxCurve[topLeftIndex].x);
    array[1] = static_cast<jfloat>(approxCurve[topLeftIndex].y);
    array[2] = static_cast<jfloat>(approxCurve[topRightIndex].x);
    array[3] = static_cast<jfloat>(approxCurve[topRightIndex].y);
    array[4] = static_cast<jfloat>(approxCurve[bottomRightIndex].x);
    array[5] = static_cast<jfloat>(approxCurve[bottomRightIndex].y);
    array[6] = static_cast<jfloat>(approxCurve[bottomLeftIndex].x);
    array[7] = static_cast<jfloat>(approxCurve[bottomLeftIndex].y);

    jfloatArray result = env->NewFloatArray(8);
    if (!result) {
        return nullptr;
    }

    env->SetFloatArrayRegion(result, 0, 8, array);
    return result;
}

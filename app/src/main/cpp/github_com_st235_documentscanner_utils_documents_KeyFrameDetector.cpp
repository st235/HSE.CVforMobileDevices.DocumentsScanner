#include "github_com_st235_documentscanner_utils_documents_KeyFrameDetector.h"

#include <opencv2/imgproc.hpp>


JNIEXPORT jdouble JNICALL Java_github_com_st235_documentscanner_utils_documents_KeyFrameDetector_getFramesDiff(
        JNIEnv* env, jclass clazz,
        jlong one,
        jlong another) {
    cv::Mat& currentFrame = *reinterpret_cast<cv::Mat*>(one);
    cv::Mat& nextFrame = *reinterpret_cast<cv::Mat*>(another);

    cv::Mat currentGrayscaleFrame;
    cv::Mat nextGrayscaleFrame;
    cv::cvtColor(currentFrame, currentGrayscaleFrame, cv::COLOR_BGR2GRAY);
    cv::cvtColor(nextFrame, nextGrayscaleFrame, cv::COLOR_BGR2GRAY);

    cv::Mat scoreImg;
    double maxScore;

    cv::matchTemplate(currentGrayscaleFrame, nextGrayscaleFrame, scoreImg, cv::TM_CCOEFF_NORMED);
    cv::minMaxLoc(scoreImg, 0, &maxScore);

    return static_cast<jdouble>(maxScore);
}

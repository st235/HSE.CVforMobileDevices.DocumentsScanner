#include "DocumentScanner.h"

#include <cstdint>
#include <math.h>
#include <vector>
#include <opencv2/imgproc.hpp>

namespace scanner {

void DocumentScanner::wrapPerspective(const cv::Mat& image,
                                      float corners[8],
                                      cv::Mat& outImage) const {
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
    cv::warpPerspective(image, outImage, M, warped_image_size);
}

bool DocumentScanner::findCorners(const cv::Mat& image,
                                  float corners[8]) const {
    const auto& kernel =
            cv::getStructuringElement(cv::MORPH_RECT, cv::Size(5, 5));

    cv::Mat tempImage;
    cv::morphologyEx(image, tempImage, cv::MORPH_CLOSE, kernel,  /* anchor= */ cv::Point(-1, -1),  /* iterations= */ 3);

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
    cv::Canny(tempImage2, tempImage2, 100, 220, /* kernel size= */ 5);
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
        return false;
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

    corners[0] = approxCurve[topLeftIndex].x;
    corners[1] = approxCurve[topLeftIndex].y;
    corners[2] = approxCurve[topRightIndex].x;
    corners[3] = approxCurve[topRightIndex].y;
    corners[4] = approxCurve[bottomRightIndex].x;
    corners[5] = approxCurve[bottomRightIndex].y;
    corners[6] = approxCurve[bottomLeftIndex].x;
    corners[7] = approxCurve[bottomLeftIndex].y;
    return true;
}

} // namespace scanner

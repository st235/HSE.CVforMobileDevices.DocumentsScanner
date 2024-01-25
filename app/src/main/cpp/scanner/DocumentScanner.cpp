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

} // namespace scanner

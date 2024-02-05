#include "HoughDocumentScanner.h"

#include <algorithm>
#include <cstdint>
#include <cmath>
#include <vector>
#include <opencv2/imgproc.hpp>

namespace {

void HoughTransform(const cv::Mat& image,
                    uint16_t thetaStep,
                    uint16_t pointsPerLineThreshold,
                    std::vector<std::pair<cv::Point, cv::Point>>& pointsOut) {
    // Max distance to the line equals to the length of a diagonal of the image.
    int maxDistance = hypot(image.rows, image.cols);

    cv::Mat houghSpace = cv::Mat::zeros(360, maxDistance, CV_16UC1);

    for (size_t r = 0; r < image.rows; r++) {
        for (size_t c = 0; c < image.cols; c++) {
            // Filtering out not real images.
            if (image.at<uint8_t>(r, c) != 255) {
                continue;
            }

            for (uint16_t t = 0; t <= 360; t += thetaStep) {
                // Converting degrees to radians.
                double theta = 1.0 * t * M_PI / 180.0;
                auto rho = static_cast<int32_t>(c*std::cos(theta) + r*std::sin(theta));

                if (rho < 0 || rho > maxDistance) {
                    continue;
                }

                houghSpace.at<uint16_t>(t, rho)++;
            }
        }
    }

    for (size_t theta = 0; theta < houghSpace.rows; theta++) {
        for (size_t rho = 1; rho < houghSpace.cols; rho++) {
            if (houghSpace.at<uint16_t>(theta, rho) < pointsPerLineThreshold) {
                continue;
            }

            cv::Point p1;
            cv::Point p2;
            double thetaRadians = 1.0 * theta * M_PI / 180.0;

            int x0 = cvRound(rho * cos(thetaRadians));
            int y0 = cvRound(rho * sin(thetaRadians));

            p1.x = cvRound(x0 + 1000 * (-sin(thetaRadians)));
            p1.y = cvRound(y0 + 1000 * (cos(thetaRadians)));

            p2.x = cvRound(x0 - 1000 * (-sin(thetaRadians)));
            p2.y = cvRound(y0 - 1000 * (cos(thetaRadians)));

            pointsOut.emplace_back(std::make_pair(p1, p2));
        }
    }
}

} // namespace

namespace scanner {

bool HoughDocumentScanner::findCorners(const cv::Mat& image,
                                       float corners[8]) const {
    cv::Mat interimImage;
    cv::cvtColor(image, interimImage, cv::COLOR_BGR2GRAY);

    const auto& kernel =
            cv::getStructuringElement(cv::MORPH_RECT, cv::Size(5, 5));
    cv::morphologyEx(interimImage, interimImage, cv::MORPH_DILATE, kernel,  /* anchor= */ cv::Point(-1, -1),  /* iterations= */ 5);
    cv::GaussianBlur(interimImage, interimImage, cv::Size(3, 3), 0);
    cv::morphologyEx(interimImage, interimImage, cv::MORPH_ERODE, kernel,  /* anchor= */ cv::Point(-1, -1),  /* iterations= */ 5);

    cv::Canny(interimImage, interimImage, /* threshold1= */ 100, /* threshold2= */ 200);
}

} // namespace scanner

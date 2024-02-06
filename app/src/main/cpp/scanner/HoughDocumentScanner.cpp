#include "HoughDocumentScanner.h"

#include <algorithm>
#include <cstdint>
#include <cmath>
#include <vector>
#include <opencv2/imgproc.hpp>

namespace {

struct Line {
    double rho;
    double theta;
    uint32_t count;

    Line(double rho, double theta, uint32_t count) {
        this->rho = rho;
        this->theta = theta;
        this->count = count;
    }

    Line(const Line& that) {
        if (this != &that) {
            this->rho = that.rho;
            this->theta = that.theta;
            this->count = that.count;
        }
    }

    Line& operator=(const Line& that) {
        if (this != &that) {
            this->rho = that.rho;
            this->theta = that.theta;
            this->count = that.count;
        }

        return *this;
    }
};

void HoughTransform(const cv::Mat& image,
                    uint16_t thetaStep,
                    uint16_t pointsPerLineThreshold,
                    std::vector<Line>& pointsOut) {
    // Max distance to the line equals to the length of a diagonal of the image.
    int maxDistance = hypot(image.rows, image.cols);

    cv::Mat houghSpace = cv::Mat::zeros(360, maxDistance, CV_16UC1);

    for (size_t r = 0; r < image.rows; r++) {
        for (size_t c = 0; c < image.cols; c++) {
            // Filtering out not real images.
            if (image.at<uint8_t>(r, c) != 255) {
                continue;
            }

            for (uint16_t t = 0; t < 360; t += thetaStep) {
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
            const auto count = houghSpace.at<uint16_t>(theta, rho);
            if (count < pointsPerLineThreshold) {
                continue;
            }
            pointsOut.emplace_back(Line(rho, theta, count));
        }
    }
}

} // namespace

namespace scanner {

bool HoughDocumentScanner::findCorners(const cv::Mat& image,
                                       float corners[8]) const {
    cv::Mat interimImage;
    cv::cvtColor(image, interimImage, cv::COLOR_BGR2GRAY);
    cv::Canny(interimImage, interimImage, /* threshold1= */ 50, /* threshold2= */ 200);


}

} // namespace scanner

#include "ImageStitcher.h"

#include <opencv2/imgproc.hpp>
#include <opencv2/calib3d/calib3d.hpp>

void ImageStitcher::stitchHorizontally(const cv::Mat& one,
                                       const cv::Mat& another,
                                       cv::Mat& out,
                                       double matchingThreshold) const {
    cv::Mat image1 = one;
    cv::Mat image2 = another;

    std::vector<cv::KeyPoint> image1Keypoints, image2Keypoints;
    cv::Mat image1Descriptors, image2Descriptors;

    _detector->detectAndCompute(image1, cv::noArray(), image1Keypoints, image1Descriptors);
    _detector->detectAndCompute(image2, cv::noArray(), image2Keypoints, image2Descriptors);

    if (image1Keypoints.size() < image2Keypoints.size()) {
        std::swap(image1, image2);
        std::swap(image1Keypoints, image2Keypoints);
        std::swap(image1Descriptors, image2Descriptors);
    }

    std::vector<std::vector<cv::DMatch>> matches;

    _matcher->knnMatch(image1Descriptors, image2Descriptors, matches, /* k= */ 2);
    std::vector<cv::Point2f> image1Points, image2Points;
    image1Points.reserve(matches.size());
    image2Points.reserve(matches.size());

    for (const auto& match: matches) {
        if (match[0].distance < matchingThreshold * match[1].distance) {
            image1Points.push_back(image1Keypoints[match[0].queryIdx].pt);
            image2Points.push_back(image2Keypoints[match[0].trainIdx].pt);
        }
    }

    cv::Mat H = cv::findHomography(image1Points, image2Points, cv::RANSAC);

    cv::Mat result;
    cv::warpPerspective(image1, result, H, cv::Size(image1.cols + image2.cols, std::max(image1.rows, image2.rows)));
    cv::Mat half(result, cv::Rect(0, 0, image2.cols, image2.rows));
    image2.copyTo(half);

    out = result;
}

void ImageStitcher::stitchVertically(const std::vector<cv::Mat>& images, cv::Mat& out) const {
    if (images.empty()) {
        return;
    }

    cv::Mat current;
    cv::rotate(images[0], current, cv::ROTATE_90_COUNTERCLOCKWISE);
    for (size_t i = 1; i < images.size(); i++) {
        cv::Mat t;
        cv::rotate(images[i], t, cv::ROTATE_90_COUNTERCLOCKWISE);
        stitchHorizontally(current, t, current);
    }

    cv::rotate(current, out, cv::ROTATE_90_CLOCKWISE);
}

#include "ImageStitcher.h"

#include <opencv2/imgproc.hpp>

void ImageStitcher::stitchHorizontally(const cv::Mat& one,
                                       const cv::Mat& another,
                                       cv::Mat& out) const {
    std::vector<cv::KeyPoint> oneKeypoints, anotherKeypoints;
    cv::Mat oneDescriptors, anotherDescriptors;

    _detector->detectAndCompute(one, cv::noArray(), oneKeypoints, oneDescriptors);
    _detector->detectAndCompute(another, cv::noArray(), anotherKeypoints, anotherDescriptors);

    std::vector<cv::DMatch> matches;

    _matcher->match(oneDescriptors, anotherDescriptors, matches);

    std::vector<cv::Point2d> onePoints, anotherPoints;
    onePoints.reserve(matches.size());
    anotherPoints.reserve(matches.size());

    double maxDist = 0; double minDist = 1e6;
    for (const auto& m : matches) {
        double dist = m.distance;
        if (dist < minDist) minDist = dist;
        if (dist > maxDist) maxDist = dist;
    }

    double searchRadius = 2.5 * minDist;
    for (const auto& match: matches) {
        if (match.distance <= searchRadius) {
            onePoints.push_back(oneKeypoints.at(match.queryIdx).pt);
            anotherPoints.push_back(anotherKeypoints.at(match.trainIdx).pt);
        }
    }

    cv::Rect oneCropArea(0, 0, one.cols, one.rows);
    cv::Rect anotherCropArea(0, 0, another.cols, another.rows);

    double dy = 0;
    int resultWidth = one.cols;
    for (int i = 0; i < onePoints.size(); ++i) {
        if (onePoints[i].x < resultWidth) {
            oneCropArea.width = onePoints[i].x;

            anotherCropArea.x = anotherPoints[i].x;
            anotherCropArea.width = another.cols - anotherCropArea.x;

            dy = onePoints[i].y - anotherPoints[i].y;
            resultWidth = onePoints[i].x;
        }
    }

    cv::Mat oneResult = one(oneCropArea);
    cv::Mat anotherResult = another(anotherCropArea);

    int maxHeight = std::max(oneResult.rows, anotherResult.rows);
    int maxWidth = oneResult.cols + anotherResult.cols;

    cv::Mat result( /* rows= */ maxHeight + abs(dy), /* cols= */ maxWidth, CV_8UC3);

    if (dy > 0) {
        oneResult.copyTo(result(cv::Rect(0, 0, oneResult.cols, oneResult.rows)));
        anotherResult.copyTo(result(cv::Rect(oneResult.cols, abs(dy), anotherResult.cols, anotherResult.rows)));
    } else {
        oneResult.copyTo(result(cv::Rect(0, abs(dy), oneResult.cols, oneResult.rows)));
        anotherResult.copyTo(result(cv::Rect(oneResult.cols, 0, anotherResult.cols, anotherResult.rows)));
    }

    out = result;
}

void ImageStitcher::stitch(const std::vector<cv::Mat>& images, cv::Mat& out) const {
    if (images.empty()) {
        return;
    }

    cv::Mat current = images[0].t();
    for (size_t i = 1; i < images.size(); i++) {
        // out is already transposed.
        stitchHorizontally(current, images[i].t(), current);
    }

    out = current.t();
}

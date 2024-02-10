#ifndef DOCUMENTSCANNER_IMAGESTITCHER_H
#define DOCUMENTSCANNER_IMAGESTITCHER_H

#include <opencv2/core.hpp>
#include <opencv2/features2d.hpp>
#include <vector>

class ImageStitcher {
private:
    cv::Ptr<cv::SIFT> _detector;
    cv::Ptr<cv::FlannBasedMatcher> _matcher;

public:
    ImageStitcher():
        _detector(cv::SIFT::create()),
        _matcher(cv::FlannBasedMatcher::create()) {
        // Empty on purpose.
    }

    void stitchHorizontally(const cv::Mat& one, const cv::Mat& another, cv::Mat& out, double matchingThreshold = 0.4) const;

    void stitchVertically(const std::vector<cv::Mat>& images, cv::Mat& out) const;

    ~ImageStitcher() = default;
};


#endif //DOCUMENTSCANNER_IMAGESTITCHER_H

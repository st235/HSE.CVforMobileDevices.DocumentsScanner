#ifndef DOCUMENTSCANNER_IMAGESTITCHER_H
#define DOCUMENTSCANNER_IMAGESTITCHER_H

#include <opencv2/core.hpp>
#include <opencv2/features2d.hpp>
#include <vector>

class ImageStitcher {
private:
    cv::Ptr<cv::ORB> _detector;
    cv::Ptr<cv::DescriptorMatcher> _matcher;

public:
    ImageStitcher():
        _detector(cv::ORB::create()),
        _matcher(cv::DescriptorMatcher::create(cv::DescriptorMatcher::BRUTEFORCE)) {
        // Empty on purpose.
    }

    void stitchHorizontally(const cv::Mat& one, const cv::Mat& another, cv::Mat& out) const;

    void stitch(const std::vector<cv::Mat>& images, cv::Mat& out) const;

    ~ImageStitcher() = default;
};


#endif //DOCUMENTSCANNER_IMAGESTITCHER_H

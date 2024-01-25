#ifndef DOCUMENTSCANNER_DOCUMENTSCANNER_H
#define DOCUMENTSCANNER_DOCUMENTSCANNER_H

#include <opencv2/core.hpp>

namespace scanner {

class DocumentScanner {
public:
    virtual void wrapPerspective(const cv::Mat& image,
                                 float corners[8],
                                 cv::Mat& outImage) const;

    virtual bool findCorners(const cv::Mat& image,
                             float corners[8]) const = 0;

    virtual ~DocumentScanner() = default;
};

} // namespace scanner

#endif //DOCUMENTSCANNER_DOCUMENTSCANNER_H

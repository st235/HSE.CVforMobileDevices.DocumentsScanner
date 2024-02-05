#ifndef DOCUMENTSCANNER_HOUGHDOCUMENTSCANNER_H
#define DOCUMENTSCANNER_HOUGHDOCUMENTSCANNER_H

#include "DocumentScanner.h"

#include <opencv2/core.hpp>

namespace scanner {

class HoughDocumentScanner: public DocumentScanner {
public:
    bool findCorners(const cv::Mat& image,
                     float corners[8]) const override;
};

} // namespace scanner

#endif //DOCUMENTSCANNER_HOUGHDOCUMENTSCANNER_H

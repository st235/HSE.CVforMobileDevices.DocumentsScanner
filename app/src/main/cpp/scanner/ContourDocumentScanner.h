#ifndef DOCUMENTSCANNER_CONTOURDOCUMENTSCANNER_H
#define DOCUMENTSCANNER_CONTOURDOCUMENTSCANNER_H

#include "DocumentScanner.h"

#include <opencv2/core.hpp>

namespace scanner {

class ContourDocumentScanner : public DocumentScanner {
public:
    bool findCorners(const cv::Mat& image,
                     float corners[8]) const override;
};

} // namespace scanner

#endif //DOCUMENTSCANNER_CONTOURDOCUMENTSCANNER_H

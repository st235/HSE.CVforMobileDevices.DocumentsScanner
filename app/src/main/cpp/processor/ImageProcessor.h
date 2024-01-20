#ifndef DOCUMENTSCANNER_IMAGEPROCESSOR_H
#define DOCUMENTSCANNER_IMAGEPROCESSOR_H

#include <opencv2/core.hpp>

class ImageProcessor {
public:
    enum class BINARIZATION {
        GLOBAL,
        ADAPTIVE_MEAN,
        ADAPTIVE_GAUSSIAN,
        OTSU,
        TRIANGLE,
    };

    enum class FILTER {
        BOX,
        GAUSSIAN,
        MEDIAN,
        BILATERAL,
    };

    enum class DENOISING {
        TVL1,
        FAST_NI,
    };

    enum class CONTRAST {
        MULT,
        HISTOGRAM,
        CLAHE,
    };

    void rotate90(const cv::Mat& image, cv::Mat& out) const;

    void binarization(const cv::Mat& image, const BINARIZATION& mode, cv::Mat& out) const;

    void filter(const cv::Mat& image, const FILTER& mode, cv::Mat& out) const;

    void denoise(const cv::Mat& image, const DENOISING& mode, cv::Mat& out) const;

    void enhanceContrast(const cv::Mat& image, const CONTRAST& mode, cv::Mat& out) const;

};


#endif //DOCUMENTSCANNER_IMAGEPROCESSOR_H

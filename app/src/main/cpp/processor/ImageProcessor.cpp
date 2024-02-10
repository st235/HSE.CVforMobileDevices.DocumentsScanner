#include "ImageProcessor.h"

#include <opencv2/imgproc.hpp>
#include <opencv2/photo.hpp>
#include <vector>

namespace {

void HistogramEqualisation(const cv::Mat& image, cv::Mat& out) {
    cv::cvtColor(image, out, cv::COLOR_BGR2YCrCb);

    std::vector<cv::Mat> vec_channels;
    cv::split(out, vec_channels);

    cv::equalizeHist(vec_channels[0], vec_channels[0]);

    cv::merge(vec_channels, out);

    cv::cvtColor(out, out, cv::COLOR_YCrCb2BGR);
}

void CLAHE(const cv::Mat& image, cv::Mat& out) {
    cv::cvtColor(image, out, cv::COLOR_BGR2Lab);

    std::vector<cv::Mat> lab_planes(3);
    cv::split(out, lab_planes);

    cv::Ptr<cv::CLAHE> clahe = cv::createCLAHE();
    clahe->setClipLimit(4);
    cv::Mat dst;
    clahe->apply(lab_planes[0], dst);

    dst.copyTo(lab_planes[0]);
    cv::merge(lab_planes, out);

    cv::cvtColor(out, out, cv::COLOR_Lab2BGR);
}

bool CharThreshold(const cv::Mat& image,
                   double percent,
                   cv::Mat& out) {
    if (image.type() != CV_8UC1) {
        return false;
    }

    int histSize = 256;
    float range[] = { 0, 256 }; //the upper boundary is exclusive
    const float* histRange[] = { range };
    bool uniform = true, accumulate = false;
    cv::Mat hist;
    cv::calcHist(&image, 1, 0, cv::Mat(), hist, 1, &histSize, histRange, uniform, accumulate);

    double min, max;
    cv::Point minLoc, maxLoc;

    minMaxLoc(hist, &min, &max, &minLoc, &maxLoc);

    uint8_t maxColor = static_cast<uint8_t>(maxLoc.y);
    bool ruledOutColors[histSize];

    for (int32_t i = maxColor; i >= 0; i--) {
        bool isFrequencyAtMostPercentOfWhile = hist.at<float>(i) < (max * (1.0 - percent));
        ruledOutColors[i] = isFrequencyAtMostPercentOfWhile;
    }

    cv::Mat result = cv::Mat::zeros(image.rows, image.cols, CV_8UC1);

    for (size_t r = 0; r < image.rows; r++) {
        for (size_t c = 0; c < image.cols; c++) {
            uint8_t color = image.at<uint8_t>(r, c);
            if (color < maxColor && ruledOutColors[color]) {
                result.at<uint8_t>(r, c) = static_cast<uint8_t>(0);
            } else {
                result.at<uint8_t>(r, c) = static_cast<uint8_t>(255);
            }
        }
    }

    out = result;
    return true;
}

} // namespace

void ImageProcessor::rotate90(const cv::Mat& image,
                              cv::Mat& out) const {
    cv::rotate(image, out, cv::ROTATE_90_CLOCKWISE);
}

void ImageProcessor::binarization(const cv::Mat& image,
                                  const BINARIZATION& mode,
                                  cv::Mat& out) const {
    cv::cvtColor(image, out, cv::COLOR_BGR2GRAY);
    cv::GaussianBlur(out, out, cv::Size(3, 3), 0);

    switch (mode) {
        case BINARIZATION::GLOBAL:
            cv::threshold(out, out, 127,255,cv::THRESH_BINARY);
            break;
        case BINARIZATION::OTSU:
            cv::threshold(out, out, 0, 255, cv::THRESH_BINARY + cv::THRESH_OTSU);
            break;
        case BINARIZATION::TRIANGLE:
            cv::threshold(out, out, 0, 255, cv::THRESH_BINARY + cv::THRESH_TRIANGLE);
            break;
        case BINARIZATION::ADAPTIVE_MEAN:
            cv::adaptiveThreshold(out, out, 255, cv::ADAPTIVE_THRESH_MEAN_C, cv::THRESH_BINARY,11,2);
            break;
        case BINARIZATION::ADAPTIVE_GAUSSIAN:
            cv::adaptiveThreshold(out, out, 255, cv::ADAPTIVE_THRESH_GAUSSIAN_C, cv::THRESH_BINARY,11,2);
            break;
        case BINARIZATION::CHAR:
            CharThreshold(out, 0.95, out);
            break;
    }
}

void ImageProcessor::filter(const cv::Mat& image,
                            const FILTER& mode,
                            cv::Mat& out) const {
    switch (mode) {
        case FILTER::BOX:
            cv::boxFilter(image, out, /* ddepth= */ -1, cv::Size(7, 7));
            break;
        case FILTER::MEDIAN:
            cv::medianBlur(image, out, 3);
            break;
        case FILTER::GAUSSIAN:
            cv::GaussianBlur(image, out, cv::Size(7, 7), 0);
            break;
        case FILTER::BILATERAL:
            cv::bilateralFilter(image, out, /* d= */ 11, /* sigmaColor= */ 75, /* sigmaSpace= */ 75);
            break;
    }
}

void ImageProcessor::denoise(const cv::Mat& image,
                             const DENOISING& mode,
                             cv::Mat& out) const {
    switch (mode) {
        case DENOISING::TVL1: {
            cv::Mat temp;
            cv::cvtColor(image, temp, cv::COLOR_BGR2GRAY);

            std::vector<cv::Mat> images;
            images.push_back(temp);
            cv::denoise_TVL1(images, out);
            break;
        }
        case DENOISING::FAST_NI: {
            cv::fastNlMeansDenoisingColored(image, out);
            break;
        }
    }
}

void ImageProcessor::enhanceContrast(const cv::Mat& image,
                                     const CONTRAST& mode,
                                     cv::Mat& out) const {
    switch (mode) {
        case CONTRAST::MULT:
            image.convertTo(out, -1, 2, 0);
            break;
        case CONTRAST::HISTOGRAM:
            HistogramEqualisation(image, out);
            break;
        case CONTRAST::CLAHE:
            CLAHE(image, out);
            break;
    }
}

#ifndef RECONSTRUCTOR_H
#define RECONSTRUCTOR_H

#include <opencv2/core/mat.hpp>

class Reconstructor {
public:
    void reconstruct();
private:
    std::vector<cv::Mat> *vKFImColor;
    std::vector<cv::Mat> *vKFTcw;
};

#endif
#ifndef RECONSTRUCTOR_H
#define RECONSTRUCTOR_H

#include <opencv2/core/mat.hpp>
#include "System.h"

using namespace SLAM;

class Reconstructor {
public:
    void reconstruct();
private:
    System *slam;
    std::vector<cv::Mat> *vKFImColor;
    std::vector<cv::Mat> *vKFTcw;
};

#endif
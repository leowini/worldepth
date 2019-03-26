#ifndef RECONSTRUCTOR_H
#define RECONSTRUCTOR_H

#include <opencv2/core/mat.hpp>
#include "System.h"
//#include "TextureMapper.h"

using namespace SLAM;

class Reconstructor {
public:
    void reconstruct();
    void passImageToSlam(cv::Mat &im, double &tstamp);
    void endSlam(std::string filename);
private:
    System *slam;
    //TextureMapper *textureMapper;
    std::vector<cv::Mat> *vKFImColor;
    std::vector<cv::Mat> *vKFTcw;
};

#endif
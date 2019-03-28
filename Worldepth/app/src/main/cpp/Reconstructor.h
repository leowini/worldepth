#ifndef RECONSTRUCTOR_H
#define RECONSTRUCTOR_H

#include <opencv2/core/mat.hpp>
#include "System.h"
#include "TextureMapping/TextureMapper.h"

using namespace SLAM;

class Reconstructor {

public:
    Reconstructor(std::string & vocFile, std::string & settingsFile);
    bool hasKeyframes();
    void passImageToSlam(cv::Mat &im, double &tstamp);
    void endSlam(std::string filename, bool slamSuccess);
    void textureMap();

private:
    System *slam;
    TextureMapper *textureMapper;
    std::vector<cv::Mat> *vKFImColor;
    std::vector<cv::Mat> *vKFTcw;

};

#endif
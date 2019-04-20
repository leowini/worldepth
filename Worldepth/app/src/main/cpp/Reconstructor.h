#ifndef RECONSTRUCTOR_H
#define RECONSTRUCTOR_H

#include <opencv2/core/mat.hpp>
#include "System.h"
#include <camera_calibration.h>
#include "TextureMapping/TextureMapper.h"

using namespace SLAM;

class Reconstructor {

public:
    Reconstructor(std::string &vocFile, std::string &settingsFile);
    ~Reconstructor();
    bool hasKeyframes();
    void passImageToSlam(cv::Mat &im, double tstamp);
    void endSlam(const std::string &filename, bool success);
    void textureMap();
    void resetSlam();

private:
    System *slam = nullptr;
    TextureMapper *textureMapper;
    std::vector<cv::Mat> vKFImColor;
    std::vector<cv::Mat> vKFTcw;

};

#endif
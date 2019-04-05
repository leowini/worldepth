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
    bool hasKeyframes();
    void passImageToSlam(cv::Mat &im, double tstamp);
    void endSlam(const std::string &filename);
    void textureMap();
    void resetSlam();

private:
    System *slam;
    TextureMapper *textureMapper;
    std::vector<cv::Mat> vKFImColor;
    std::vector<cv::Mat> vKFTcw;

    //whether calibration is run or SLAM is
    calib::Settings *sptr;
    std::vector<cv::Mat> vecmatread(const std::string &filename);
};

#endif
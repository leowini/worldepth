#ifndef RECONSTRUCTOR_H
#define RECONSTRUCTOR_H

#include <opencv2/core/mat.hpp>
#include "System.h"
#include <camera_calibration.h>
//#include "TextureMapper.h"

using namespace SLAM;

class Reconstructor {

public:
    Reconstructor(std::string & vocFile, std::string & settingsFile);
    void passImageToSlam(cv::Mat &im, double &tstamp);
    void endSlam(std::string filename);
    void textureMap();
    void resetSlam();

private:
    System *slam;
    calib::Settings *sptr;
    //whether calibration is run or SLAM is
    //TextureMapper *textureMapper;
    std::vector<cv::Mat> *vKFImColor;
    std::vector<cv::Mat> *vKFTcw;

};

#endif
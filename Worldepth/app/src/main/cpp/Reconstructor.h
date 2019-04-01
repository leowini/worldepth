#ifndef RECONSTRUCTOR_H
#define RECONSTRUCTOR_H

#include <opencv2/core/mat.hpp>
#include "System.h"
#include "TextureMapping/TextureMapper.h"

using namespace SLAM;

class Reconstructor {

public:
    Reconstructor(const std::string & vocFile, const std::string & settingsFile);
    bool hasKeyframes();
    void passImageToSlam(cv::Mat &im, double tstamp);
    void endSlam(const std::string &filename, bool slamSuccess);
    void textureMap();

private:
    System *slam;
    TextureMapper *textureMapper;
    std::vector<cv::Mat> vKFImColor;
    std::vector<cv::Mat> vKFTcw;
    void vecmatwrite(const string& filename, const vector<cv::Mat>& matrices);
    vector<cv::Mat> vecmatread(const string& filename);
};

#endif
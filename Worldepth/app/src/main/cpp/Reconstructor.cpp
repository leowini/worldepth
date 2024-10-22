#include <RandomMap.h>
#include <camera_calibration.h>
#include "Reconstructor.h"

Reconstructor::Reconstructor(std::string &vocFile, std::string &settingsFile, std::string &internalPath)
: internalPath(internalPath) {
    if (slam == nullptr) {
        slam = new System(vocFile, settingsFile);
    }
    vKFImColor = std::vector<cv::Mat>();
    vKFTcw = std::vector<cv::Mat>();
}

Reconstructor::~Reconstructor() {
    //slam should be deleted to reinit here. Need to figure out orbvocload problem first.
    vKFImColor.clear();
    vKFTcw.clear();
    slam->Reset();
    delete slam;
    slam = nullptr;
}

bool Reconstructor::hasKeyframes() {
    return (!vKFImColor.empty());
}

void Reconstructor::passImageToSlam(cv::Mat &im, double tstamp) {
    //call the equivalent of System::TrackMonocular for TrackingInit or Tracking directly
    if (im.empty() || tstamp == 0) {
        cerr << "could not load image!" << endl;
    } else {
        cv::Mat Tcw = slam->TrackMonocular(im, tstamp);
        if (!Tcw.empty()) {
            vKFImColor.push_back(im.clone());
            vKFTcw.push_back(Tcw.clone());
        }
    }
}

void Reconstructor::endSlam(bool success) {
    //get finished map as reference
    if (success) writeMap(internalPath + "/Pointcloud.txt", slam->GetAllMapPoints());
    //System actually has a clear func, it's
    slam->Reset();
}


void Reconstructor::textureMap() {
    textureMapper = new TextureMapper(internalPath, vKFImColor, vKFTcw);
    textureMapper->textureMap();
    delete textureMapper;
    vKFImColor.clear();
    vKFTcw.clear();
}
#include <RandomMap.h>
#include <camera_calibration.h>
#include "Reconstructor.h"

Reconstructor::Reconstructor(std::string &vocFile, std::string &settingsFile) {
    slam = new System(vocFile, settingsFile);
    vKFImColor = std::vector<cv::Mat>();
    vKFTcw = std::vector<cv::Mat>();
}

Reconstructor::~Reconstructor() {
    delete slam;
    //delete textureMapper; this causes sigsegv
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

void Reconstructor::endSlam(const std::string &filename) {
    //get finished map as reference
    writeMap(filename, slam->GetAllMapPoints());
    slam->Shutdown();
    //System actually has a clear func, it's
    slam->Reset();
    //close any other threads (should be done already in System.Reset()
    delete slam;
}


void Reconstructor::textureMap() {
    textureMapper = new TextureMapper("/data/user/0/com.example.leodw.worldepth/files/SLAM.ply", vKFImColor, vKFTcw);
    textureMapper->textureMap();
    delete textureMapper;
    vKFImColor.clear();
    vKFTcw.clear();
}

void Reconstructor::resetSlam() {
    slam->Reset();
    slam->Shutdown();
}
#include <RandomMap.h>
#include "Reconstructor.h"

Reconstructor::Reconstructor(std::string & vocFile, std::string & settingsFile) {
    slam = new System(vocFile, settingsFile);
    vKFImColor = new std::vector<cv::Mat>();
    vKFTcw = new std::vector<cv::Mat>();
}

void Reconstructor::passImageToSlam(cv::Mat &im, double &tstamp) {
    //call the equivalent of System::TrackMonocular for TrackingInit or Tracking directly
    if (im.empty() || tstamp == 0){
        cerr << "could not load image!" << endl;
    } else {
        cv::Mat Tcw = slam->TrackMonocular(im, tstamp);
        if(!Tcw.empty()){
            vKFImColor->push_back(im.clone());
            vKFTcw->push_back(Tcw.clone());
        }
    }
}

void Reconstructor::endSlam(std::string filename) {
    //get finished map as reference
    writeMap(filename, slam->GetAllMapPoints());
    slam->Shutdown();
    //System actually has a clear func, it's
    slam->Reset();
    //close any other threads (should be done already in System.Reset()
    delete slam;
    //textureMapper = new TextureMapper("plyfile", vKFImColor, vKFTcw);
    delete vKFImColor;
    delete vKFTcw;
}

void Reconstructor::textureMap() {
    //textureMapper->textureMap();
}
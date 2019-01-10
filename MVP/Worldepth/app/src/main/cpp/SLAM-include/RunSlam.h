//
// Created by Soren Dahl on 12/27/18.
//

#ifndef WORLDEPTH_RUNSLAM_H
#define WORLDEPTH_RUNSLAM_H

#include <opencv2/core/mat.hpp>
#include <System.h>

namespace SLAM
{

//call this fist to start the TrackingInit and threads,
//we can remove the settings file, but I need to know how to access the phone's settings
System* start(std::string &vocFile, std::string &settingsFile);

//call this for each frame
void process(System * slam, cv::Mat &im, double tstamp);

//call this at the end, writes the map to the file and closes things
//for internal storage, this looks like "/data/data/com.example.leodw.worldepth/name of file" i think
//This could then read into reconstruction or go to the database so it isn't stored on the phone
void end(System * slam, std::string filename);

} //namespace SLAM

#endif //WORLDEPTH_RUNSLAM_H

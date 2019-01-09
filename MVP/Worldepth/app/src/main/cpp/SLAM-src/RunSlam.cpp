//
// Created by Soren Dahl on 12/17/18.
//

#include <RunSlam.h>
#include <TrackingInit.h>
#include <RandomMap.h>
#include <System.h>

namespace SLAM {

    void start (std::string & vocFile, std::string & settingsFile) {
        System slam(vocFile, settingsFile);
    }

    void process(cv::Mat im, double tstamp) {

        //call the equivalent of System::TrackMonocular for TrackingInit or Tracking directly
        if (im.empty()){
            //this makes and writes to an internal storage file, reused every time slam is run
            //we may change this to instead pass it to Poisson
            end("data/user/0/com.example.leodw.worldepth/files/SLAM.txt");
        } else {

        }
    }


    void end (std::string filename) {
        //get finished map as reference
        //writeMap(filename, map);

        //this should run map.clear and delete map
        //delete slam;

        //System actually has a clear func, it's
        //slam.Reset()

        //close any other threads
    }
}



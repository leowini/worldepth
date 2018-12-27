//
// Created by Soren Dahl on 12/17/18.
//

#include <RunSlam.h>
#include <TrackingInit.h>
#include <RandomMap.h>

namespace SLAM {

    void start () {
        //TrackingInit * slam = new TrackingInit();
        //maybe do other threading here
    }

    void process(cv::Mat im, double tstamp) {

        //call the equivalent of System::TrackMonocular for TrackingInit or Tracking directly
    }


    void end (std::string filename) {
        //get finished map as reference
        //writeMap(filename, map);

        //this should run map.clear and delete map
        //delete slam;
        
        //close any other threads
    }
}



//
// Created by Soren Dahl on 12/17/18.
//

#include <RunSlam.h>
#include <TrackingInit.h>

namespace SLAM {

    void start () {
        TrackingInit();
        //maybe do other threading here
    }

    void process(cv::Mat im, double tstamp) {

        //call the equivalent of System::TrackMonocular for TrackingInit or Tracking directly
    }


    void end () {
        //get
    }
}



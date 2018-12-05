/*
 * Created by Michael Duan on 12/1/18.
 * Creates the list to which frames (processed cv::mats) are stored
 * Initiates processing thread

*/


#ifndef WORLDEPTH_TRACKINGINIT_H
#define WORLDEPTH_TRACKINGINIT_H

#include <opencv2/core/mat.hpp>
#include <list>
#include <vector>
#include <thread>
#include "Frame.h"

namespace SLAM
{

    class TrackingInit {
    public:
        //basic constructor
        TrackingInit();

        //Send frames to FrameList
        void sendToFrameList(Frame frame);
    private:
        //Stores processed frames
        std::vector<Frame> mFrame;

        //Whether or not images are being captured for SLAM processing
        bool isProcessing;

        //method called by processing thread
        void processing();

        //While isProcessing is true, this will send frames to be processed
        void sendToProcess();
    };

}


#endif //WORLDEPTH_TRACKINGINIT_H

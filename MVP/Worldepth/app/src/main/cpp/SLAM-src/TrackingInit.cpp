//
// Created by Michael Duan on 12/1/18.
//

#include "TrackingInit.h"
#include <thread>

namespace SLAM
{
    TrackingInit::TrackingInit() {
        isProcessing = true;
        std::thread consume (TrackingInit::processing);
    }

    void TrackingInit::sendToFrameList(SLAM::Frame frame) {
        FrameList.push_back(frame);
    }

    void TrackingInit::processing() {
        while (isProcessing) {
            TrackingInit::sendToProcess();
        }
    }

    void TrackingInit::sendToProcess() {
        Frame coolFrame = FrameList[0];

        //call function on FrameList[0]

        FrameList.erase(FrameList.begin());
    }
}

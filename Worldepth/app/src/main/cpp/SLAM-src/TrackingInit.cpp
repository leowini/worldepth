//
// Created by Michael Duan on 12/1/18.
//

#include "TrackingInit.h"
#include <thread>

namespace SLAM
{
    TrackingInit::TrackingInit() {
        isProcessing = true;
        frameList = FrameList();
        std::thread consume (&TrackingInit::processing, this);
        consume.join();
    }

    void TrackingInit::sendToFrameList(Frame *frame) {
        frameList.addFrame(frame);
    }

    void TrackingInit::sendToKeyFrameList(SLAM::KeyFrame *keyFrame) {
        frameList.addKeyFrame(keyFrame);
    }

    void TrackingInit::processing() {
        while (TrackingInit::isProcessing) {
            TrackingInit::sendToProcess();
        }
    }

    void TrackingInit::sendToProcess() {

        frameList.getFrameDatabase()[0];

        //call function on FrameList[0]
    }
}

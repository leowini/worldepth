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
#include "FrameList.h"
#include "KeyFrame.h"
#include "Map.h"
#include "Tracking.h"
#include "ORBVocabulary.h"
#include "LocalMapping.h"

namespace SLAM
{
    class Map;
    class Tracking;

    class TrackingInit {
    public:
        //basic constructor
        TrackingInit(string &strVocFile, string &strSettingsFile);

        //Send frames to FrameList
        void sendToFrameList(Frame* frame);

        //Send keyFrames to KeyFrameList
        void sendToKeyFrameList(KeyFrame* keyFrame);

        cv::Mat beginTracking(const cv::Mat im, const double timestamp);

    private:
        //Stores processed frames
        FrameList* frameList;

        //Map lol
        Map* map;

        Tracking* mTracker;

        //ORB VOCAB
        ORBVocabulary* mVocabulary;

        std::thread* mptLocalMapping;
        std::thread* mptLoopClosing;
        std::thread* mptTracking;

        //Whether or not images are being captured for SLAM processing
        bool isProcessing;

        //method called by processing thread
        void processing();

        //While isProcessing is true, this will send frames to be processed
        void sendToProcess();

        int mTrackingState;
        std::vector<MapPoint*> mTrackedMapPoints;
        std::vector<cv::KeyPoint> mTrackedKeyPointsUn;
    };

}


#endif //WORLDEPTH_TRACKINGINIT_H

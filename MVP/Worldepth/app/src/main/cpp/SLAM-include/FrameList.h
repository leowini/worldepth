//
// Created by Michael Duan on 12/3/18.
//

#ifndef WORLDEPTH_FRAMELIST_H
#define WORLDEPTH_FRAMELIST_H

#include <list>
#include <vector>
#include <thread>
#include "Frame.h"
#include "KeyFrame.h"

namespace SLAM
{
    class KeyFrame;
    class Frame;

    class FrameList {
    public:
        //basic constructor
        FrameList();

        //Add a frame
        void addFrame(Frame* frame);

        //Add a KeyFrame
        void addKeyFrame(KeyFrame* keyFrame);

        //Remove a frame
        void removeFrame(int pos);

        //Remove a KeyFrame
        void removeKeyFrame(int pos);

        std::vector<Frame*> getFrameDatabase();

        std::vector<KeyFrame*> getKeyFrameDatabase();

        //checks if frame list is empty
        bool isFrameListEmpty();

        //checks if keyframe list is empty
        bool isKeyFrameListEmpty();
    private:
        //Frame List
        std::vector<Frame*> frameDatabase;

        //KeyFrame List
        std::vector<KeyFrame*> keyFrameDatabase;
    };

}


#endif //WORLDEPTH_FRAMELIST_H

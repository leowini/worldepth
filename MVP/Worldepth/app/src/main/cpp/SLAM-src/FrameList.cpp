//
// Created by Michael Duan on 12/3/18.
//

#include "FrameList.h"
#include <vector>

namespace SLAM
{
    FrameList::FrameList() {

    }

    void FrameList::addFrame(Frame *frame) {
        frameDatabase.push_back(frame);
    }

    void FrameList::addKeyFrame(KeyFrame *keyFrame) {
        keyFrameDatabase.push_back(keyFrame);
    }

    void FrameList::removeFrame(int pos) {
        frameDatabase.erase(frameDatabase.begin() + pos);
    }

    void FrameList::removeKeyFrame(int pos) {
        keyFrameDatabase.erase(keyFrameDatabase.begin() + pos);
    }

    std::vector<Frame*> FrameList::getFrameDatabase() {
        return frameDatabase;
    }

    std::vector<KeyFrame*> FrameList::getKeyFrameDatabase() {
        return keyFrameDatabase;
    }

    bool FrameList::isFrameListEmpty() {
        return static_cast<int>(FrameList::frameDatabase.size()) < 1;
    }

    bool FrameList::isKeyFrameListEmpty() {
        return static_cast<int>(FrameList::keyFrameDatabase.size()) < 1;
    }
}
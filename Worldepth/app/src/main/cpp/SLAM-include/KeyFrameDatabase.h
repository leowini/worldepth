//
// Created by Soren Dahl on 12/4/18.
//

#ifndef WORLDEPTH_KEYFRAMEDATABASE_H
#define WORLDEPTH_KEYFRAMEDATABASE_H

#include <vector>
#include <list>
#include <set>

#include "KeyFrame.h"
#include "Frame.h"
#include "ORBVocabulary.h"

#include<mutex>


namespace SLAM
{

    class KeyFrame;
    class Frame;


    class KeyFrameDatabase
    {
    public:

        KeyFrameDatabase(const ORBVocabulary &voc);

        void add(KeyFrame* pKF);

        void erase(KeyFrame* pKF);

        void clear();

        // Loop Detection
        std::vector<KeyFrame *> DetectLoopCandidates(KeyFrame* pKF, float minScore);

        // Relocalization
        std::vector<KeyFrame*> DetectRelocalizationCandidates(Frame* F);

    protected:

        // Associated vocabulary
        const ORBVocabulary* mpVoc;

        // Inverted file
        std::vector<list<KeyFrame*> > mvInvertedFile;

        // Mutex
        std::mutex mMutex;
    };

}

#endif //WORLDEPTH_KEYFRAMEDATABASE_H

//
// Created by Soren Dahl on 11/29/18.
//

#ifndef WORLDEPTH_ORBVOCABULARY_H
#define WORLDEPTH_ORBVOCABULARY_H

#include"DBoW2/FORB.h"
#include"DBoW2/TemplatedVocabulary.h"

namespace SLAM
{

    typedef DBoW2::TemplatedVocabulary<DBoW2::FORB::TDescriptor, DBoW2::FORB>
            ORBVocabulary;

}

#endif //WORLDEPTH_ORBVOCABULARY_H

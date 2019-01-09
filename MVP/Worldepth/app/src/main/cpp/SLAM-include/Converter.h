//
// Created by Soren Dahl on 12/2/18.
//

#ifndef WORLDEPTH_CONVERTER_H
#define WORLDEPTH_CONVERTER_H

#include<opencv2/core/core.hpp>

namespace SLAM
{
    class Converter {
    public:
        static std::vector<cv::Mat> toDescriptorVector(const cv::Mat &Descriptors);
    };
}



#endif //WORLDEPTH_CONVERTER_H

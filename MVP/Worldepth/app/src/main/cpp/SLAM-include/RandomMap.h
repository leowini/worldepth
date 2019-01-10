//
// Created by Soren Dahl on 12/3/18.
//

#ifndef WORLDEPTH_RANDOMMAP_H
#define WORLDEPTH_RANDOMMAP_H

#include <iostream>
#include <string>
#include <Map.h>
#include <MapPoint.h>
#include <opencv2/core/core.hpp>

namespace SLAM {


    //Call this one to write if you already have a map
    void writeMap(std::string filename, Map & map);

    //call this one to run it all
    void makeMapAndWrite(string &filename, size_t num);

}

#endif //WORLDEPTH_RANDOMMAP_H

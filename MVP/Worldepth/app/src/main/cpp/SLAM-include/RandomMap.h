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

    //this writes to a pre-existing text file (must be empty and opened)
    void writeMap(ofstream &file, Map & map);

    //this does everything with a random map with num amount of random points
void makeMapAndWrite(string &filename, size_t num);

}

#endif //WORLDEPTH_RANDOMMAP_H

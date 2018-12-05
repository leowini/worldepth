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

//Call this method with a file and a map to output the map coords to the file
    void writeMap(ofstream file, string filename, Map *pMap);

//Call this method to generate a random map with 1000 points, returns the pointer to the map
    Map* randomMap();

}

#endif //WORLDEPTH_RANDOMMAP_H

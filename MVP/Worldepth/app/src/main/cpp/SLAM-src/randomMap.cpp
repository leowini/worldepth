//
// Created by Soren Dahl on 12/3/18.
//

#include <RandomMap.h>

namespace SLAM {

    void writeMap(std::string filename, std::vector<MapPoint *> vpMapPoints) {
        FILE * file;
        file = fopen(filename.c_str(), "w");
        //std::vector<MapPoint*> vpMapPoints = map.GetAllMapPoints();
        for(MapPoint * pMP: vpMapPoints) {
            if (pMP == NULL) { continue; }
            cv::Mat pos = pMP->GetWorldPos();
            for(int i = 0; i < pos.rows; i++) {
                for (int j = 0; j < pos.cols; j++) {
                    fputs(to_string(pos.at<float>(i, j)).c_str(), file);
                    fputs(string (" ").c_str(), file);
                }
            }
            cv::Mat norm = pMP->GetNormal();
            for(int i = 0; i < norm.rows; i++) {
                for(int j = 0; j < norm.cols; j++) {
                    fputs(to_string(norm.at<float>(i, j)).c_str(), file);
                    fputs(string (" ").c_str(), file);
                }
            }
            fputs(string ("\n").c_str(), file);
        }
        fclose(file);
    }



    void putPointsInMap(size_t size, KeyFrame & kf, Map & map) {
        for(int i = 0; i < size; i++) {
            cv::Point3f point(rand() % 100, rand() % 100, rand() % 100);
            cv::Mat pos(point);
            MapPoint * pMP = new MapPoint(pos, &kf, &map);
            pMP->UpdateNormalAndDepth();
            map.AddMapPoint(pMP);
        }

    }


    /*
    void makeMapAndWrite(string &filename, size_t num) {
        Map * pmap = new Map();
        Frame empty;
        KeyFrame emptykf(empty, pmap);
        putPointsInMap(num, emptykf, *pmap);
        writeMap(filename, pmap->GetAllMapPoints());

        pmap->clear();
        delete pmap;

    }
     */
}


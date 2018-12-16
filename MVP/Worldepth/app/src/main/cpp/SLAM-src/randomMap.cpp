//
// Created by Soren Dahl on 12/3/18.
//

#include <RandomMap.h>

namespace SLAM {

    void writeMap(ofstream & file, Map & map) {
        std::vector<MapPoint*> vpMapPoints = map.GetAllMapPoints();
        for(MapPoint * pMP: vpMapPoints) {
            cv::Mat pos = pMP->GetWorldPos();
            for(int i = 0; i < pos.rows; i++) {
                for (int j = 0; j < pos.cols; j++) {
                    file << pos.at<float>(i, j) << " ";
                }
            }
            cv::Mat norm = pMP->GetNormal();
            for(int i = 0; i < norm.rows; i++) {
                for(int j = 0; j < norm.cols; j++) {
                    file << norm.at<float>(i, j) << " ";
                }
            }
            file << "\n";
        }

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



    void makeMapAndWrite(string &filename, size_t num) {
        Map * pmap = new Map();
        Frame empty;
        KeyFrame emptykf(empty, pmap);
        putPointsInMap(num, emptykf, *pmap);
        ofstream file;
        file.open(filename, std::ofstream::trunc);
        writeMap(file, *pmap);

        pmap->clear();
        delete pmap;

    }
}


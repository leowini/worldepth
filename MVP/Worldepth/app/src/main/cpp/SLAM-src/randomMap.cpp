//
// Created by Soren Dahl on 12/3/18.
//

#include <RandomMap.h>

namespace SLAM {

    void writeMap(ofstream file, string filename, Map * pMap) {
        file.open(filename);
        std::vector<MapPoint*> vpMapPoints = pMap->GetAllMapPoints();

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

    void randomMap(Map * pMap) {
        Frame empty = Frame();
        KeyFrame * pKF = new KeyFrame(empty, pMap);
        for(int i = 0; i < 1000; i++) {
            cv::Point3f p3f = cv::Point3f(rand() % 100, rand() % 100, rand() % 100);
            cv::Mat pos(p3f);
            MapPoint * pMP = new MapPoint(pos, pKF, pMap);
            pMP->UpdateNormalAndDepth();
            pMap->AddMapPoint(pMP);
        }

    }
}


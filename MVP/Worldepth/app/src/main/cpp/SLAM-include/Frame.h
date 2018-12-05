//
// Created by Soren Dahl on 11/28/18.
// This is the class that describes the input, based on cv::Mat images
// It has some other stuff, such as detecting keypoints and storing map and pose info,
// but the cv::Mat &im is the most important part (I think)
//

#ifndef WORLDEPTH_FRAME_H
#define WORLDEPTH_FRAME_H


#include <opencv2/core/mat.hpp>

#include "ORBExtractor.h"
#include "ORBVocabulary.h"
#include "MapPoint.h"

namespace SLAM
{

    class KeyFrame;
    class MapPoint;

#define FRAME_GRID_ROWS 48
#define FRAME_GRID_COLS 64

class Frame {

public:

    //basic constructor
    Frame();

    //constructor by reference
    Frame(const Frame &frame);

    //constructor by image, needs ORB descriptors
    Frame(const cv::Mat &imGray, const double &timeStamp, ORBextractor* extractor,
                 ORBVocabulary* voc, cv::Mat &K, cv::Mat &distCoef, const float &bf, const float &thDepth);

    void ExtractORB(const cv::Mat & im);

    // Compute Bag of Words representation.
    void ComputeBoW();

    // Set the camera pose.
    void SetPose(cv::Mat Tcw);

    // Computes rotation, translation and camera center matrices from the camera pose.
    void UpdatePoseMatrices();

    // Returns the camera center.
    inline cv::Mat GetCameraCenter(){
        return mOw.clone();
    }

    // Returns inverse of rotation
    inline cv::Mat GetRotationInverse(){
        return mRwc.clone();
    }

    // Check if a MapPoint is in the frustum of the camera
    // and fill variables of the MapPoint to be used by the tracking
    // DO THIS AFTER MAPPOINT
    //*******************************
    bool isInFrustum(MapPoint* pMP, float viewingCosLimit);
    //*******************************

    // Compute the cell of a keypoint (return false if outside the grid)
    bool PosInGrid(const cv::KeyPoint & kp, int & posX, int & posY);


    //I'm Not exactly sure what this does, but I think it finds the KeyPoints in an area and returns
    //a vector of addresses relating to those keypoints
    vector<size_t> GetFeaturesInArea(const float & x, const float  & y, const float  & r,
                                     const int minLevel=-1, const int maxLevel=-1) const;



public:
    // Vocabulary used for relocalization.
    ORBVocabulary* mpORBvocabulary;

    // Feature extractor. The right is used only in the stereo case.
    ORBextractor* mpORBextractorLeft;

    // Frame timestamp.
    double mTimeStamp;

    // Calibration matrix and OpenCV distortion parameters.
    cv::Mat mK;
    static float fx;
    static float fy;
    static float cx;
    static float cy;
    static float invfx;
    static float invfy;
    cv::Mat mDistCoef;

    // Stereo baseline multiplied by fx.
    float mbf;

    // Stereo baseline in meters.
    //I don' think we need this, but i'm not sure, I'll leave it here
     float mb;

    // Threshold close/far points. Close points are inserted from 1 view.
    // Far points are inserted as in the monocular case from 2 views.
    float mThDepth;

    // Number of KeyPoints.
    int N;

    // Vector of keypoints (original for visualization) and undistorted (actually used by the system).
    // In the stereo case, mvKeysUn is redundant as images must be rectified.
    // In the RGB-D case, RGB images can be distorted.
    std::vector<cv::KeyPoint> mvKeys;
    std::vector<cv::KeyPoint> mvKeysUn;

    // Corresponding stereo coordinate and depth for each keypoint.
    // "Monocular" keypoints have a negative value.
    //I also don't think we need these, but I'll keep them in in case
     std::vector<float> mvuRight;
     std::vector<float> mvDepth;

    // Bag of Words Vector structures.
    DBoW2::BowVector mBowVec;
    DBoW2::FeatureVector mFeatVec;

    // ORB descriptor, each row associated to a keypoint.
    cv::Mat mDescriptors;

    // MapPoints associated to keypoints, NULL pointer if no association.
    //DO THIS AFTER MAPPOINT
    //**********************************
    std::vector<MapPoint*> mvpMapPoints;
    //**********************************

    // Flag to identify outlier associations.
    std::vector<bool> mvbOutlier;

    // Keypoints are assigned to cells in a grid to reduce matching complexity when projecting MapPoints.
    static float mfGridElementWidthInv;
    static float mfGridElementHeightInv;
    std::vector<std::size_t> mGrid[FRAME_GRID_COLS][FRAME_GRID_ROWS];

    // Camera pose.
    cv::Mat mTcw;

    // Current and Next Frame id.
    static long unsigned int nNextId;
    long unsigned int mnId;

    // Reference Keyframe.
    //DO THIS AFTER KEYFRAME
    //************************
    KeyFrame* mpReferenceKF;
    //************************

    // Scale pyramid info.
    int mnScaleLevels;
    float mfScaleFactor;
    float mfLogScaleFactor;
    vector<float> mvScaleFactors;
    vector<float> mvInvScaleFactors;
    vector<float> mvLevelSigma2;
    vector<float> mvInvLevelSigma2;

    // Undistorted Image Bounds (computed once).
    static float mnMinX;
    static float mnMaxX;
    static float mnMinY;
    static float mnMaxY;

    static bool mbInitialComputations;


private:


    // Computes image bounds for the undistorted image (called in the constructor).
    void ComputeImageBounds(const cv::Mat & im);

    void UndistortKeyPoints();

    // Assign keypoints to the grid for speed up feature matching (called in the constructor).
    //I assume this uses DBOW and FORB
    void AssignFeaturesToGrid();

    // Rotation, translation and camera center
    cv::Mat mRcw;
    cv::Mat mtcw;
    cv::Mat mRwc;
    cv::Mat mOw; //==mtwc
};




}; //Namespcae SLAM


#endif //WORLDEPTH_FRAME_H

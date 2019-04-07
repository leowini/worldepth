#ifndef TEXTUREMAPPER_H
#define TEXTUREMAPPER_H

struct float3 {float x, y, z;};

class TextureMapper {

public:
    TextureMapper(const std::string &plyFilename, std::vector<cv::Mat> &source, std::vector<cv::Mat> &TcwPoses, int patchSize = 7);
    void textureMap();

private:
    std::string plyFilename;
    std::string tempFilename;

    cv::Size sourceImgSize;
    int sourceWidth;
    int sourceHeight;
    int targetWidth;
    int targetHeight;
    int targetSize;
    int sourceSize;
    int sourceChannels;
    int targetChannels;

    int patchSize;
    std::vector<cv::Point3f> vertices;
    std::vector<cv::Mat> TcwPoses;
    std::vector<cv::Mat> source;
    std::vector<cv::Mat> target;
    std::vector<cv::Mat> texture;
    cv::Mat cameraMatrix;
    cv::Mat distCoef;

    void init();
    void read_ply_file();
    void write_ply_file(double *weights, double *acc_red, double *acc_grn, double *acc_blu);
    void align();
    //void reconstruct();
    //int Mixi();
    cv::Mat patchSearch(int iterations);
    //void vote(cv::Mat &completenessPatchMatches, cv::Mat &coherencePatchMatches);
    std::vector<std::vector<std::vector<int>>> findSourcePatches(cv::Mat &completenessPatchMatches, cv::Mat &coherencePatchMatches,
                                                                    int x, int y, int t);
    //bool isInTargetPatch(cv::Vec<float, 4> targetMatch, int x, int y, int t);
    //int Tixi(std::vector<std::vector<int>> &completenessPatches, std::vector<std::vector<int>> &coherencePatches, int c);
    float distance(int sx, int sy, int st,
                    int tx, int ty, int tt,
                    int patchSize, float threshold);
    int randomInt(int min, int max);
    //std::vector<cv::Mat> getRGBD(std::vector<cv::Mat> &target, std::vector<cv::Mat> &TcwPoses);
    void projectToSurface();
};

#endif
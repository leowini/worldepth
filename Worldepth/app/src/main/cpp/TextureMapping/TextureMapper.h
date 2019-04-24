#ifndef TEXTUREMAPPER_H
#define TEXTUREMAPPER_H

struct float3 {float x, y, z;};

class TextureMapper {

public:
    TextureMapper(const std::string &plyFilename, std::vector<cv::Mat> &source, std::vector<cv::Mat> &TcwPoses, int patchSize = 7);
    void textureMap();

private:
    std::string plyFilename;
    std::string internalPath;
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

    const float nearClippingPLane = 1;
    const float farClippingPLane = 1000;
    std::vector<cv::Mat> depthMaps;
    std::vector<cv::Mat> thetas;
    std::vector<cv::Vec3b> faces;

    int patchSize;
    int ntris;
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
    void reconstruct();
    int Mixi(int &x, int &y, int &t);
    cv::Mat patchSearch(int iterations);
    void vote(std::vector<cv::Mat> &completenessPatchMatches, std::vector<cv::Mat> &coherencePatchMatches);
    std::vector<std::vector<std::vector<int>>> findSourcePatches(std::vector<cv::Mat> &completenessPatchMatches, std::vector<cv::Mat> &coherencePatchMatches,
                                                                    int x, int y, int t);
    //bool isInTargetPatch(cv::Vec<float, 4> targetMatch, int x, int y, int t);
    int Tixi(int &x, int &y, int &t, std::vector<std::vector<int>> &completenessPatches, std::vector<std::vector<int>> &coherencePatches, int c);
    float distance(int sx, int sy, int st,
                    int tx, int ty, int tt,
                    int patchSize, float threshold);
    int randomInt(int min, int max);
    float min3(const float &a, const float &b, const float &c);
    float max3(const float &a, const float &b, const float &c);
    float edgeFunction(const cv::Vec3f &a, const cv::Vec3f &b, const cv::Vec3f &c);
    void convertToRaster(
            const cv::Vec3f &vertexWorld,
            const cv::Mat &worldToCamera,
            const float &l,
            const float &r,
            const float &t,
            const float &b,
            const float &near,
            const uint32_t &imageWidth,
            const uint32_t &imageHeight,
            cv::Vec3f &vertexRaster
    );
    std::vector<cv::Mat> getRGBD();
    void projectToSurface();
    void multVecMatrix(const cv::Mat &matrix, const cv::Vec3f &src, cv::Vec3f &dst);
};

#endif
#ifndef TEXTUREMAPPER_H
#define TEXTUREMAPPER_H

struct float3 {float x, y, z;};

class TextureMapper {

public:
    TextureMapper(const std::string &plyFilename, std::vector<cv::Mat> &source, std::vector<cv::Mat> &TcwPoses, int patchSize = 7);
    void textureMap();

private:
    static constexpr float inchToMm = 25.4;
    enum FitResolutionGate { kFill = 0, kOverscan };
    float top, bottom, left, right;
    float focalLength = 20; // in mm
// 35mm Full Aperture in inches
    float filmApertureWidth = 0.980;
    float filmApertureHeight = 0.735;

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
    std::vector<cv::Vec3f> normals;

    int patchSize;
    int ntris;
    std::vector<cv::Point3f> vertices;
    std::vector<cv::Mat> TcwPoses;
    std::vector<cv::Mat> source;
    std::vector<cv::Mat> target;
    std::vector<cv::Mat> texture;
    cv::Mat intrinsicMatrix;
    cv::Mat distCoef;

    void init();
    void read_ply_file();
    void write_ply_file(float *weights, float *acc_red, float *acc_grn, float *acc_blu);
    void align();
    void reconstruct();
    double Mixi(int &x, int &y, int &t);
    cv::Mat patchSearch(int iterations);
    void vote(std::vector<cv::Mat> &completenessPatchMatches, std::vector<cv::Mat> &coherencePatchMatches);
    std::vector<std::vector<std::vector<float>>> findSourcePatches(std::vector<cv::Mat> &completenessPatchMatches, std::vector<cv::Mat> &coherencePatchMatches,
                                                                    int x, int y, int t);
    //bool isInTargetPatch(cv::Vec<float, 4> targetMatch, int x, int y, int t);
    double Tixi(int &x, int &y, int &t, std::vector<std::vector<float>> &completenessPatches, std::vector<std::vector<float>> &coherencePatches, int c);
    float distance(int sx, int sy, int st,
                    int tx, int ty, int tt,
                    int patchSize, float threshold);
    int randomInt(int min, int max);
    float min3(const float &a, const float &b, const float &c);
    float max3(const float &a, const float &b, const float &c);
    float edgeFunction(const cv::Vec3f &a, const cv::Vec3f &b, const cv::Vec3f &c);
    void convertToRaster(const cv::Point3f &vertexWorld, cv::Mat &cameraMatrix,
            cv::Point3f &vertexRaster
    );
    void getRGBD();
    void projectToSurface();
    void multVecMatrix(const cv::Mat &matrix, const cv::Vec3f &src, cv::Vec3f &dst);
    cv::Vec3f getNormalFromTri(cv::Vec3b &tri);
    float calcThetaAngle(cv::Vec3f &triangleNormal, cv::Vec3f &cameraPose);
    void computeScreenCoordinates(
            const float &filmApertureWidth,
            const float &filmApertureHeight,
            const uint32_t &imageWidth,
            const uint32_t &imageHeight,
            const FitResolutionGate &fitFilm,
            const float &nearClippingPLane,
            const float &focalLength,
            float &top, float &bottom, float &left, float &right
    );
    cv::Mat computeCameraMatrix(cv::Mat &extrinsicMatrix);
};

#endif
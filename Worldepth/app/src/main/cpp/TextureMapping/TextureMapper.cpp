#include <vector>
#include <thread>
#include <chrono>
#include <vector>
#include <sstream>
#include <fstream>
#include <iostream>
#include <cstring>
#include <iterator>
#include <opencv2/core/mat.hpp>
#include <opencv2/core.hpp>
#include <opencv/cv.hpp>

#include "tinyply.h"
#include "TextureMapper.h"

using namespace tinyply;

/**
** Assuming that source is a vector of cv::Mats
**/
TextureMapper::TextureMapper(const std::string &internalPath, std::vector<cv::Mat> &source,
                             std::vector<cv::Mat> &TcwPoses, int patchSize) :
        internalPath(internalPath), source(source), TcwPoses(TcwPoses), patchSize(patchSize) {
    plyFilename = internalPath + "/SLAM.ply";
    read_ply_file(); //gets vertices from the file
    init(); //clones source and target
}

/**
** Initialize
** the targets and textures with their corresponding source images,
** i.e., Ti = Si and Mi = Si.
**/
void TextureMapper::init() {
    tempFilename = internalPath + "/temp.ply";
    for (auto &t : source) {
        target.push_back(t.clone());
        texture.push_back(t.clone());
    }
    sourceImgSize = source.at(0).size();
    sourceWidth = sourceImgSize.width;
    sourceHeight = sourceImgSize.height;
    targetWidth = source.at(0).size().width;
    targetHeight = source.at(0).size().height;
    targetSize = static_cast<int>(target.size());
    sourceSize = static_cast<int>(source.size());

    sourceChannels = source.at(0).channels();
    targetChannels = target.at(0).channels();

    cv::FileStorage fSettings(internalPath + "/CalibVals.yaml",
                              cv::FileStorage::READ);
    float fx = fSettings["Camera_fx"];
    float fy = fSettings["Camera_fy"];
    float cx = fSettings["Camera_cx"];
    float cy = fSettings["Camera_cy"];
    cv::Mat cameraMatrix = cv::Mat::eye(3, 3, CV_32F);
    cameraMatrix.at<float>(0, 0) = fx;
    cameraMatrix.at<float>(1, 1) = fy;
    cameraMatrix.at<float>(0, 2) = cx;
    cameraMatrix.at<float>(1, 2) = cy;
    cameraMatrix.copyTo(this->cameraMatrix);

    cv::Mat DistCoef(4, 1, CV_32F);
    DistCoef.at<float>(0) = fSettings["Camera_k1"];
    DistCoef.at<float>(1) = fSettings["Camera_k2"];
    DistCoef.at<float>(2) = fSettings["Camera_p1"];
    DistCoef.at<float>(3) = fSettings["Camera_p2"];
    const float k3 = fSettings["Camera.k3"];
    if (k3 != 0) {
        DistCoef.resize(5);
        DistCoef.at<float>(4) = k3;
    }
    DistCoef.copyTo(this->distCoef);
}

void TextureMapper::textureMap() {
    align();
    reconstruct();
    projectToSurface();
}

void TextureMapper::align() {
    int iterations = 1;
    std::vector<cv::Mat> completenessPatchMatches = patchSearch(iterations);
    std::vector<cv::Mat> coherencePatchMatches = patchSearch(iterations);
    vote(completenessPatchMatches, coherencePatchMatches);
}

cv::Mat TextureMapper::patchSearch(int iterations) {

    // convert patch diameter to patch radius
    patchSize /= 2;

    // For each source pixel, output a 3-vector to the best match in
    // the target, with an error as the last channel. The 3-vector should be the location of the patch center.
    int sizes[3] = {sourceWidth, sourceHeight, sourceSize};
    cv::Mat out(3, sizes, CV_8UC4);

    // Iterate over source frames, finding a match in the target where
    // the mask is high.
    int dxMax = targetWidth - patchSize - 1;
    int dyMax = targetHeight - patchSize - 1;
    int dtMax = targetSize - 1;
    auto start = std::chrono::high_resolution_clock::now();
    for (int t = 0; t < sourceSize; t++) {
        // INITIALIZATION - uniform random assignment of out matrix values.
        for (int y = 0; y < sourceHeight; y++) {
            for (int x = 0; x < sourceWidth; x++) {
                int dx = randomInt(patchSize, dxMax);
                int dy = randomInt(patchSize, dyMax);
                int dt = randomInt(0, dtMax);
                cv::Vec4b pixel(static_cast<uchar>(dx), static_cast<uchar>(dy),
                                static_cast<uchar>(dt),
                                static_cast<uchar>(distance(x, y, t, dx, dy, dt, patchSize,
                                                            HUGE_VAL)));
                out.at<cv::Vec4b>(x, y, t) = pixel;
            }
        }
    }
    auto finish = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double> elapsed = finish - start;
    std::ofstream myfile;
    myfile.open(internalPath + "/initTime.txt");
    myfile << std::to_string(elapsed.count()) + "\n";
    myfile.close();
    bool forwardSearch = true;

    //Split the out matrix into 4 channels for dx, dy, dt, and error.
    std::vector<cv::Mat> channels(4);
    cv::split(out, channels);
    cv::Mat dx = channels[0], dy = channels[1], dt = channels[2], error = channels[3];

    for (int i = 0; i < iterations; i++) {
        //printf("Iteration %d\n", i);

        // PROPAGATION
        if (forwardSearch) {
            // Forward propagation - compare left, center and up
            for (int t = 0; t < sourceSize; t++) {
                for (int y = 1; y < sourceHeight; y++) {
                    for (int x = 1; x < sourceWidth; x++) {
                        if (*error.ptr(x, y, t) + 0 > 0) {
                            float distLeft = distance(x, y, t,
                                                      (*dx.ptr(x - 1, y, t)) + 1,
                                                      (*dy.ptr(x - 1, y, t)),
                                                      (*dt.ptr(x - 1, y, t)),
                                                      patchSize, *error.ptr(x, y, t));

                            if (distLeft < *error.ptr(x, y, t)) {
                                *dx.ptr(x, y, t) = static_cast<uchar>((*dx.ptr(x - 1, y, t)) + 1);
                                *dy.ptr(x, y, t) = *dy.ptr(x - 1, y, t);
                                *dt.ptr(x, y, t) = *dt.ptr(x - 1, y, t);
                                *error.ptr(x, y, t) = static_cast<uchar>(distLeft);
                            }

                            float distUp = distance(x, y, t,
                                                    *dx.ptr(x, y - 1, t),
                                                    (*dy.ptr(x, y - 1, t)) + 1,
                                                    *dt.ptr(x, y - 1, t),
                                                    patchSize, *error.ptr(x, y, t));

                            if (distUp < *error.ptr(x, y, t)) {
                                *dx.ptr(x, y, t) = *dx.ptr(x, y - 1, t);
                                *dy.ptr(x, y, t) = static_cast<uchar>((*dy.ptr(x, y - 1, t)) + 1);
                                *dt.ptr(x, y, t) = *dt.ptr(x, y - 1, t);
                                *error.ptr(x, y, t) = static_cast<uchar>(distUp);
                            }
                        }

                        // TODO: Consider searching across time as well

                    }
                }
            }

        } else {
            // Backward propagation - compare right, center and down
            for (int t = sourceSize - 1; t >= 0; t--) {
                for (int y = sourceHeight - 2; y >= 0; y--) {
                    for (int x = sourceWidth - 2; x >= 0; x--) {
                        if (*error.ptr(x, y, t) > 0) {
                            float distRight = distance(x, y, t,
                                                       (*dx.ptr(x + 1, y, t)) - 1,
                                                       *dy.ptr(x + 1, y, t),
                                                       *dt.ptr(x + 1, y, t),
                                                       patchSize, *error.ptr(x, y, t));

                            if (distRight < *error.ptr(x, y, t)) {
                                *dx.ptr(x, y, t) = static_cast<uchar>((*dx.ptr(x + 1, y, t)) - 1);
                                *dy.ptr(x, y, t) = *dy.ptr(x + 1, y, t);
                                *dt.ptr(x, y, t) = *dt.ptr(x + 1, y, t);
                                *error.ptr(x, y, t) = static_cast<uchar>(distRight);
                            }

                            float distDown = distance(x, y, t,
                                                      *dx.ptr(x, y + 1, t),
                                                      (*dy.ptr(x, y + 1, t)) - 1,
                                                      *dt.ptr(x, y + 1, t),
                                                      patchSize, *error.ptr(x, y, t));

                            if (distDown < *error.ptr(x, y, t)) {
                                *dx.ptr(x, y, t) = *dx.ptr(x, y + 1, t);
                                *dy.ptr(x, y, t) = static_cast<uchar>((*dy.ptr(x, y + 1, t)) - 1);
                                *dt.ptr(x, y, t) = *dt.ptr(x, y + 1, t);
                                *error.ptr(x, y, t) = static_cast<uchar>(distDown);
                            }
                        }

                        // TODO: Consider searching across time as well
                    }
                }
            }
        }

        forwardSearch = !forwardSearch;

        // RANDOM SEARCH
        for (int t = 0; t < sourceSize; t++) {
            for (int y = 0; y < sourceHeight; y++) {
                for (int x = 0; x < sourceWidth; x++) {
                    if (*error.ptr(x, y, t) > 0) {

                        int radius = targetWidth > targetHeight ? targetWidth : targetHeight;

                        // search an exponentially smaller window each iteration
                        while (radius > 8) {
                            // Search around current offset vector (distance-weighted)

                            // clamp the search window to the image
                            int minX = (int) (*dx.ptr(x, y, t)) - radius;
                            int maxX = (int) (*dx.ptr(x, y, t)) + radius + 1;
                            int minY = (int) (*dy.ptr(x, y, t)) - radius;
                            int maxY = (int) (*dy.ptr(x, y, t)) + radius + 1;
                            if (minX < 0) { minX = 0; }
                            if (maxX > targetWidth) { maxX = targetWidth; }
                            if (minY < 0) { minY = 0; }
                            if (maxY > targetHeight) { maxY = targetHeight; }

                            int randX = randomInt(minX, maxX - 1);
                            int randY = randomInt(minY, maxY - 1);
                            int randT = randomInt(0, targetSize - 1);
                            float dist = distance(x, y, t,
                                                  randX, randY, randT,
                                                  patchSize, *error.ptr(x, y, t));
                            if (dist < *error.ptr(x, y, t)) {
                                *dx.ptr(x, y, t) = static_cast<uchar>(randX);
                                *dy.ptr(x, y, t) = static_cast<uchar>(randY);
                                *dt.ptr(x, y, t) = static_cast<uchar>(randT);
                                *error.ptr(x, y, t) = static_cast<uchar>(dist);
                            }

                            radius >>= 1;

                        }
                    }
                }
            }
        }
    }

    //Merge output channels back together
    std::vector<cv::Mat> outs = {dx, dy, dt, error};
    cv::merge(outs, out);

    return out;

}

float TextureMapper::distance(int sx, int sy, int st,
                              int tx, int ty, int tt,
                              int patchSize, float threshold) {

    // Do not use patches on boundaries
    if (tx < patchSize || tx >= targetWidth - patchSize ||
        ty < patchSize || ty >= targetHeight - patchSize) {
        return HUGE_VAL;
    }

    // Compute distance between patches
    // Average L2 distance in RGB space
    float dist = 0;

    int x1 = std::max({-patchSize, -sx, -tx});
    int x2 = std::min({patchSize, -sx + sourceWidth - 1, -tx + targetWidth - 1});
    int y1 = std::max({-patchSize, -sy, -ty});
    int y2 = std::min({patchSize, -sy + sourceHeight - 1, -ty + targetHeight - 1});

    for (int c = 0; c < targetChannels; c++) {
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {

                uint8_t const *sourceValue_ptr(
                        source.at(static_cast<unsigned long>(st)).ptr(sx + x, sy + y, c));
                uint8_t const *targetValue_ptr(
                        target.at(static_cast<unsigned long>(tt)).ptr(tx + x, ty + y, c));

                float delta = *sourceValue_ptr - *targetValue_ptr;
                dist += delta * delta;

                // Early termination
                if (dist > threshold) { return HUGE_VAL; }
            }
        }
    }

    return dist;
}

void TextureMapper::vote(std::vector<cv::Mat> &completenessPatchMatches, std::vector<cv::Mat> &coherencePatchMatches) {
    //For each pixel in the target
    for (int t = 0; t < target.size(); t++) {
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                std::vector<std::vector<std::vector<int>>> patches = findSourcePatches(completenessPatchMatches, coherencePatchMatches, x, y, t);
                std::vector<std::vector<int>> completenessPatches = patches[0];
                std::vector<std::vector<int>> coherencePatches = patches[1];

                for (int c = 0; c < sourceChannels; c++) {
                    Tixi(x, y, t, completenessPatches, coherencePatches, c);
                }
            }
        }
    }
}

std::vector<std::vector<std::vector<int>>>
TextureMapper::findSourcePatches(std::vector<cv::Mat> &completenessPatchMatches, std::vector<cv::Mat> &coherencePatchMatches,
                                 int x, int y, int t) {
    std::vector<std::vector<std::vector<int>>> sourcePatches;
    std::vector<std::vector<int>> completenessPatches;
    sourcePatches[0] = completenessPatches;
    std::vector<std::vector<int>> coherencePatches;
    sourcePatches[1] = coherencePatches;
    //Find patches in target that contain the pixel
    int targetx = x;
    int targety = y;
    int x1 = std::max(-patchSize, -targetx);
    int x2 = std::min(patchSize, -targetx + targetWidth - 1);
    int y1 = std::max(-patchSize, -targety);
    int y2 = std::min(patchSize, -targety + targetHeight - 1);

    //Completeness: Find Source patches that have target patches as their most similar patch
    //For each pixel in completenessPatchMatches
    for (int st = 0; st < source.size(); st++) {
        for (int sy = 0; sy < sourceHeight; sy++) {
            for (int sx = 0; sx < sourceWidth; sx++) {
                cv::Vec<float, 4> patchMatch = completenessPatchMatches.at(st).at<cv::Vec<float,4>>(sx, sy);
                int stx = static_cast<int>(patchMatch[0]), sty = static_cast<int>(patchMatch[1]), stt = static_cast<int>(patchMatch[2]);
                if ( /* is in x range */(stx >= x1 && stx <= x2) &&
                                        /** is in y range */ (sty >= y1 && sty <= y2) && stt == t) {
                    //return value of the target pixel within the source patch
                    std::vector<int> targetPixel;
                    //Find target pixel in source patch
                    for (int c = 0; c < sourceChannels; c++) {
                        targetPixel.push_back(
                                static_cast<int &&>(source.at(st).at<cv::Vec<float, 4>>(
                                        (x - stx) + sx, (y - sty) + sy)[c]));
                    }
                    sourcePatches[0].push_back(targetPixel);
                }
            }
        }
    }

    //Coherence: Find the Source patches most similar to the target patches
    for (int patchy = y1; patchy <= y2; patchy++) {
        for (int patchx = x1; patchx <= x2; patchx++) {
            cv::Vec<float, 4> sourcePatchVec = coherencePatchMatches.at(t).at<cv::Vec<float, 4>>(patchx, patchy);
            //return value of the target pixel within the source patch
            std::vector<int> targetPixel;
            //Find target pixel in source patch
            int targetPixelX = static_cast<int>((x - patchx) + sourcePatchVec[0]);
            int targetPixelY = static_cast<int>((y - patchy) + sourcePatchVec[1]);
            for (int c = 0; c < sourceChannels; c++) {
                targetPixel.push_back(
                        static_cast<int &&>(source.at(
                                static_cast<unsigned int>(sourcePatchVec[2])).at<cv::Vec<float, 4>>(
                                targetPixelX, targetPixelY)[c]));
            }
            sourcePatches[0].push_back(targetPixel);
        }
    }

    return sourcePatches;
}

int TextureMapper::Tixi(int &x, int &y, int &t, std::vector<std::vector<int>> &completenessPatches, std::vector<std::vector<int>> &coherencePatches, int c /*color channel*/) {
    //su and sv are the source patches overlapping with pixel xi of the target for the completeness and coherence terms, respectively.
    //yu and yv refer to a single pixel in su and sv , respectively, corresponding to the Xith pixel of the target image.
    //U and V refer to the number of patches for the completeness and coherence terms, respectively.
    //wj = (cos(θ)**2) / (d**2), where θ is the angle between the surface
    //normal and the viewing direction at image j and d denotes the distance between the camera and the surface.
    int U = (int) completenessPatches.size();
    int V = (int) coherencePatches.size();
    int L = 49; //L is the number of pixels in a patch (7 x 7 = 49)
    int alpha = 2;
    double lambda = 0.1;
    int sum1 = 0;
    int N = (int) texture.size(); //N is the number of texture images.
    for (int u = 0; u < U; u++) {
        int upatch = completenessPatches[u][c];
        sum1 += upatch;
    }
    int term1 = (1/L)*sum1;
    int sum2 = 0;
    for (int v; v < V; v++) {
        int vpatch = coherencePatches[v][c];
        sum2 += vpatch;
    }
    int term2 = (alpha / L) * sum2;
    int sum3 = 0;
    std::vector<cv::Point2f> Xi;
    Xi.emplace_back(cv::Point2f(x, y));
    for (unsigned long k = 0; k < N; k++) {
        //Mk(Xi->k) RGB color of the kth texture at pixel Xi->k, i.e., the result of projecting texture k to camera i
        // (Xi->k is pixel position projected from image i to k)
        cv::Mat pose = TcwPoses.at(k);
        cv::Mat rvec;
        cv::Rodrigues(pose(cv::Rect(0, 0, 3, 3)), rvec);
        std::vector<cv::Point2f> Xik;
        cv::projectPoints(Xi, rvec, pose(cv::Rect(3, 0, 1, 3)), cameraMatrix, distCoef, Xik);
        sum3 += texture.at(k).at<int>(Xik.at(0))/*project texture k to image i*/;
    }
    int WiXi = (cos(theta)^2) / (depthMaps.at((unsigned long) t).at<int>(Xi.at(0))^2);
    int term3 = (int) (lambda / N) * WiXi * sum3;
    int denominator = (int) ((U / L) + ((alpha * V) / L) + (lambda * WiXi));
    return ((term1 + term2 + term3) / denominator);
}

void TextureMapper::reconstruct() {
    for (int t = 0; t < texture.size(); t++) {
        for (int y = 0; y < texture.at(0).size().height; y++) {
            for (int x = 0; x < texture.at(0).size().width; x++) {
                texture.at(t).at<int>(x,y) = Mixi(x, y, t);
            }
        }
    }
}

int TextureMapper::Mixi(int &x, int &y, int &t) {
    int N = (int) texture.size();
    int numerator = 0;
    int denominator = 0;
    for (int j = 0; j < N; j++) {
        //Tj(Xi->j) is the result of projecting target j to camera i
        cv::Mat pose = TcwPoses.at((unsigned long) t);
        cv::Mat rvec;
        cv::Rodrigues(pose(cv::Rect(0, 0, 3, 3)), rvec);
        std::vector<cv::Point2f> Xij;
        std::vector<cv::Point2f> Xi;
        Xi.emplace_back(cv::Point2f(x, y));
        cv::projectPoints(Xi, rvec, pose(cv::Rect(3, 0, 1, 3)), cameraMatrix, distCoef, Xij);
        int WjXij = (cos(theta)^2) / (depthMaps.at((unsigned long) j).at<int>(Xij.at(0))^2);
        numerator += WjXij * target.at((unsigned long) j).at<int>(Xij.at(0));
        denominator += WjXij;
    }
    return numerator / denominator;
}

int TextureMapper::randomInt(int min, int max) {
    return min + (rand() % static_cast<int>(max - min + 1));
}

float TextureMapper::edgeFunction(const cv::Vec3f &a, const cv::Vec3f &b, const cv::Vec3f &c)
{ return (c[0] - a[0]) * (b[1] - a[1]) - (c[1] - a[1]) * (b[0] - a[0]); }

void convertToRaster(
        const cv::Vec3f &vertexWorld,
        const cv::Mat &worldToCamera,
        const cv::Rect &rect,
        const float &near,
        const uint32_t &imageWidth,
        const uint32_t &imageHeight,
        cv::Vec3f &vertexRaster
)
{
    cv::Vec3f vertexCamera;

    worldToCamera.multVecMatrix(vertexWorld, vertexCamera);

    // convert to screen space
    cv::Vec2f vertexScreen;
    vertexScreen[0] = near * vertexCamera[0] / -vertexCamera[2];
    vertexScreen[1] = near * vertexCamera[1] / -vertexCamera[2];

    // now convert point from screen space to NDC space (in range [-1,1])
    cv::Vec2f vertexNDC;
    vertexNDC[0] = 2 * vertexScreen[0] / (rect.width - rect.x) - (rect.x + rect.width + rect.x) / (rect.width);
    vertexNDC[1] = 2 * vertexScreen[1] / (rect.height) - (rect.y + ) / (rect.height);

    // convert to raster space
    vertexRaster[0] = (vertexNDC[0] + 1) / 2 * imageWidth;
    // in raster space y is down so invert direction
    vertexRaster[1] = (1 - vertexNDC[1]) / 2 * imageHeight;
    vertexRaster[2] = -vertexCamera[2];
}

std::vector<cv::Mat> TextureMapper::getRGBD() {
    //Get depth for all of the pixels. This will either require rasterization or ray-tracing (I need to do more research to determine which one).
    cv::Mat sourceImage;
    cv::Mat depth = cv::Mat(sourceWidth, sourceHeight, CV_64F, cvScalar(0.));
    for (unsigned int cam = 0; cam < TcwPoses.size(); cam++) {
        sourceImage = source.at(cam);
        cv::Rect rect(cv::Point(), sourceImgSize);
        cv::Mat pose = TcwPoses.at(cam);
        cv::Mat rvec;
        cv::Rodrigues(pose(cv::Rect(0, 0, 3, 3)), rvec);
        std::vector<cv::Point2f> imagePoints;
        cv::projectPoints(vertices, rvec, pose(cv::Rect(3, 0, 1, 3)), cameraMatrix, distCoef,
                          imagePoints);
        // rasterization algorithm
        // define the frame-buffer and the depth-buffer. Initialize depth buffer
        // to far clipping plane.
        float *depthBuffer = new float[sourceWidth * sourceHeight];
        for (uint32_t i = 0; i < sourceWidth * sourceHeight; ++i) depthBuffer[i] = farClippingPLane;

        auto t_start = std::chrono::high_resolution_clock::now();


        for (uint32_t i = 0; i < ntris; i++) {
            const cv::Vec3f &v0 = vertices[nvertices[i * 3]];
            const cv::Vec3f &v1 = vertices[nvertices[i * 3 + 1]];
            const cv::Vec3f &v2 = vertices/*array of vec3fs*/[nvertices/*array of ints*/[i * 3 + 2]];

            cv::Vec3f v0Raster, v1Raster, v2Raster;

            convertToRaster(v0, cameraMatrix, l, r, t, b, nearClippingPLane, sourceWidth,
                            sourceHeight, v0Raster);
            convertToRaster(v1, cameraMatrix, l, r, t, b, nearClippingPLane, sourceWidth,
                            sourceHeight, v1Raster);
            convertToRaster(v2, cameraMatrix, l, r, t, b, nearClippingPLane, sourceWidth,
                            sourceHeight, v2Raster);

            v0Raster[2] = 1 / v0Raster[2],
            v1Raster[2] = 1 / v1Raster[2],
            v2Raster[2] = 1 / v2Raster[2];

            cv::Vec2f st0 = st[stindices[i * 3]];
            cv::Vec2f st1 = st[stindices[i * 3 + 1]];
            cv::Vec2f st2 = st[stindices[i * 3 + 2]];

            st0 *= v0Raster[2], st1 *= v1Raster[2], st2 *= v2Raster[2];

            float xmin = min3(v0Raster[0], v1Raster[0], v2Raster[0]);
            float ymin = min3(v0Raster[1], v1Raster[1], v2Raster[1]);
            float xmax = max3(v0Raster[0], v1Raster[0], v2Raster[0]);
            float ymax = max3(v0Raster[1], v1Raster[1], v2Raster[1]);

            // the triangle is out of screen
            if (xmin > sourceWidth - 1 || xmax < 0 || ymin > sourceHeight - 1 || ymax < 0) continue;

            // be careful xmin/xmax/ymin/ymax can be negative. Don't cast to uint32_t
            uint32_t x0 = std::max(int32_t(0), (int32_t) (std::floor(xmin)));
            uint32_t x1 = std::min(int32_t(sourceWidth) - 1, (int32_t) (std::floor(xmax)));
            uint32_t y0 = std::max(int32_t(0), (int32_t) (std::floor(ymin)));
            uint32_t y1 = std::min(int32_t(sourceHeight) - 1, (int32_t) (std::floor(ymax)));

            float area = edgeFunction(v0Raster, v1Raster, v2Raster);

            for (uint32_t y = y0; y <= y1; ++y) {
                for (uint32_t x = x0; x <= x1; ++x) {
                    cv::Vec3f pixelSample(x + 0.5, y + 0.5, 0);
                    float w0 = edgeFunction(v1Raster, v2Raster, pixelSample);
                    float w1 = edgeFunction(v2Raster, v0Raster, pixelSample);
                    float w2 = edgeFunction(v0Raster, v1Raster, pixelSample);
                    if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                        w0 /= area;
                        w1 /= area;
                        w2 /= area;
                        float oneOverZ = v0Raster[2] * w0 + v1Raster[2] * w1 + v2Raster[2] * w2;
                        float z = 1 / oneOverZ;
                        if (z < depthBuffer[y * sourceWidth + x]) {
                            depthBuffer[y * sourceWidth + x] = z;
                        }
                    }
                }
            }
        }
        auto t_end = std::chrono::high_resolution_clock::now();
        auto passedTime = std::chrono::duration<double, std::milli>(t_end - t_start).count();
        std::cerr << "Wall passed time:  " << passedTime << " ms" << std::endl;
        std::ofstream ofs;
        ofs.open("./output.ppm");
        ofs << "P6\n" << sourceWidth << " " << sourceHeight << "\n255\n";
        ofs.close();
        depthMap = depthBuffer;
        delete [] depthBuffer;
    }
}

void TextureMapper::projectToSurface() {
    // accumulation buffers for colors and weights
    int buff_ind;
    cv::Mat sourceImage;
    double pweight = 1.0;
    double *weights;
    double *acc_red;
    double *acc_grn;
    double *acc_blu;

    // init accumulation buffers for colors and weights
    weights = new double[vertices.size()];
    acc_red = new double[vertices.size()];
    acc_grn = new double[vertices.size()];
    acc_blu = new double[vertices.size()];
    for (int buff_ind = 0; buff_ind < vertices.size(); buff_ind++) {
        weights[buff_ind] = 0.0;
        acc_red[buff_ind] = 0.0;
        acc_grn[buff_ind] = 0.0;
        acc_blu[buff_ind] = 0.0;
    }

    //for each camera
    for (unsigned int cam = 0; cam < TcwPoses.size(); cam++) {
        buff_ind = 0;
        sourceImage = source.at(cam);
        cv::Rect rect(cv::Point(), sourceImgSize);
        cv::Mat pose = TcwPoses.at(cam);
        cv::Mat rvec;
        cv::Rodrigues(pose(cv::Rect(0, 0, 3, 3)), rvec);
        std::vector<cv::Point2f> imagePoints;
        cv::projectPoints(vertices, rvec, pose(cv::Rect(3, 0, 1, 3)), cameraMatrix, distCoef,
                          imagePoints); //sigsegv here
        for (auto &imgPoint : imagePoints) {
            if (rect.contains(imgPoint)) {
                cv::Vec3b pcolor = sourceImage.at<cv::Vec3b>(imgPoint);
                //add color buffers
                weights[buff_ind] += pweight;
                acc_red[buff_ind] += (pcolor[0] * pweight / 255.0);
                acc_grn[buff_ind] += (pcolor[1] * pweight / 255.0);
                acc_blu[buff_ind] += (pcolor[2] * pweight / 255.0);
            }
            buff_ind++;
        } //end for each vertex
    } //end for each camera
    write_ply_file(weights, acc_red, acc_grn, acc_blu);
    delete[]weights;
    delete[]acc_red;
    delete[]acc_grn;
    delete[]acc_blu;
}

void TextureMapper::read_ply_file() {
    try {
        std::ifstream ss(plyFilename, std::ios::binary);
        if (ss.fail()) throw std::runtime_error("failed to open " + plyFilename);

        PlyFile file;
        file.parse_header(ss);

        std::cout << "........................................................................\n";
        for (auto c : file.get_comments()) std::cout << "Comment: " << c << std::endl;
        for (auto e : file.get_elements()) {
            std::cout << "element - " << e.name << " (" << e.size << ")" << std::endl;
            for (auto p : e.properties)
                std::cout << "\tproperty - " << p.name << " ("
                          << tinyply::PropertyTable[p.propertyType].str << ")" << std::endl;
        }
        std::cout << "........................................................................\n";

        // Tinyply treats parsed data as untyped byte buffers. See below for examples.
        std::shared_ptr<PlyData> vertices, normals, faces, texcoords;

        // The header information can be used to programmatically extract properties on elements
        // known to exist in the header prior to reading the data. For brevity of this sample, properties
        // like vertex position are hard-coded:
        try { vertices = file.request_properties_from_element("vertex", {"x", "y", "z"}); }
        catch (const std::exception &e) {
            std::cerr << "tinyply exception: " << e.what() << std::endl;
        }

        try { normals = file.request_properties_from_element("vertex", {"nx", "ny", "nz"}); }
        catch (const std::exception &e) {
            std::cerr << "tinyply exception: " << e.what() << std::endl;
        }

        try { texcoords = file.request_properties_from_element("vertex", {"u", "v"}); }
        catch (const std::exception &e) {
            std::cerr << "tinyply exception: " << e.what() << std::endl;
        }

        // Providing a list size hint (the last argument) is a 2x performance improvement. If you have
        // arbitrary ply files, it is best to leave this 0.
        try { faces = file.request_properties_from_element("face", {"vertex_indices"}, 3); }
        catch (const std::exception &e) {
            std::cerr << "tinyply exception: " << e.what() << std::endl;
        }

        file.read(ss);

        const size_t numVerticesBytes = vertices->buffer.size_bytes();
        std::vector<cv::Point3f> verts(vertices->count);
        std::memcpy(verts.data(), vertices->buffer.get(), numVerticesBytes);
        TextureMapper::vertices = verts;
        TextureMapper::ntris = faces->count;
    }
    catch (const std::exception &e) {
        std::cerr << "Caught tinyply exception: " << e.what() << std::endl;
    }
}

void TextureMapper::write_ply_file(double *weights, double *acc_red, double *acc_grn, double *acc_blu) {
    std::ifstream in(plyFilename, std::ios_base::binary);
    std::ofstream out(tempFilename, std::ios_base::binary);
    std::string line;
    while (std::getline(in, line) && line != "property float z") {
        out << line << std::endl;
    }
    out << line << std::endl; //Copy vertex z property line
    out << "property uchar red\nproperty uchar green\nproperty uchar blue\n";
    while (std::getline(in, line) && line != "end_header") {
        out << line << std::endl;
    }
    out << line << std::endl; //Copy end_header line
    // Paint model vertices with colors
    for (int buff_ind = 0; buff_ind < vertices.size(); buff_ind++) {
        //float is 4 bytes - need to copy 12 bytes for x, y, z
        char *s = new char[12];
        in.read(s, 12);
        out.write(s, 12);
        delete[]s;
        if (weights[buff_ind] != 0) // if 0, it has not found any valid projection on any camera
        {
            uchar r = (acc_red[buff_ind] / weights[buff_ind]) * 255.0;
            uchar g = (acc_grn[buff_ind] / weights[buff_ind]) * 255.0;
            uchar b = (acc_blu[buff_ind] / weights[buff_ind]) * 255.0;
            out.write(reinterpret_cast<char *>(&r), 1);
            out.write(reinterpret_cast<char *>(&g), 1);
            out.write(reinterpret_cast<char *>(&b), 1);
        } else {
            uchar r = 0;
            uchar g = 0;
            uchar b = 0;
            out.write(reinterpret_cast<char *>(&r), 1);
            out.write(reinterpret_cast<char *>(&g), 1);
            out.write(reinterpret_cast<char *>(&b), 1);
        }
    }
    //Copy the rest
    while (std::getline(in, line)) {
        out << line << std::endl;
    }
    in.close();
    out.close();
    int result = std::remove(plyFilename.c_str());
    int renameResult = std::rename(tempFilename.c_str(), plyFilename.c_str());
}

float TextureMapper::min3(const float &a, const float &b, const float &c)
{ return std::min(a, std::min(b, c)); }

float TextureMapper::max3(const float &a, const float &b, const float &c)
{ return std::max(a, std::max(b, c)); }
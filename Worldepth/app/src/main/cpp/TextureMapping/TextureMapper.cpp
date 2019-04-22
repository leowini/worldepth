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
    //align();
    //reconstruct();
    projectToSurface();
}

void TextureMapper::align() {
    int iterations = 1;
    std::vector<cv::Mat> completenessPatchMatches = patchSearch(iterations);
    std::vector<cv::Mat> coherencePatchMatches = patchSearch(iterations);
    //vote(completenessPatchMatches, coherencePatchMatches);
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

//void TextureMapper::vote(cv::Mat &completenessPatchMatches, cv::Mat &coherencePatchMatches) {
//    //For each pixel in the target
//    for (int t = 0; t < target.size(); t++) {
//        for (int y = 0; y < targetHeight; y++) {
//            for (int x = 0; x < targetWidth; x++) {
//                std::vector<std::vector<std::vector<int>>> patches = findSourcePatches(completenessPatchMatches, coherencePatchMatches, x, y, t);
//                std::vector<std::vector<int>> completenessPatches = patches[0];
//                std::vector<std::vector<int>> coherencePatches = patches[1];
//
//                for (int c = 0; c < sourceChannels; c++) {
//                    Tixi(completenessPatches, coherencePatches, c);
//                }
//
//            }
//        }
//    }
//}

std::vector<std::vector<std::vector<int>>>
TextureMapper::findSourcePatches(cv::Mat &completenessPatchMatches, cv::Mat &coherencePatchMatches,
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
                cv::Vec<float, 4> patchMatch = completenessPatchMatches.at<cv::Vec<float, 4>>(st,
                                                                                              sy,
                                                                                              st);
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
            cv::Vec<float, 4> sourcePatchVec = coherencePatchMatches.at<cv::Vec<float, 4>>(patchx,
                                                                                           patchy,
                                                                                           t);
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

//int TextureMapper::Tixi(std::vector<std::vector<int>> &completenessPatches, std::vector<std::vector<int>> &coherencePatches, int c /*color channel*/) {
//    //su and sv are the source patches overlapping with pixel xi of the target for the completeness and coherence terms, respectively.
//    //yu and yv refer to a single pixel in su and sv , respectively, corresponding to the Xith pixel of the target image.
//    //U and V refer to the number of patches for the completeness and coherence terms, respectively.
//    //wj = (cos(θ)**2) / (d**2), where θ is the angle between the surface
//    //normal and the viewing direction at image j and d denotes the distance between the camera and the surface.
//    int U = completenessPatches.size();
//    int V = coherencePatches.size();
//    int L = 49; //L is the number of pixels in a patch (7 x 7 = 49)
//    int alpha = 2;
//    double lambda = 0.1;
//    int sum1 = 0;
//    int N = texture.size(); //N is the number of texture images.
//    for (int u = 0; u < U; u++) {
//        int upatch = completenessPatches[u][c];
//        sum1 += upatch;
//    }
//    int term1 = (1/L)*sum1;
//    int sum2 = 0;
//    for (int v; v < V; v++) {
//        int vpatch = coherencePatches[v][c];
//        sum2 += vpatch;
//    }
//    int term2 = (alpha / L) * sum2;
//    int sum3 = 0;
//    for (int k = 0; k < N; k++) {
//        //Mk(Xi->k) RGB color of the kth texture at pixel Xi->k, i.e., the result of projecting texture k to camera i
//        // (Xi->k is pixel position projected from image i to k)
//        sum3 += Mk(Xi->k);
//    }
//    int term3 = (lambda / N) * wi(xi) * sum3;
//    int denominator = (U / L) + ((alpha * V) / L) + (lambda * wi(xi));
//    return ((term1 + term2 + term3) / denominator);
//}

//void TextureMapper::reconstruct() {
//    for (int t = 0; t < texture.size(); t++) {
//        for (int y = 0; y < texture.at(0).size().height; y++) {
//            for (int x = 0; x < texture.at(0).size().width; x++) {
//                texture.at(t).ptr(x,y) = Mixi();
//            }
//        }
//    }
//}
//
//int TextureMapper::Mixi() {
//    int N = texture.size();
//    int numerator = 0;
//    for (int j = 0; j < N; j++) {
//        //Tj(Xi->j) is the result of projecting target j to camera i
//        numerator += wj(Xi->j) * Tj(Xi->j);
//    }
//    int denominator = 0;
//    for (int j = 0; j < N; j++) {
//        denominator += wj(Xi->j);
//    }
//    return numerator / denominator;
//}

int TextureMapper::randomInt(int min, int max) {
    return min + (rand() % static_cast<int>(max - min + 1));
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
        for (auto &triangle : triangles) {
            // STEP 1: project vertices of the triangle using perspective projection
            cv::Vec2f v0 = perspectiveProject(triangle[i].v0);
            cv::Vec2f v1 = perspectiveProject(triangle[i].v1);
            cv::Vec2f v2 = perspectiveProject(triangle[i].v2);
            for (each pixel in image) {
                // STEP 2: is this pixel contained in the projected image of the triangle?
                if (pixelContainedIn2DTriangle(v0, v1, v2, x, y)) {
                    image(x,y) = triangle[i].color;
                    float oneOverZ = v0Raster.z * w0 + v1Raster.z * w1 + v2Raster.z * w2;
                    float z = 1 / oneOverZ;
                }
            }
        }
    }
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
    }
    catch (const std::exception &e) {
        std::cerr << "Caught tinyply exception: " << e.what() << std::endl;
    }
}

void
TextureMapper::write_ply_file(double *weights, double *acc_red, double *acc_grn, double *acc_blu) {
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
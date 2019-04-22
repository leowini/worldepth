#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <PoissonRecon.h>
#include "Reconstructor.h"
#include "camera_calibration.h"

using namespace std;

Reconstructor *reconstructor = nullptr;
calib::Settings *sptr = nullptr;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_leodw_worldepth_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_PoissonWrapper_startPoisson(JNIEnv *env, jobject instance, jstring internalPath) {
    //Poisson is crashing with the internalPath.
    const char *_internalPath = env->GetStringUTFChars(internalPath, 0);
    std::string intPath = std::string(_internalPath);
    char* args [] = {
            (char*)"PoissonRecon",
            (char*)"--in",
            (char*) (intPath + "/Pointcloud.txt").c_str(),
            (char*)"--out",
            (char*) (intPath + "/SLAM.ply").c_str(),
            (char*)"--depth",
            (char*)"10",
            (char*)"--tempDir",
            (char*) intPath.c_str()
    };
    int numArgs = 9;
    runMain(numArgs, args);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_leodw_worldepth_slam_Slam_passImageToSlam(JNIEnv *env, jobject instance, jlong img, jlong timeStamp) {
    if (img == 0) { //poison pill
        if(reconstructor != nullptr) {
            bool success = reconstructor->hasKeyframes();
            reconstructor->endSlam(success);
            return static_cast<jboolean>(success);
        }
        else return static_cast<jboolean>(false);
    } else {
        cv::Mat &mat = *(cv::Mat *) img;
        double tframe = (double) timeStamp;
        reconstructor->passImageToSlam(mat, tframe);
        mat.release();
    }
    return static_cast<jboolean>(true);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_Slam_initSystem(JNIEnv *env, jobject instance, jstring vocFile, jstring settingsFile, jstring internalPath) {
    const char *_vocFile = env->GetStringUTFChars(vocFile,0);
    const char *_settingsFile = env->GetStringUTFChars(settingsFile,0);
    const char *_internalPath = env->GetStringUTFChars(internalPath, 0);
    std::string strPath = std::string(_internalPath);
    std::string vocFileString = _vocFile;
    std::string settingsFileString = _settingsFile;
    reconstructor = new Reconstructor(vocFileString, settingsFileString, strPath);
    env->ReleaseStringUTFChars(vocFile, _vocFile);
    env->ReleaseStringUTFChars(settingsFile, _settingsFile);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_TextureMapWrapper_textureMap(JNIEnv *env, jobject instance) {
    reconstructor->textureMap();
    //delete reconstructor;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_CalibWrapper_initSettings(JNIEnv *env, jobject instance, jstring internalPath) {
    const char *_internalPath = env->GetStringUTFChars(internalPath, 0);
    std::string internalPathStr= std::string(_internalPath);
    sptr = new calib::Settings(/*internalPathStr*/);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_leodw_worldepth_slam_CalibWrapper_passImageToCalibrate(JNIEnv *env, jobject instance, jlong img) {
    cv::Mat &mat = *(cv::Mat *) img;
    //returns whether or not it finished
    if (sptr != nullptr) {
        sptr->processImage(mat);
        mat.release();
        bool done = sptr->mode == calib::CALIBRATED;
        if (done) {  //if calibration is finished and written
            delete sptr;
            sptr = nullptr;
        }
        return done;
    }
    return false;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_Slam_endReconstruction(JNIEnv *env, jobject instance) {
    delete reconstructor;
    reconstructor = nullptr;
}
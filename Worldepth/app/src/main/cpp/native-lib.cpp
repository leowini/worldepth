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
Java_com_example_leodw_worldepth_slam_PoissonWrapper_startPoisson(JNIEnv *env, jobject instance) {
    char* args [] = {
            (char*)"PoissonRecon",
            (char*)"--in",
//            (char*)"/storage/emulated/0/Worldepth/Pointcloud.txt",
            (char*) "/data/user/0/com.example.leodw.worldepth/files/SLAM.txt",
//            (char*) "/data/user/0/com.example.leodw.worldepth/files/Pointcloud.txt",
            (char*)"--out",
//            (char*)"/storage/emulated/0/Worldepth/SLAM.ply",
            (char*) "/data/user/0/com.example.leodw.worldepth/files/SLAM.ply",
            (char*)"--depth",
            (char*)"10",
            (char*)"--tempDir",
//            (char*)"/storage/emulated/0/Worldepth"
            (char*)"/data/user/0/com.example.leodw.worldepth/files"
    };
    int numArgs = 9;
    runMain(numArgs, args);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_example_leodw_worldepth_slam_Slam_passImageToSlam(JNIEnv *env, jobject instance, jlong img, jlong timeStamp) {
    if (img == 0) { //poison pill
        bool success = reconstructor->hasKeyframes();
        reconstructor->endSlam("/data/user/0/com.example.leodw.worldepth/files/Pointcloud.txt", success);
        return static_cast<jboolean>(success);
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
Java_com_example_leodw_worldepth_slam_Slam_initSystem(JNIEnv *env, jobject instance, jstring vocFile, jstring settingsFile) {
    const char *_vocFile = env->GetStringUTFChars(vocFile,0);
    const char *_settingsFile = env->GetStringUTFChars(settingsFile,0);
    std::string vocFileString = _vocFile;
    std::string settingsFileString = _settingsFile;
    reconstructor = new Reconstructor(vocFileString, settingsFileString);
    env->ReleaseStringUTFChars(vocFile, _vocFile);
    env->ReleaseStringUTFChars(settingsFile, _settingsFile);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_TextureMapWrapper_textureMap(JNIEnv *env, jobject instance) {
    reconstructor->textureMap();
    delete reconstructor;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_CalibWrapper_initSettings(JNIEnv *env, jobject instance) {
    sptr = new calib::Settings();
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
}
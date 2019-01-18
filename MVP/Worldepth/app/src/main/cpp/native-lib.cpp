#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_leodw_worldepth_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_Slam_passImageToSlam(JNIEnv *env, jobject instance, jint width, jint height, jlong img, jlong timeStamp) {
    cv::Mat &mat = *(cv::Mat *) img;
    double tframe = (double) timeStamp;
    //SLAM.TrackMonocular(mimg, tframe);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_PoissonWrapper_passPointCloudToPoisson(JNIEnv *env, jobject instance, jint x) {

}
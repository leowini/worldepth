#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <PoissonRecon.cpp>

using namespace std;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_leodw_worldepth_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_leodw_worldepth_slam_PoissonWrapper_passPointCloudToPoisson(JNIEnv *env, jobject instance) {
    char* args [] = {
            (char*)"PoissonRecon",
            (char*)"--in data/user/0/com.example.leodw.worldepth/files/SLAM.txt",
            (char*)"--out data/user/0/com.example.leodw.worldepth/files/SLAM.ply",
            (char*)"--depth 10"
    };
    int numArgs = 4;
    runMain(numArgs, args);


}
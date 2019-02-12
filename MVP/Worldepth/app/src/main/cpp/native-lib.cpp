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
            (char*)"--in",
            (char*)"/storage/emulated/0/Worldepth/Pointcloud.txt",
            (char*)"--out",
            (char*)"/storage/emulated/0/Worldepth/SLAM.ply",
            (char*)"--depth",
            (char*)"10",
            (char*)"--tempDir",
            (char*)"/storage/emulated/0/Worldepth"
    };
    int numArgs = 9;
    runMain(numArgs, args);
}
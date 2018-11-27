#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_leodw_worldepth_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_leodw_worldepth_slam_Slam_passImage(JNIEnv *env, jobject instance, jobject img) {

    // TODO
    jclass cls = env->GetObjectClass(img);
    return env->NewStringUTF("Got the image!");
}
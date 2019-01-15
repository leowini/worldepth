//
// Created by Soren Dahl on 12/17/18.
//

#include <RunSlam.h>
#include <TrackingInit.h>
#include <RandomMap.h>
#include <System.h>
#include <jni.h>
#include <string>

namespace SLAM
{

    void start (std::string & vocFile, std::string & settingsFile) {
        slam = new System(vocFile, settingsFile);
        //return slam;
    }

    //I still don't know how to do the initializer, if it's not automatically Idk how this is done
    void process(cv::Mat &im, double &tstamp) {
        //call the equivalent of System::TrackMonocular for TrackingInit or Tracking directly
        if (im.empty() || tstamp == 0){
            cerr << "could not load image!" << endl;
        } else if (im.rows == 1 && im.cols == 1) {  //poison pill
            //this makes and writes to an internal storage file, reused every time slam is run
            //we may change this to instead pass it to Poisson
            end("data/user/0/com.example.leodw.worldepth/files/SLAM.txt");
        } else {
            slam->TrackMonocular(im, tstamp);
        }

    }


    void end (std::string filename) {

        //get finished map as reference
        writeMap(filename, slam->GetTrackedMapPoints());
        

        slam->Shutdown();
        //System actually has a clear func, it's
        slam->Reset();

        //close any other threads (should be done already in System.Reset()
        delete slam;
    }

    extern "C"
    JNIEXPORT jstring JNICALL
    Java_com_example_leodw_worldepth_slam_Slam_passImageToSlam(JNIEnv *env, jobject instance, jint width, jint height, jbyteArray img, jlong timeStamp) {
        jbyte* _img  = env->GetByteArrayElements(img, 0);
        cv::Mat mimg(width, height, CV_8UC1, (unsigned char *)_img);
        double tframe = (double) timeStamp;
        process(mimg, tframe);
        env->ReleaseByteArrayElements(img, _img, 0);
        std::string hello = "Hello from C++";
        return env->NewStringUTF(hello.c_str());
    }

    extern "C"
    JNIEXPORT void JNICALL
    Java_com_example_leodw_worldepth_slam_Slam_initSystem(JNIEnv *env, jobject instance, jstring vocFile, jstring settingsFile) {
        const char *_vocFile = env->GetStringUTFChars(vocFile,0);
        const char *_settingsFile = env->GetStringUTFChars(settingsFile,0);
        std::string vocFileString = _vocFile;
        std::string settingsFileString = _settingsFile;
        start(vocFileString, settingsFileString);
        env->ReleaseStringUTFChars(vocFile, _vocFile);
        env->ReleaseStringUTFChars(settingsFile, _settingsFile);
    }


}



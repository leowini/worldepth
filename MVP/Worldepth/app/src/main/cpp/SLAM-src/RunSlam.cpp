//
// Created by Soren Dahl on 12/17/18.
//

#include <RunSlam.h>

namespace SLAM
{

    void start (std::string & vocFile, std::string & settingsFile) {
        //now with binary
        slam = new System(vocFile, settingsFile);
    }

    //I still don't know how to do the initializer, if it's not automatically Idk how this is done
    void process(cv::Mat &im, double &tstamp) {
        //call the equivalent of System::TrackMonocular for TrackingInit or Tracking directly
        if (im.empty() || tstamp == 0){
            cerr << "could not load image!" << endl;
        } else {
            slam->TrackMonocular(im, tstamp);
        }

    }


    void end (std::string filename) {

        //get finished map as reference
        writeMap(filename, slam->GetAllMapPoints());
        

        slam->Shutdown();
        //System actually has a clear func, it's
        slam->Reset();

        //close any other threads (should be done already in System.Reset()
        delete slam;
    }

    extern "C"
    JNIEXPORT void JNICALL
    Java_com_example_leodw_worldepth_slam_Slam_passImageToSlam(JNIEnv *env, jobject instance, jlong img, jlong timeStamp) {
        if (img == 0) { //poison pill
            end(/*"/storage/emulated/0/Worldepth/SLAM.npts"*/"/data/user/0/com.example.leodw.worldepth/files/SLAM.txt");
        } else {
            cv::Mat &mat = *(cv::Mat *) img;
            double tframe = (double) timeStamp;
            process(mat, tframe);
            mat.release();
        }
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



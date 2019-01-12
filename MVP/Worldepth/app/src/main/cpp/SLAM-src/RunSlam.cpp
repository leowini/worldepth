//
// Created by Soren Dahl on 12/17/18.
//

#include <RunSlam.h>
#include <TrackingInit.h>
#include <RandomMap.h>
#include <System.h>

namespace SLAM
{

    void start (std::string & vocFile, std::string & settingsFile) {
        slam = new System(vocFile, settingsFile);
        //return slam;
    }

    //I still don't know how to do the initializer, if it's not automatically Idk how this is done
    void process(cv::Mat im, double tstamp) {
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
}



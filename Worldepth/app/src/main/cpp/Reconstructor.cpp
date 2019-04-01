#include <RandomMap.h>
#include "Reconstructor.h"

Reconstructor::Reconstructor(const std::string & vocFile, const std::string & settingsFile) {
    slam = new System(vocFile, settingsFile);
    vKFImColor = std::vector<cv::Mat>();
    vKFTcw = std::vector<cv::Mat>();
}

bool Reconstructor::hasKeyframes() {
    return (!vKFImColor.empty());
}

void Reconstructor::passImageToSlam(cv::Mat &im, double tstamp) {
    //call the equivalent of System::TrackMonocular for TrackingInit or Tracking directly
    if (im.empty() || tstamp == 0){
        cerr << "could not load image!" << endl;
    } else {
        cv::Mat Tcw = slam->TrackMonocular(im, tstamp);
        if(!Tcw.empty()){
            vKFImColor.push_back(im.clone());
            vKFTcw.push_back(Tcw.clone());
        }
    }
}

void Reconstructor::endSlam(const std::string &filename, const bool slamSuccess) {
    //get finished map as reference
    writeMap(filename, slam->GetAllMapPoints());
    slam->Shutdown();
    //System actually has a clear func, it's
    slam->Reset();
    //close any other threads (should be done already in System.Reset()
    delete slam;
    if (slamSuccess) vecmatwrite("/data/user/0/com.example.leodw.worldepth/files/vKFImColor.bin", vKFImColor);
    if (slamSuccess) vecmatwrite("/data/user/0/com.example.leodw.worldepth/files/vKFTcw.bin", vKFTcw);
}

void Reconstructor::textureMap() {
    vKFImColor = vecmatread("/data/user/0/com.example.leodw.worldepth/files/vKFImColor.bin");
    vKFTcw = vecmatread("/data/user/0/com.example.leodw.worldepth/files/vKFTcw.bin");
    textureMapper = new TextureMapper("/data/user/0/com.example.leodw.worldepth/files/Horse.ply", vKFImColor, vKFTcw);
    textureMapper->textureMap();
    delete textureMapper;
    vKFImColor.clear();
    vKFTcw.clear();
}

void Reconstructor::vecmatwrite(const string& filename, const vector<cv::Mat>& matrices)
{
    ofstream fs(filename, fstream::binary);

    for (size_t i = 0; i < matrices.size(); ++i)
    {
        const cv::Mat& mat = matrices[i];

        // Header
        int type = mat.type();
        int channels = mat.channels();
        fs.write((char*)&mat.rows, sizeof(int));    // rows
        fs.write((char*)&mat.cols, sizeof(int));    // cols
        fs.write((char*)&type, sizeof(int));        // type
        fs.write((char*)&channels, sizeof(int));    // channels

        // Data
        if (mat.isContinuous())
        {
            fs.write(mat.ptr<char>(0), (mat.dataend - mat.datastart));
        }
        else
        {
            int rowsz = CV_ELEM_SIZE(type) * mat.cols;
            for (int r = 0; r < mat.rows; ++r)
            {
                fs.write(mat.ptr<char>(r), rowsz);
            }
        }
    }
}

vector<cv::Mat> Reconstructor::vecmatread(const string& filename)
{
    vector<cv::Mat> matrices;
    ifstream fs(filename, fstream::binary);

    // Get length of file
    fs.seekg(0, fs.end);
    int length = fs.tellg();
    fs.seekg(0, fs.beg);

    while (fs.tellg() < length)
    {
        // Header
        int rows, cols, type, channels;
        fs.read((char*)&rows, sizeof(int));         // rows
        fs.read((char*)&cols, sizeof(int));         // cols
        fs.read((char*)&type, sizeof(int));         // type
        fs.read((char*)&channels, sizeof(int));     // channels

        // Data
        cv::Mat mat(rows, cols, type);
        fs.read((char*)mat.data, CV_ELEM_SIZE(type) * rows * cols);

        matrices.push_back(mat);
    }
    return matrices;
}
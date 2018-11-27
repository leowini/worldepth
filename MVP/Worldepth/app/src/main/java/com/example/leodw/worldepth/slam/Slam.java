package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.leodw.worldepth.ui.camera.Renderer;

public class Slam implements Renderer.FrameListener {
    public static final String TAG = "Slam";

    public native String passImage(Mat img);

    @Override
    public void sendFrameToSlam(Bitmap frame) {
        //Convert Bitmap to Mat
        Mat inputImage = new Mat();
        Utils.bitmapToMat(frame, inputImage);
        String success = passImage(inputImage);
        Log.d(TAG, success);
    }

}
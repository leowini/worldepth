package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.leodw.worldepth.ui.camera.Renderer;

public class Slam implements Renderer.FrameListener {
    public static final String TAG = "Slam";

    public native String passImage(Bitmap img);

    @Override
    public void sendFrameToSlam(Bitmap frame) {
        //JNI call
        String success = passImage(frame);
        Log.d(TAG, success);
    }

}
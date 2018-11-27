package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;

import com.example.leodw.worldepth.ui.camera.Renderer;

public class Slam implements Renderer.FrameListener {

    public native boolean sendSlam(Bitmap img);

    @Override
    public void sendFrameToSlam(Bitmap frame) {
        //JNI call

    }

}
package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;

import com.example.leodw.worldepth.ui.camera.Renderer;

public class Slam implements Renderer.FrameListener {

    @Override
    public void sendFrameToSlam(Bitmap frame) {
        //JNI call
    }

}
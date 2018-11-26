package com.example.leodw.worldepth.slam;

import android.graphics.SurfaceTexture;

import com.example.leodw.worldepth.ui.camera.Renderer;

public class Slam implements Renderer.FrameListener {

    void sendSlam(SurfaceTexture frame) {

    }

    @Override
    public void onFrameAvailable(SurfaceTexture frame) {
        sendSlam(frame);
    }

}

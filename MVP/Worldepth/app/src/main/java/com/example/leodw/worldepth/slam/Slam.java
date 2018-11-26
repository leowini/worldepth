package com.example.leodw.worldepth.slam;

import android.graphics.SurfaceTexture;

public class Slam {
    private FrameListener mFrameListener;

    public Slam() {
        setOnFrameAvailableListener(frame -> {
            SurfaceTexture slamFrame = frame;
            sendSlam(slamFrame);
        });
    }

    void sendSlam(SurfaceTexture frame) {

    }

    void setOnFrameAvailableListener(FrameListener frameListener) {
        this.mFrameListener = frameListener;
    }

    public interface FrameListener {
        void onFrameAvailable(SurfaceTexture frame);
    }

}

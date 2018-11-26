package com.example.leodw.worldepth.slam;

public class Slam {
    private FrameListener mFrameListener;

    void setOnFrameAvailableListener(FrameListener frameListener) {
        this.mFrameListener = frameListener;
    }

    public interface FrameListener {
        void onFrameAvailable();
    }

}

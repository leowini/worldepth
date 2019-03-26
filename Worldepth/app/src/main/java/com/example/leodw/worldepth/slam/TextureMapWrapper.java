package com.example.leodw.worldepth.slam;

public class TextureMapWrapper {

    private static final String TAG = "TextureMapWrapper";

    private OnCompleteListener listener;

    public native void textureMap();

    void runMapping(int mesh) {
        textureMap();
        listener.onComplete(0);
    }

    public interface OnCompleteListener {
        void onComplete(int finalModel);
    }

    void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }
}

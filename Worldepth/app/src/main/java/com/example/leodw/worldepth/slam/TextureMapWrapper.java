package com.example.leodw.worldepth.slam;

public class TextureMapWrapper {

    private static final String TAG = "TextureMapWrapper";

    private OnCompleteListener listener;

    public native void textureMap();

    void map() {
        //textureMap();
        listener.onComplete();
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }
}

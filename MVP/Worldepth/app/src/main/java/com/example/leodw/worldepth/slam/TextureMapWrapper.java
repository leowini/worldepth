package com.example.leodw.worldepth.slam;

public class TextureMapWrapper {

    private static final String TAG = "TextureMapWrapper";

    private OnCompleteListener listener;

    public void runMapping() {

    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }
}

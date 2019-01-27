package com.example.leodw.worldepth.reconstruction;

public class TextureMapWrapper {

    private static final String TAG = "TextureMapWrapper";

    private OnCompleteListener listener;

    void runMapping(int mesh) {
        listener.onComplete(0);
    }

    public interface OnCompleteListener {
        void onComplete(int finalModel);
    }

    void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }
}

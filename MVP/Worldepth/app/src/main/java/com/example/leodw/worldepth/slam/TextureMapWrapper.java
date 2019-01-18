package com.example.leodw.worldepth.slam;

public class TextureMapWrapper {

    private static final String TAG = "TextureMapWrapper";

    private OnCompleteListener listener;

    public void runMapping(int mesh) {
        listener.onComplete(0);
    }

    public interface OnCompleteListener {
        void onComplete(int finalModel);
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }
}

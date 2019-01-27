package com.example.leodw.worldepth.reconstruction;

public class PoissonWrapper {

    private static final String TAG = "PoissonWrapper";

    private OnCompleteListener listener;

    void runPoisson(int pointCloud) {
        listener.onComplete(0);
    }

    void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }

    public interface OnCompleteListener {
        void onComplete(int mesh);
    }

}

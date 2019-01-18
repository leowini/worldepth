package com.example.leodw.worldepth.slam;

public class PoissonWrapper {

    private static final String TAG = "PoissonWrapper";

    private OnCompleteListener listener;

    public void runPoisson(int pointCloud) {
        listener.onComplete(0);
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }

    public interface OnCompleteListener {
        void onComplete(int mesh);
    }
}

package com.example.leodw.worldepth.slam;

public class PoissonWrapper {

    private static final String TAG = "PoissonWrapper";

    private OnCompleteListener listener;


    public native void passPointCloudToPoisson();

    public void runPoisson(int pointCloud) {
        passPointCloudToPoisson();
        listener.onComplete(0);
    }

    void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }

    public interface OnCompleteListener {
        void onComplete(int mesh);
    }

}

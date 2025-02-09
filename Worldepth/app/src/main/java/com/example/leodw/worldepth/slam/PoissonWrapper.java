package com.example.leodw.worldepth.slam;

public class PoissonWrapper {

    private static final String TAG = "PoissonWrapper";

    private OnCompleteListener listener;

    public native void startPoisson(String internalPath);

    void runPoisson(String internalPath) {
        startPoisson(internalPath);
        listener.onComplete();
    }

    void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }

    public interface OnCompleteListener {
        void onComplete();
    }

}

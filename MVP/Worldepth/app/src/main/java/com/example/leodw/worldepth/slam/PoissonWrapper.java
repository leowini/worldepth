package com.example.leodw.worldepth.slam;

public class PoissonWrapper {

    private static final String TAG = "PoissonWrapper";

    private OnCompleteListener listener;

    public void runPoisson() {
        listener.onComplete();
    }

    public void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }

    public interface OnCompleteListener {
        void onComplete();
    }
}

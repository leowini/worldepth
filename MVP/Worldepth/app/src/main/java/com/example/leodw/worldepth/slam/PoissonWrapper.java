package com.example.leodw.worldepth.slam;

public class PoissonWrapper {

    private static final String TAG = "PoissonWrapper";

    public native void passPointCloudToPoisson(int x);

    public void runPoisson(int x){
        passPointCloudToPoisson(x);
    }
}

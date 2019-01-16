package com.example.leodw.worldepth.slam;

public class PoissonWrapper {

    public native void passPointCloudToPoisson(int x);

    public void runPoisson(int x){
        passPointCloudToPoisson(x);
    }
}

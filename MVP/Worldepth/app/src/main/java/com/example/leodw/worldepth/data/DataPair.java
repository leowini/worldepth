package com.example.leodw.worldepth.data;

public class DataPair {
    private String mData;
    private int mLocation;

    public DataPair(String data, int location) {
        mData = data;
        mLocation = location;
    }

    public String getData() { return mData; }
    public int getLocation() { return mLocation; }
}

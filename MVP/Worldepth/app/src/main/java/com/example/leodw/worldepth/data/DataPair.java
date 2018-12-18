package com.example.leodw.worldepth.data;

public class DataPair {
    private String mData;
    private int mLocation;
    private int mSender;

    public DataPair(String data, int location, int sender) {
        mData = data;
        mLocation = location;
        mSender = sender;
    }

    public String getData() { return mData; }
    public int getLocation() { return mLocation; }
    public int getSender() { return mSender; }
}

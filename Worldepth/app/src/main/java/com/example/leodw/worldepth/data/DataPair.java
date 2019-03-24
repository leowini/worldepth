package com.example.leodw.worldepth.data;

public class DataPair {

    private static final String TAG = "DataPair";
    private String mData;
    private String mLocation;
    private String mSender;

    public DataPair(String data, String location, String sender) {
        mData = data;
        mLocation = location;
        mSender = sender;
    }

    public String getData() { return mData; }
    public String getLocation() { return mLocation; }
    public String getSender() { return mSender; }
}

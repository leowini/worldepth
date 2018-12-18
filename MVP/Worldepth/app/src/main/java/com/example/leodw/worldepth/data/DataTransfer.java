package com.example.leodw.worldepth.data;

import java.util.*;

public class DataTransfer {
    private List<DataPair> mDataPairs;

    public DataTransfer() {
        mDataPairs = new ArrayList<DataPair>();
    }

    public void addData(DataPair dataPair) {
        mDataPairs.add(dataPair);
    }

    public void removeData(int i) {
        mDataPairs.remove(i);
    }

    public DataPair getDataPair(int i) {
        return mDataPairs.get(i);
    }

    public int size() {
        return mDataPairs.size();
    }
}


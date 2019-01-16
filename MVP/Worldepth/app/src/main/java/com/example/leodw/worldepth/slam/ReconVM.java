package com.example.leodw.worldepth.slam;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ClipData;

public class ReconVM extends ViewModel {
    private final MutableLiveData<ClipData.Item> selected = new MutableLiveData<ClipData.Item>();
    public void select(ClipData.Item item) {
        selected.setValue(item);
    }

    public LiveData<ClipData.Item> getSelected() {
        return selected;
    }
}

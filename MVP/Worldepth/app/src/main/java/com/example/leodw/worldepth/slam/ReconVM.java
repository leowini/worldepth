package com.example.leodw.worldepth.slam;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ClipData;
import android.graphics.Bitmap;

public class ReconVM extends ViewModel {
    private final MutableLiveData<ClipData.Item> selected = new MutableLiveData<ClipData.Item>();

    private Bitmap mPoisonPillBitmap;
    private Slam mSlam;
    public void select(ClipData.Item item) {
        selected.setValue(item);
    }

    public LiveData<ClipData.Item> getSelected() {
        return selected;
    }

    private void Reconstruct() {
        //doSlam();
        //doPoisson();
        //doTextureMapping();
    }

    public ReconVM(Bitmap poisonPillBitmap) {
        super();
        mPoisonPillBitmap = poisonPillBitmap;
        mSlam = new Slam(q, mPoisonPillBitmap);
    }
}

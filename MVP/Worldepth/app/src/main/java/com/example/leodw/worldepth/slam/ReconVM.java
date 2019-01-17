package com.example.leodw.worldepth.slam;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ClipData;
import android.graphics.Bitmap;

import com.example.leodw.worldepth.ui.camera.TimeFramePair;

public class ReconVM extends ViewModel {
    private final MutableLiveData<TimeFramePair<Bitmap, Long>> selected = new MutableLiveData<>();

    private Bitmap mPoisonPillBitmap;
    private Slam mSlam;
    public void select(TimeFramePair<Bitmap, Long> timeFramePair) {
        selected.setValue(timeFramePair);
    }

    public LiveData<TimeFramePair<Bitmap, Long>> getSelected() {
        return selected;
    }

    private void Reconstruct() {
        //doSlam();
        //doPoisson();
        //doTextureMapping();
    } public void queueFrame(TimeFramePair<Bitmap, Long> timeFramePair) {mSlam.sendFrameToSlamWrapper(selected.getValue());}

    public ReconVM(Bitmap poisonPillBitmap) {
        super();
        mPoisonPillBitmap = poisonPillBitmap;
    mSlam = new Slam(mPoisonPillBitmap);}
}

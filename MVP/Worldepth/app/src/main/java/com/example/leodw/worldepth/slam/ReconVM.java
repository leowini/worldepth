package com.example.leodw.worldepth.slam;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.example.leodw.worldepth.ui.camera.TimeFramePair;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReconVM extends ViewModel {
    private final MutableLiveData<ReconProgress> mProgress = new MutableLiveData<>();

    private final BlockingQueue<TimeFramePair<Bitmap, Long>> mQueue;
    private Bitmap mPoisonPillBitmap;
    private Slam mSlam;

    public enum ReconProgress {
        SLAM, POISSON, TM
    }

    public void sendFrameToReconVM(TimeFramePair<Bitmap, Long> timeFramePair) {
        try {
            mQueue.put(timeFramePair);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public LiveData<ReconProgress> getReconstructionProgress() {
        return mProgress;
    }

    private void Reconstruct() {
        //doSlam();
        //doPoisson();
        //doTextureMapping();
    }

    public ReconVM(Bitmap poisonPillBitmap) {
        super();
        mQueue = new LinkedBlockingQueue<>();
        mPoisonPillBitmap = poisonPillBitmap;
        mSlam = new Slam(mQueue, mPoisonPillBitmap);
        mSlam.setOnSlamCompleteListener(() -> mSlam.stopSlamThread(), new Handler(Looper.getMainLooper()));
    }

}

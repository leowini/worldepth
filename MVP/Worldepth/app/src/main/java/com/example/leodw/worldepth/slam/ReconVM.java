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

    private static final String TAG = "ReconVM";

    private final MutableLiveData<ReconProgress> mReconProgress = new MutableLiveData<>();
    private final MutableLiveData<String> mSlamProgress = new MutableLiveData<>();

    private int mRenderedFrames;
    private int mProcessedFrames;

    private static final Bitmap mPoisonPillBitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888);

    private final BlockingQueue<TimeFramePair<Bitmap, Long>> mQueue;

    private Slam mSlam;
    private PoissonWrapper mPoissonWrapper;

    private TextureMapWrapper mTextureMapWrapper;


    public enum ReconProgress {
        SLAM, POISSON, TM
    }

    public ReconVM() {
        mRenderedFrames = 0;
        mProcessedFrames = 0;
        mQueue = new LinkedBlockingQueue<>();
        mTextureMapWrapper = new TextureMapWrapper();
        mTextureMapWrapper.setOnCompleteListener(this::showModelPreview);
        mPoissonWrapper = new PoissonWrapper();
        mPoissonWrapper.setOnCompleteListener(mesh -> mTextureMapWrapper.runMapping(mesh));
        mSlam = new Slam(mQueue, mPoisonPillBitmap);
        mSlam.setOnSlamCompleteListener(pointCloud -> {
            mSlamProgress.setValue("100");
            mSlam.stopSlamThread();
            mPoissonWrapper.runPoisson(pointCloud);
        }, new Handler(Looper.getMainLooper()));
        mSlam.setFrameCountListener(this::frameProcessed, new Handler(Looper.getMainLooper()));
    }

    private void showModelPreview(int finalModel) {

    }

    private void frameProcessed() {
        mProcessedFrames++;
        updateSlamProgress();
    }

    private void frameRendered() {
        mRenderedFrames++;
        updateSlamProgress();
    }

    private void updateSlamProgress() {
        int slamProgressPercent = (int) (((float) mProcessedFrames / (float) mRenderedFrames) * 100);
        mSlamProgress.setValue(Integer.toString(slamProgressPercent));
    }

    public void sendFrameToReconVM(TimeFramePair<Bitmap, Long> timeFramePair) {
        frameRendered();
        try {
            mQueue.put(timeFramePair);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void Reconstruct() {
        //doSlam();
        //doPoisson();
        //doTextureMapping();
    }

    public Bitmap getPoisonPill() {
        return mPoisonPillBitmap;
    }

    public LiveData<String> getSlamProgress() {
        return mSlamProgress;
    }

    public LiveData<ReconProgress> getReconProgress() { return mReconProgress; }

}

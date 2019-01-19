package com.example.leodw.worldepth.slam;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.example.leodw.worldepth.ui.camera.TimeFramePair;

import org.opencv.core.Mat;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReconVM extends ViewModel {

    private static final String TAG = "ReconVM";

    private HandlerThread mReconstructionThread;
    private Handler mReconstructionHandler;

    private ReconstructionCompleteListener mCompleteListener;
    private Handler mCompleteListenerHandler;

    private final MutableLiveData<ReconProgress> mReconProgress = new MutableLiveData<>();
    private final MutableLiveData<String> mSlamProgress = new MutableLiveData<>();

    private int mRenderedFrames;
    private int mProcessedFrames;

    private static final Bitmap mPoisonPillBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

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
        mCompleteListener = finalModel -> {
            stopReconstructionThread();
            showModelPreview(finalModel);
        };
        mCompleteListenerHandler = new Handler(Looper.getMainLooper());
        mQueue = new LinkedBlockingQueue<>();
        startReconstructionThread();
        mReconstructionHandler.post(this::reconstruct);
    }

    private void startReconstructionThread() {
        mReconstructionThread = new HandlerThread("ReconstructionThread");
        mReconstructionThread.start();
        mReconstructionHandler = new Handler(mReconstructionThread.getLooper());
    }

    public void stopReconstructionThread() {
        mReconstructionThread.quitSafely();
        try {
            mReconstructionThread.join();
            mReconstructionThread = null;
            mReconstructionHandler = null;
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }
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

    private void reconstruct() {
        mTextureMapWrapper = new TextureMapWrapper();
        mTextureMapWrapper.setOnCompleteListener(finalModel ->
                mCompleteListenerHandler.post(() ->
                        mCompleteListener.onReconstructionComplete(finalModel)));
        mPoissonWrapper = new PoissonWrapper();
        mPoissonWrapper.setOnCompleteListener(mesh -> mTextureMapWrapper.runMapping(mesh));
        mSlam = new Slam(mQueue, mPoisonPillBitmap);
        mSlam.setOnSlamCompleteListener(pointCloud -> {
            mReconstructionHandler.post(() -> mSlamProgress.setValue("100"));
            mPoissonWrapper.runPoisson(pointCloud);
        });
        mSlam.setFrameCountListener(() -> mReconstructionHandler.post(this::frameProcessed));
        mSlam.doSlam();
    }

    public Bitmap getPoisonPill() {
        return mPoisonPillBitmap;
    }

    public LiveData<String> getSlamProgress() {
        return mSlamProgress;
    }

    public LiveData<ReconProgress> getReconProgress() {
        return mReconProgress;
    }

    public interface ReconstructionCompleteListener {
        void onReconstructionComplete(int finalModel);
    }

}

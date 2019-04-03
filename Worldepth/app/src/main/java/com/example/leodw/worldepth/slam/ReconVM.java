package com.example.leodw.worldepth.slam;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.leodw.worldepth.ui.camera.TimeFramePair;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.security.AccessController.getContext;

public class ReconVM extends ViewModel {

    private static final String TAG = "ReconVM";

    private HandlerThread mReconstructionThread;
    private Handler mReconstructionHandler;

    private Handler mProgressListenerHandler;

    private Handler mFrameCountHandler;

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
        INIT, READY, SLAM, POISSON, TM, COMPLETE
    }

    public ReconVM() {
        mReconProgress.setValue(ReconProgress.INIT);
        mRenderedFrames = 0;
        mProcessedFrames = 0;
        mProgressListenerHandler = new Handler(Looper.getMainLooper());
        mFrameCountHandler = new Handler(Looper.getMainLooper());
        mQueue = new LinkedBlockingQueue<>();
        startReconstructionThread();
    }

    private void startReconstructionThread() {
        mReconstructionThread = new HandlerThread("ReconstructionThread");
        mReconstructionThread.start();
        mReconstructionHandler = new Handler(mReconstructionThread.getLooper());
        mReconstructionHandler.post(this::reconstruct);
    }

    private void stopReconstructionThread() {
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
        mReconProgress.setValue(ReconProgress.COMPLETE);
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
            Log.d(TAG, timeFramePair.getFrame().getHeight()+"");
            Log.d(TAG, timeFramePair.getFrame().getWidth()+"");
            mQueue.put(timeFramePair);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void reconstruct() {
        mTextureMapWrapper = new TextureMapWrapper();
        mTextureMapWrapper.setOnCompleteListener(finalModel ->
                mProgressListenerHandler.post(() -> {
                    //stopReconstructionThread();
                    showModelPreview(finalModel);
                }));
        mPoissonWrapper = new PoissonWrapper();
        mPoissonWrapper.setOnCompleteListener(() -> {
            mProgressListenerHandler.post(() -> mReconProgress.setValue(ReconProgress.TM));
            mTextureMapWrapper.runMapping();
        });
        mSlam = new Slam(mQueue, mPoisonPillBitmap);
        mSlam.setOnCompleteListener(pointCloud -> {
            mFrameCountHandler.post(() -> {
                mProcessedFrames = mRenderedFrames;
                updateSlamProgress();
            });
            mProgressListenerHandler.post(() -> mReconProgress.setValue(ReconProgress.POISSON));
            mPoissonWrapper.runPoisson();
        });
        mSlam.setFrameCountListener(() -> mFrameCountHandler.post(this::frameProcessed));
        mProgressListenerHandler.post(() -> mReconProgress.setValue(ReconProgress.READY));
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

}

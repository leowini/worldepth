package com.example.leodw.worldepth.slam;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.MainActivity;
import com.example.leodw.worldepth.ui.camera.TimeFramePair;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.security.AccessController.getContext;

public class ReconVM extends ViewModel {

    private static final String TAG = "ReconVM";
    private String mInternalPath = "";
    private final Object lock = new Object();

    private Thread mReconstructionThread;

    private Thread mCalibrationThread;

    private Handler mProgressListenerHandler;
    private Handler mFrameCountHandler;

    private final MutableLiveData<ReconProgress> mReconProgress = new MutableLiveData<>();
    private final MutableLiveData<String> mSlamProgress = new MutableLiveData<>();

    private int mRenderedFrames;
    private int mProcessedFrames;

    private static final Bitmap mPoisonPillBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

    private final BlockingQueue<TimeFramePair<Bitmap, Double>> mQueue;

    private Slam mSlam;
    private PoissonWrapper mPoissonWrapper;
    private TextureMapWrapper mTextureMapWrapper;
    private CalibWrapper mCalibWrapper;

    public boolean calibration;


    public enum ReconProgress {
        INIT, READY, SLAM, POISSON, TM, COMPLETE, FAILED, ERROR, CALIBRATED
    }

    public ReconVM() {
        mReconProgress.setValue(ReconProgress.INIT);
        mRenderedFrames = 0;
        mProcessedFrames = 0;
        mProgressListenerHandler = new Handler(Looper.getMainLooper());
        mFrameCountHandler = new Handler(Looper.getMainLooper());
        mQueue = new LinkedBlockingQueue<>();
        startReconstructionThread();
        calibration = false;
    }

    private void startReconstructionThread() {
        mReconstructionThread = new Thread("ReconstructionThread") {
            public void run() {
                reconstruct();
            }
        };
        mReconstructionThread.start();
        mRenderedFrames = 0;
        mProcessedFrames = 0;
    }

    private void stopReconstructionThread() {
        mQueue.add(new TimeFramePair<>(mPoisonPillBitmap, (double) 0));
        mSlam.endReconstruction();
        mSlam = null;
        mProgressListenerHandler.post(() -> {
            try {
                mReconstructionThread.join();
                mReconstructionThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mPoissonWrapper = null;
            mTextureMapWrapper = null;
        });
    }

    private void showModelPreview() {
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

    public void sendFrame(TimeFramePair<Bitmap, Double> timeFramePair) {
        frameRendered();
        try {
            Log.d(TAG, timeFramePair.getFrame().getHeight() + "");
            Log.d(TAG, timeFramePair.getFrame().getWidth() + "");
            mQueue.put(timeFramePair);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setCalibration(boolean calib) {
        calibration = calib;
        if (calibration && mCalibrationThread == null) {
            startCalibrationThread();
        } else if (!calibration && mCalibrationThread != null) {
            try {
                stopCalibrationThread();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startCalibrationThread() {
        stopReconstructionThread();
        mCalibrationThread = new Thread("CalibrationThread") {
            public void run() {
                calibrate();
            }
        };
        mRenderedFrames = 0;
        mProcessedFrames = 0;
        mCalibrationThread.start();

    }

    private void stopCalibrationThread() throws InterruptedException {
        mCalibrationThread = null;
        mQueue.clear();
        mCalibWrapper = null;
        startReconstructionThread();
    }

    public boolean getCalibState() {
        return calibration;
    }

    private void calibrate() {
        synchronized (lock) {
            while (mInternalPath.equals("") || !mQueue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mCalibWrapper = new CalibWrapper(mQueue, mPoisonPillBitmap, mInternalPath);
        mCalibWrapper.setOnCompleteListener(() -> {
            mFrameCountHandler.post(() -> {
                mProcessedFrames = mRenderedFrames;
                updateSlamProgress();
            });
            mProgressListenerHandler.post(() -> mReconProgress.setValue(ReconProgress.CALIBRATED));
            setCalibration(false);
        });
        mCalibWrapper.setFrameCountListener(() -> mFrameCountHandler.post(this::frameProcessed));
        mCalibWrapper.doCalib();
    }

    private void reconstruct() {
        mTextureMapWrapper = new TextureMapWrapper();
        mTextureMapWrapper.setOnCompleteListener(() -> {
            mProgressListenerHandler.post(() -> {
                showModelPreview();
                mRenderedFrames = 0;
                mProcessedFrames = 0;
            });
        });
        mPoissonWrapper = new PoissonWrapper();
        mPoissonWrapper.setOnCompleteListener(() -> {
            mProgressListenerHandler.post(() -> mReconProgress.setValue(ReconProgress.TM));
            mTextureMapWrapper.map();
        });
        synchronized(lock){
            while (mInternalPath.equals("")) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mSlam = new Slam(mQueue, mPoisonPillBitmap, mInternalPath);
        mSlam.setOnCompleteListener(success -> {
            mFrameCountHandler.post(() -> {
                mProcessedFrames = mRenderedFrames;
                updateSlamProgress();
            });
            if (success) {
                mProgressListenerHandler.post(() -> mReconProgress.setValue(ReconProgress.POISSON));
                mPoissonWrapper.runPoisson(mInternalPath);
            } else {
                mProgressListenerHandler.post(() -> mReconProgress.setValue(ReconProgress.FAILED));
                mProgressListenerHandler.post(() -> {
                    mRenderedFrames = 0;
                    mProcessedFrames = 0;
                });
                if(!calibration) {
                    mSlam.doSlam();
                }
            }
        });
        mSlam.setFrameCountListener(() -> mFrameCountHandler.post(this::frameProcessed));
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

    public void setReconProgress(ReconProgress progress) {
        mReconProgress.setValue(progress);
    }

    public void setInternalPath(String path) {
        synchronized (lock) {
            mInternalPath = path;
            lock.notify();
        }
    }

}
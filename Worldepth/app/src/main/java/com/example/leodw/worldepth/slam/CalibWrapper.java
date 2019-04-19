package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.example.leodw.worldepth.ui.camera.TimeFramePair;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.BlockingQueue;

public class CalibWrapper {

    private static final String TAG = "Calibration";

    private final BlockingQueue<TimeFramePair<Bitmap, Long>> mQueue;

    private CalibCompleteListener mCompleteListener;

    private final Bitmap mPoisonPillBitmap;

    private FrameCountListener mFrameCountListener;

    private boolean complete;

    public native boolean passImageToCalibrate(long img);

    public native void initSettings(String internalPath);

    CalibWrapper(BlockingQueue<TimeFramePair<Bitmap, Long>> q, Bitmap mPoisonPillBitmap, String internalPath) {
        this.mQueue = q;
        this.mPoisonPillBitmap = mPoisonPillBitmap;
        complete = false;
        initSettings(internalPath);
    }

    /**
     * Converts the bitmap frame to a byte array and sends it to the C++ code.
     *
     * @param frame
     */
    private void sendFrameToCalib(Bitmap frame) {
        Mat mat = new Mat();
        Utils.bitmapToMat(frame, mat);
        if(!complete) {
            if (passImageToCalibrate(mat.getNativeObjAddr())) {
                Log.d(TAG, "calibration complete!");
            }
        }
    }

    /**
     * This will run in the background on the SlamSenderThread.
     */
    void doCalib() {
        try {
            TimeFramePair<Bitmap, Long> timeFramePair = mQueue.take();
            Bitmap bmp = timeFramePair.getFrame();
            while (!bmp.equals(mPoisonPillBitmap)) {
                mFrameCountListener.onNextFrame();
                sendFrameToCalib(bmp);
                timeFramePair = mQueue.take();
                bmp = timeFramePair.getFrame();
            }
            sendFrameToCalib(mPoisonPillBitmap);
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + " " + e.getMessage());
        }
        complete = true;
        mCompleteListener.onCalibComplete();
    }

    public interface FrameCountListener {
        void onNextFrame();
    }

    void setFrameCountListener(FrameCountListener listener) {
        mFrameCountListener = listener;
    }

    public interface CalibCompleteListener {
        void onCalibComplete();
    }

    void setOnCompleteListener(CalibCompleteListener listener) {
        mCompleteListener = listener;
    }

}


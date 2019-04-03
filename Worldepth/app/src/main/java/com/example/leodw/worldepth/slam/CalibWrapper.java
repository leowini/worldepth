package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.example.leodw.worldepth.ui.camera.TimeFramePair;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.BlockingQueue;

public class CalibWrapper {

    private static final String TAG = "Slam";

    private final BlockingQueue<TimeFramePair<Bitmap, Long>> mQueue;

    private CalibCompleteListener mCompleteListener;

    private final Bitmap mPoisonPillBitmap;

    private FrameCountListener mFrameCountListener;

    public native boolean passImageToCalibrate(long img);

    public native void initSettings();

    CalibWrapper(BlockingQueue<TimeFramePair<Bitmap, Long>> q, Bitmap mPoisonPillBitmap) {
        this.mQueue = q;
        this.mPoisonPillBitmap = mPoisonPillBitmap;
        initSettings();
    }

    /**
     * Converts the bitmap frame to a byte array and sends it to the C++ code.
     *
     * @param frame
     */
    private void sendFrameToCalib(Bitmap frame) {
            Mat mat = new Mat();
            Utils.bitmapToMat(frame, mat);
            if(passImageToCalibrate(mat.getNativeObjAddr())){
                mCompleteListener.onCalibComplete(0);
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
    }

    public interface FrameCountListener {
        void onNextFrame();
    }

    void setFrameCountListener(FrameCountListener listener) {
        mFrameCountListener = listener;
    }

    public interface CalibCompleteListener {
        void onCalibComplete(int calib);
    }

    void setOnCompleteListener(CalibCompleteListener listener) {
        mCompleteListener = listener;
    }

}


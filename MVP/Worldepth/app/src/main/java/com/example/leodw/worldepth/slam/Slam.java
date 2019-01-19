package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;

import com.example.leodw.worldepth.ui.camera.TimeFramePair;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.BlockingQueue;

public class Slam {

    private static final String TAG = "Slam";

    private final BlockingQueue<TimeFramePair<Bitmap, Long>> mQueue;

    private HandlerThread mSlamSenderThread;
    public Handler mSlamSenderHandler;

    private SlamCompleteListener mCompleteListener;

    private final Bitmap mPoisonPillBitmap;

    private FrameCountListener mFrameCountListener;

    public native void passImageToSlam(int width, int height, long img, long timeStamp);

    public Slam(BlockingQueue<TimeFramePair<Bitmap, Long>> q, Bitmap mPoisonPillBitmap) {
        this.mQueue = q;
        this.mPoisonPillBitmap = mPoisonPillBitmap;
        doSlam();
    }

    /**
     * Converts the bitmap frame to a byte array and sends it to the C++ code.
     * @param frame
     */
    private void sendFrameToSlam(Bitmap frame, Long timeStamp) {
        Mat mat = new Mat();
        Utils.bitmapToMat(frame, mat);
        passImageToSlam(frame.getWidth(), frame.getHeight(), mat.getNativeObjAddr(), timeStamp);
    }

    /**
     * This will run in the background on the SlamSenderThread.
     */
    private void doSlam() {
        try {
            TimeFramePair<Bitmap, Long> timeFramePair = mQueue.take();
            Bitmap bmp = timeFramePair.getFrame();
            Long time = timeFramePair.getTime();
            do {
                mFrameCountListener.onNextFrame();
                sendFrameToSlam(bmp, time);
                timeFramePair = mQueue.take();
                bmp = timeFramePair.getFrame();
                time = timeFramePair.getTime();
            } while (!bmp.equals(mPoisonPillBitmap));
        }
        catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + " " + e.getMessage());
        }
        mCompleteListener.onSlamComplete(0);
    }

    private void startSlamThread() {
        mSlamSenderThread = new HandlerThread("Slam Background");
        mSlamSenderThread.start();
        mSlamSenderHandler = new Handler(mSlamSenderThread.getLooper());
    }

    public void stopSlamThread() {
        mSlamSenderThread.quitSafely();
        try {
            mSlamSenderThread.join();
            mSlamSenderThread = null;
            mSlamSenderHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface FrameCountListener {
        void onNextFrame();
    }

    public void setFrameCountListener(FrameCountListener listener) {
        mFrameCountListener = listener;
    }

    public interface SlamCompleteListener {
        void onSlamComplete(int pointCloud);
    }

    public void setOnSlamCompleteListener(SlamCompleteListener listener) {
        mCompleteListener = listener;
    }

}
package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;

import com.example.leodw.worldepth.ui.camera.TimeFramePair;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.BlockingQueue;

public class Slam {
    public static final String TAG = "Slam";

    private HandlerThread mSlamSenderThread;
    public Handler mSlamSenderHandler;

    private SlamCompleteListener mCompleteListener;
    private Handler mCompleteListenerHandler;

    private final Bitmap mPoisonPillBitmap;
    private final BlockingQueue<TimeFramePair<Bitmap, Long>> mQueue;

    public native void passImageToSlam(int width, int height, byte[] img, long timeStamp);

    public Slam(BlockingQueue<TimeFramePair<Bitmap, Long>> q, Bitmap mPoisonPillBitmap) {
        this.mQueue = q;
        this.mPoisonPillBitmap = mPoisonPillBitmap;
        startSlamThread();
        mSlamSenderHandler.post(this::doSlam);
    }

    /**
     * Converts the bitmap frame to a byte array and sends it to the C++ code.
     * @param frame
     */
    public void sendFrameToSlam(Bitmap frame, Long timeStamp) {
        byte[] byteArray = bitmapToByteArray(frame);
        passImageToSlam(frame.getWidth(), frame.getHeight(), byteArray, timeStamp);
    }

    /**
     * This will run in the background on the SlamSenderThread.
     */
    private void doSlam() {
        try {
            TimeFramePair<Bitmap, Long> timeFramePair; = mQueue.take();
            Bitmap bmp; = timeFramePair.getFrame();
            Long time; = timeFramePair.getTime();
            do {
                timeFramePair = mQueue.take();
                bmp = timeFramePair.getFrame();
                time = timeFramePair.getTime();
                sendFrameToSlam(bmp, time);
            } while (!bmp.equals(mPoisonPillBitmap));
        }
        catch (Exception e) {
            System.out.println
                    (Thread.currentThread().getName() + " " + e.getMessage());
        }
        mCompleteListenerHandler.post(() -> mCompleteListener.onSlamComplete());
    }

    private byte[] bitmapToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
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

    public void setOnSlamCompleteListener(SlamCompleteListener listener, Handler handler) {
        mCompleteListener = listener;
        mCompleteListenerHandler = handler;
    }

    public interface SlamCompleteListener {
        void onSlamComplete();
    }

}
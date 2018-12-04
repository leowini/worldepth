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

    private final Bitmap mPoisonPillBitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888);
    private final BlockingQueue<TimeFramePair> mQueue;

    public native void passImageToSlam(int width, int height, byte[] img, long timeStamp);

    public Slam(BlockingQueue<TimeFramePair> q) {
        this.mQueue = q;
        startSlamThread();
        mSlamSenderHandler.post(() -> doSlam());
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
            TimeFramePair<Bitmap, Long> timeFramePair = mQueue.take();
            Bitmap bmp = timeFramePair.getFrame();
            Long time = timeFramePair.getTime();
            while (!bmp.equals(mPoisonPillBitmap)) {
                sendFrameToSlam(bmp, time);
                timeFramePair = mQueue.take();
                bmp = timeFramePair.getFrame();
                time = timeFramePair.getTime();
            }
        }
        catch (Exception e) {
            System.out.println
                    (Thread.currentThread().getName() + " " + e.getMessage());
        }
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

    /**
     * Put the end of data signal on mQueue on the SlamSenderThread.
     */
    public void signalImageQueueEnd() {
        //Put the end of data signal on the queue on the SlamThread.
        mSlamSenderHandler.post(() -> {
            try {
                mQueue.put(new TimeFramePair<Bitmap, Long>(mPoisonPillBitmap, (long)0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void stopSlamThread() {
        mSlamSenderThread.quitSafely();
        try {
            //The SlamThread isn't joining :(
            mSlamSenderThread.join();
            mSlamSenderThread = null;
            mSlamSenderHandler = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
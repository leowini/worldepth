package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.BlockingQueue;

public class Slam {
    public static final String TAG = "Slam";

    private HandlerThread mSlamThread;
    public Handler mSlamHandler;

    private final Bitmap mPoisonPillBitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888);
    private final BlockingQueue<Bitmap> mQueue;

    public native void passImageToSlam(int width, int height, byte[] img);

    public Slam(BlockingQueue<Bitmap> q) {
        this.mQueue = q;
        startSlamThread();
        mSlamHandler.post(() -> doSlam());
    }

    /**
     * Converts the bitmap frame to a byte array and sends it to the C++ code.
     * @param frame
     */
    public void sendFrameToSlam(Bitmap frame) {
        byte[] byteArray = bitmapToByteArray(frame);
        passImageToSlam(frame.getWidth(), frame.getHeight(), byteArray);
    }

    /**
     * This will run in the background on the mSlamThread.
     */
    private void doSlam() {
        try {
            Bitmap bmp = mQueue.take();
            while (!bmp.equals(mPoisonPillBitmap)) {
                sendFrameToSlam(bmp);
                bmp = mQueue.take();
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
        mSlamThread = new HandlerThread("Slam Background");
        mSlamThread.start();
        mSlamHandler = new Handler(mSlamThread.getLooper());
    }

    /**
     * Put the end of data signal on mQueue on the SlamThread.
     */
    public void signalImageQueueEnd() {
        //Put the end of data signal on the queue on the SlamThread.
        mSlamHandler.post(() -> {
            try {
                mQueue.put(mPoisonPillBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void stopSlamThread() {
        mSlamThread.quitSafely();
        try {
            //The SlamThread isn't joining :(
            mSlamThread.join();
            mSlamThread = null;
            mSlamHandler = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
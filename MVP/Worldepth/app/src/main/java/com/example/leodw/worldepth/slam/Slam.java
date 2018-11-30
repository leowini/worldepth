package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.example.leodw.worldepth.ui.camera.Renderer;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Slam implements Renderer.OnBitmapFrameAvailableListener {
    public static final String TAG = "Slam";

    private SlamThread mSlamThread;
    public Handler mSlamHandler;

    private Queue<Bitmap> mBitmapQueue = new LinkedList<Bitmap>();

    private Object mFrameSyncObject; //guards mFrameAvailable
    private boolean mFrameAvailable;

    public native void passImage(int width, int height, byte[] img);

    public Slam() {
        startSlamThread();
    }

    public void sendFrameToSlam(Bitmap frame) {
        byte[] byteArray = bitmapToByteArray(frame);
        passImage(frame.getWidth(), frame.getHeight(), byteArray);
    }

    /**
     * This will run in the background on the mSlamThread.
     */
    private void doSlam() {
        while(true) {
            while (!mBitmapQueue.isEmpty()) {
                sendFrameToSlam(mBitmapQueue.remove());
            }
        }
    }

//    /**
//     * Waits for frames to feed into SLAM - I think my code is currently broken because this needs
//     * to run on the thread that creates the Slam class instance.
//     */
//    public void awaitNewImage() {
//        final int TIMEOUT_MS = 2500;
//
//        synchronized (mFrameSyncObject) {
//            while (!mFrameAvailable) {
//                try {
//                    // Wait for onFrameAvailable() to signal us.  Use a timeout to avoid
//                    // stalling the test if it doesn't arrive.
//                    mFrameSyncObject.wait(TIMEOUT_MS);
//                    if (!mFrameAvailable) {
//                        // TODO: if "spurious wakeup", continue while loop
//                        throw new RuntimeException("frame wait timed out");
//                    }
//                } catch (InterruptedException ie) {
//                    // shouldn't happen
//                    throw new RuntimeException(ie);
//                }
//            }
//            mFrameAvailable = false;
//        }
//    }

//    @Override
//    public void onBitmapFrameAvailable(Bitmap bmp) {
//        synchronized (mFrameSyncObject) {
//            if (mFrameAvailable) {
//                throw new RuntimeException("mFrameAvailable already set, frame could be dropped");
//            }
//            mBackgroundHandler.post(() -> mBitmapQueue.add(bmp));
//            mFrameAvailable = true;
//            mFrameSyncObject.notifyAll();
//        }
//    }


    @Override
    public void onBitmapFrameAvailable(Bitmap bmp) {
        mSlamHandler.post(() -> mBitmapQueue.add(bmp));
    }

    private byte[] bitmapToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void startSlamThread() {
        mSlamThread = new SlamThread("Slam Background");
        mSlamThread.start();
        mSlamHandler = new Handler(mSlamThread.getLooper());
    }

    private void stopSlamThread() {
        mSlamThread.quitSafely();
        try {
            mSlamThread.join();
            mSlamThread = null;
            mSlamHandler = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class SlamThread extends HandlerThread {

        public SlamThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            doSlam();
        }
    }

}
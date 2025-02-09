package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import com.example.leodw.worldepth.ui.camera.TimeFramePair;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import java.io.File;
import java.util.concurrent.BlockingQueue;

public class Slam {

    private static final String TAG = "Slam";

    private final BlockingQueue<TimeFramePair<Bitmap, Double>> mQueue;

    private SlamCompleteListener mCompleteListener;

    private final Bitmap mPoisonPillBitmap;

    private FrameCountListener mFrameCountListener;

    public native boolean passImageToSlam(long img, double timeStamp);
    public native void initSystem(String vocFile, String settingsFile, String internalPath);
    public native void endReconstruction();

    Slam(BlockingQueue<TimeFramePair<Bitmap, Double>> q, Bitmap mPoisonPillBitmap, String internalPath) {
        this.mQueue = q;
        this.mPoisonPillBitmap = mPoisonPillBitmap;
        initSystem(internalPath + "/ORBvoc.bin",
                internalPath + "/CalibVals.yaml",
                internalPath);
    }

    /**
     * Converts the bitmap frame to a byte array and sends it to the C++ code.
     *
     * @param frame
     * @param timeStamp
     */
    private boolean sendFrameToSlam(Bitmap frame, Double timeStamp) {
        boolean success = true;
        if (frame.equals(mPoisonPillBitmap)) {
            success = passImageToSlam(0, timeStamp);
        } else {
            Mat mat = new Mat();
            Utils.bitmapToMat(frame, mat);
            success = passImageToSlam(mat.getNativeObjAddr(), timeStamp);
        }
        frame.recycle();
        return success;
    }

    /**
     * This will run in the background on the SlamSenderThread.
     */
    void doSlam() {
        boolean success = true;
        try {
            TimeFramePair<Bitmap, Double> timeFramePair = mQueue.take();
            Bitmap bmp = timeFramePair.getFrame();
            Double time = timeFramePair.getTime();
            while (!bmp.equals(mPoisonPillBitmap)) {
                mFrameCountListener.onNextFrame();
                success = sendFrameToSlam(bmp, time);
                timeFramePair = mQueue.take();
                bmp = timeFramePair.getFrame();
                time = timeFramePair.getTime();
            }
            success = sendFrameToSlam(bmp, time);
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName() + " " + e.getMessage());
        }
        mCompleteListener.onSlamComplete(success);
    }

    public interface FrameCountListener {
        void onNextFrame();
    }

    void setFrameCountListener(FrameCountListener listener) {
        mFrameCountListener = listener;
    }

    public interface SlamCompleteListener {
        void onSlamComplete(boolean success);
    }

    void setOnCompleteListener(SlamCompleteListener listener) {
        mCompleteListener = listener;
    }

}
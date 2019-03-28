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

public class Slam {

    private static final String TAG = "Slam";

    private final BlockingQueue<TimeFramePair<Bitmap, Long>> mQueue;

    private SlamCompleteListener mCompleteListener;

    private final Bitmap mPoisonPillBitmap;

    private FrameCountListener mFrameCountListener;

    public native boolean passImageToSlam(long img, long timeStamp);

    public native void initSystem(String vocFile, String settingsFile);

    Slam(BlockingQueue<TimeFramePair<Bitmap, Long>> q, Bitmap mPoisonPillBitmap) {
        this.mQueue = q;
        this.mPoisonPillBitmap = mPoisonPillBitmap;
        initSystem("/data/user/0/com.example.leodw.worldepth/files/ORBvoc.bin",
                "/data/user/0/com.example.leodw.worldepth/files/TUM1.yaml");
    }

    /**
     * Converts the bitmap frame to a byte array and sends it to the C++ code.
     *
     * @param frame
     * @param timeStamp
     */
    private boolean sendFrameToSlam(Bitmap frame, Long timeStamp) {
        boolean success = true;
        if (frame.equals(mPoisonPillBitmap)) {
            success = passImageToSlam(0, timeStamp);
        } else {
            Mat mat = new Mat();
            Utils.bitmapToMat(frame, mat);
            success = passImageToSlam(mat.getNativeObjAddr(), timeStamp);
        }
        return success;
    }

    /**
     * Returns boolean based on whether SLAM was successful (found keyframes)
     * This is to make sure Texture Mapping has source images.
     * @return
     */
    void doSlam() {
        boolean success = true;
        try {
            TimeFramePair<Bitmap, Long> timeFramePair = mQueue.take();
            Bitmap bmp = timeFramePair.getFrame();
            Long time = timeFramePair.getTime();
            while (!bmp.equals(mPoisonPillBitmap)){
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
package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.leodw.worldepth.ui.camera.Renderer;

import java.io.ByteArrayOutputStream;

public class Slam extends Thread implements Renderer.OnBitmapFrameAvailableListener {
    public static final String TAG = "Slam";

    private Object mFrameSyncObject; //guards mFrameAvailable
    private boolean mFrameAvailable;

    public native void passImage(int width, int height, byte[] img);

    public void sendFrameToSlam(Bitmap frame) {
        byte[] byteArray = bitmapToByteArray(frame);
        passImage(frame.getWidth(), frame.getHeight(), byteArray);
    }

    @Override
    public void run() {
        doSlam();
    }

    /**
     * This will run in the background.
     */
    private void doSlam() {
        while(true /*!slamDone*/) {
            awaitNewImage();
            //sendFrameToSlam();
        }
    }

    /**
     * Waits for frames to feed into SLAM - I think my code is currently broken because this needs
     * to run on the thread that creates the Slam class instance.
     */
    public void awaitNewImage() {
        final int TIMEOUT_MS = 2500;

        synchronized (mFrameSyncObject) {
            while (!mFrameAvailable) {
                try {
                    // Wait for onFrameAvailable() to signal us.  Use a timeout to avoid
                    // stalling the test if it doesn't arrive.
                    mFrameSyncObject.wait(TIMEOUT_MS);
                    if (!mFrameAvailable) {
                        // TODO: if "spurious wakeup", continue while loop
                        throw new RuntimeException("frame wait timed out");
                    }
                } catch (InterruptedException ie) {
                    // shouldn't happen
                    throw new RuntimeException(ie);
                }
            }
            mFrameAvailable = false;
        }
    }

    @Override
    public void onBitmapFrameAvailable(Bitmap bmp) {
        synchronized (mFrameSyncObject) {
            if (mFrameAvailable) {
                throw new RuntimeException("mFrameAvailable already set, frame could be dropped");
            }
            mFrameAvailable = true;
            mFrameSyncObject.notifyAll();
        }
    }

    private byte[] bitmapToByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
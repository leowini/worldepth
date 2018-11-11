package com.example.leodw.videoappspike;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Semaphore;

public class Renderer implements SurfaceTexture.OnFrameAvailableListener {
    private RenderThread renderThread;
    private SurfaceTexture mEglSurfaceTexture;
    private EglSurfaceTextureListener mListener;
    private Handler mListenerHandler;
    private IGLRenderer renderer;
    private final EglHelper mEglHelper = new EglHelper();
    private int mSurfaceWidth, mSurfaceHeight;

    public Renderer(IGLRenderer renderer) {
        this.renderer = renderer;
    }

    public void startRecording(int width, int height) {
        if (renderThread != null) {
            throw new IllegalStateException("Already have a context");
        }
        renderThread = new RenderThread();
        renderThread.start();
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    public void stopRecording() {
        if (renderThread == null) return;

        renderThread.handler.post(() -> {
            Looper looper = Looper.myLooper();
            if (looper != null) {
                looper.quit();
            }
        });
        renderThread = null;
    }

    public void setListener(EglSurfaceTextureListener listener, Handler handler) {
        mListener = listener;
        mListenerHandler = handler;
    }

    private void configure() {
        mEglSurfaceTexture = mEglHelper.createSurface(mSurfaceTexture, false);
        renderer.initShader(mEglSurfaceTexture, mSurfaceWidth, mSurfaceHeight);
        mEglSurfaceTexture.setOnFrameAvailableListener(this, renderThread.handler);
        //At this point we should be ready to accept frames from the camera
        mListenerHandler.post(new Runnable() {
            @Override public void run() {
                mListener.onSurfaceTextureReady(mEglSurfaceTexture);
            }
        });
    }

    private void dispose() {
        renderer.stop(mEglSurfaceTexture);
        mEglHelper.destroySurface();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        renderer.onFrameAvailable(mEglSurfaceTexture);
        mEglHelper.makeCurrent();
        mEglHelper.swapBuffers();
    }

    private class RenderThread extends Thread {
        private Semaphore eglContextReadyLock = new Semaphore(0);
        private Handler handler;

        @Override
        public void run() {
            Looper.prepare();
            handler = new Handler();
            configure();
            eglContextReadyLock.release();
            Looper.loop();
            dispose();
        }

        Handler blockingHandler() {
            //Block until the next EGL context is ready to accept messages
            eglContextReadyLock.acquireUninterruptibly();
            eglContextReadyLock.release();
            return this.handler;
        }
    }

    public SurfaceTexture getEGLSurfaceTexture() {
        return mEglSurfaceTexture;
    }

    public interface EglSurfaceTextureListener {
        /**
         * Underlying EGL Context is ready.
         */
        void onSurfaceTextureReady(SurfaceTexture surfaceTexture);
    }

    public interface IGLRenderer {

        void initShader(SurfaceTexture eglSurfaceTexture, int surfaceWidth, int surfaceHeight);

        void stop(SurfaceTexture eglSurfaceTexture);

        void onFrameAvailable(SurfaceTexture eglSurfaceTexture);
    }
}
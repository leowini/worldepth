package com.example.leodw.worldepth.ui.camera;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.leodw.worldepth.slam.Slam;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.Semaphore;

public class Renderer implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "Renderer";
    private SurfaceTexture mEglSurfaceTexture;
    private RenderThread mRenderThread;
    private EglSurfaceTextureListener mListener;
    private Handler mListenerHandler;
    private int mSurfaceWidth, mSurfaceHeight;
    private STextureRender renderer;
    private int decodeCount = 1;
    private static final File FILES_DIR = Environment.getExternalStorageDirectory();
    private ByteBuffer mPixelBuf;                       // used by saveFrame()

    private EGLDisplay mEGLDisplay;
    private EGLSurface mEGLSurface;
    private EGLContext mEGLContext;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "frame available");
        // Latch the data.
        renderer.checkGlError("before updateTexImage");
        mEglSurfaceTexture.updateTexImage();

        renderer.drawFrame(mEglSurfaceTexture, false);

        Slam.FrameListener frameListener = new Slam.FrameListener() {
            @Override
            public void onFrameAvailable() {
                se
            }
        }

        if (decodeCount <= 10) {
            File outputFile = new File(FILES_DIR,
                    String.format("frame-%02d.png", decodeCount));
            try {
                saveFrame(outputFile.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Image Saved!");
            decodeCount++;
        }
    }

    /**
     * Saves the drawn frame
     * @param filename
     * @throws IOException
     */
    private void saveFrame(String filename) throws IOException {
        mPixelBuf.rewind();
        GLES20.glReadPixels(0, 0, mSurfaceWidth, mSurfaceHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                mPixelBuf);

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filename));
            Bitmap bmp = Bitmap.createBitmap(mSurfaceWidth, mSurfaceHeight, Bitmap.Config.ARGB_8888);
            mPixelBuf.rewind();
            bmp.copyPixelsFromBuffer(mPixelBuf);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bmp.recycle();
        } finally {
            if (bos != null) bos.close();
        }
        Log.d(TAG, "Saved" + mSurfaceWidth + "x" + mSurfaceHeight + " frame as '" + filename + "'");
    }

    /**
     * Starts the Renderthread and initializes the render dimensions
     * @param width
     * @param height
     */
    public void start(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }
        mRenderThread = new RenderThread();
        mRenderThread.start();
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        Log.d(TAG, "Slam width: " + mSurfaceWidth);
        Log.d(TAG, "Slam height: " + mSurfaceHeight);
    }

    public void stop() {
        if (mRenderThread == null) return;

        mRenderThread.handler.post(() -> {
            Looper looper = Looper.myLooper();
            if (looper != null) {
                looper.quit();
            }
        });
        mRenderThread = null;
    }

    /**
     * This sets the listener for when the mEglSurfaceTexture is ready to be sent
     * back to the CameraViewModel.
     */
    public void setOnSurfaceTextureReadyListener(EglSurfaceTextureListener listener, Handler handler) {
        mListener = listener;
        mListenerHandler = handler;
    }

    /**
     * Configures the mEglSurfaceTexture and sends it to the main thread to use for mSlamOutputSurface
     */
    private void configure() {
        eglSetup();
        makeCurrent();
        setup();
        mEglSurfaceTexture.setOnFrameAvailableListener(this, mRenderThread.handler);
        mListenerHandler.post(() -> mListener.onSurfaceTextureReady(mEglSurfaceTexture));
    }

    private void eglSetup() {
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            mEGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL14");
        }

        // Configure EGL for pbuffer and OpenGL ES 2.0, 24-bit RGB.
        int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                numConfigs, 0)) {
            throw new RuntimeException("unable to find RGB888+recordable ES2 EGL config");
        }

        // Configure context for OpenGL ES 2.0.
        int[] attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT,
                attrib_list, 0);
        checkEglError("eglCreateContext");
        if (mEGLContext == null) {
            throw new RuntimeException("null context");
        }

        // Create a pbuffer surface.
        int[] surfaceAttribs = {
                EGL14.EGL_WIDTH, mSurfaceWidth,
                EGL14.EGL_HEIGHT, mSurfaceHeight,
                EGL14.EGL_NONE
        };
        mEGLSurface = EGL14.eglCreatePbufferSurface(mEGLDisplay, configs[0], surfaceAttribs, 0);
        checkEglError("eglCreatePbufferSurface");
        if (mEGLSurface == null) {
            throw new RuntimeException("surface was null");
        }
    }

    /**
     * Makes our EGL context and surface current.
     */
    public void makeCurrent() {
        if (!EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    /**
     * Creates interconnected instances of TextureRender, SurfaceTexture, and Surface.
     */
    private void setup() {
        renderer = new Renderer.STextureRender();
        renderer.surfaceCreated();

        Log.d(TAG, "textureID=" + renderer.getTextureId());
        mEglSurfaceTexture = new SurfaceTexture(renderer.getTextureId());

        // This doesn't work if this object is created on the thread that CTS started for
        // these test cases.
        //
        // The CTS-created thread has a Looper, and the SurfaceTexture constructor will
        // create a Handler that uses it.  The "frame available" message is delivered
        // there, but since we're not a Looper-based thread we'll never see it.  For
        // this to do anything useful, CodecOutputSurface must be created on a thread without
        // a Looper, so that SurfaceTexture uses the main application Looper instead.
        //
        // Java language note: passing "this" out of a constructor is generally unwise,
        // but we should be able to get away with it here.
        mEglSurfaceTexture.setOnFrameAvailableListener(this);

        //mSurface = new Surface(mEglSurfaceTexture);

        mPixelBuf = ByteBuffer.allocateDirect(mSurfaceWidth * mSurfaceHeight * 4);
        mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);
    }

    private void dispose() {
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            boolean released;
            released = EGL14.eglTerminate(mEGLDisplay);
            released = EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            released = EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            released = EGL14.eglReleaseThread();
        }

        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        mEGLContext = EGL14.EGL_NO_CONTEXT;
        mEGLSurface = EGL14.EGL_NO_SURFACE;
        renderer = null;
        mEglSurfaceTexture = null;
    }

    /**
     * Functional interface that contains the onSurfaceTextureReady method
     */
    public interface EglSurfaceTextureListener {
        void onSurfaceTextureReady(SurfaceTexture surfaceTexture);
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
            //Block until the EGL context is ready to accept messages
            eglContextReadyLock.acquireUninterruptibly();
            eglContextReadyLock.release();
            return this.handler;
        }
    }

    /**
     * Code for rendering a texture onto a surface using OpenGL ES 2.0.
     */
    private static class STextureRender {
        private static final int FLOAT_SIZE_BYTES = 4;
        private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
        private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
        private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
        private final float[] mTriangleVerticesData = {
                // X, Y, Z, U, V
                -1.0f, -1.0f, 0, 0.f, 0.f,
                1.0f, -1.0f, 0, 1.f, 0.f,
                -1.0f, 1.0f, 0, 0.f, 1.f,
                1.0f, 1.0f, 0, 1.f, 1.f,
        };

        private FloatBuffer mTriangleVertices;

        private static final String VERTEX_SHADER =
                "uniform mat4 uMVPMatrix;\n" +
                        "uniform mat4 uSTMatrix;\n" +
                        "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTextureCoord;\n" +
                        "varying vec2 vTextureCoord;\n" +
                        "void main() {\n" +
                        "    gl_Position = uMVPMatrix * aPosition;\n" +
                        "    vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                        "}\n";

        private static final String FRAGMENT_SHADER =
                "#extension GL_OES_EGL_image_external : require\n" +
                        "precision mediump float;\n" +      // highp here doesn't seem to matter
                        "varying vec2 vTextureCoord;\n" +
                        "uniform samplerExternalOES sTexture;\n" +
                        "void main() {\n" +
                        "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                        "}\n";

        private float[] mMVPMatrix = new float[16];
        private float[] mSTMatrix = new float[16];

        private int mProgram;
        private int mTextureID = -12345;
        private int muMVPMatrixHandle;
        private int muSTMatrixHandle;
        private int maPositionHandle;
        private int maTextureHandle;

        public STextureRender() {
            mTriangleVertices = ByteBuffer.allocateDirect(
                    mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mTriangleVertices.put(mTriangleVerticesData).position(0);

            Matrix.setIdentityM(mSTMatrix, 0);
        }

        public int getTextureId() {
            return mTextureID;
        }

        /**
         * Draws the external texture in SurfaceTexture onto the current EGL surface.
         */
        public void drawFrame(SurfaceTexture st, boolean invert) {
            checkGlError("onDrawFrame start");
            st.getTransformMatrix(mSTMatrix);
            if (invert) {
                mSTMatrix[5] = -mSTMatrix[5];
                mSTMatrix[13] = 1.0f - mSTMatrix[13];
            }

            // (optional) clear to green so we can see if we're failing to set pixels
            GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glUseProgram(mProgram);
            checkGlError("glUseProgram");

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            checkGlError("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            checkGlError("glEnableVertexAttribArray maPositionHandle");

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                    TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
            checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(maTextureHandle);
            checkGlError("glEnableVertexAttribArray maTextureHandle");

            Matrix.setIdentityM(mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            checkGlError("glDrawArrays");

            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        }

        /**
         * Initializes GL state.  Call this after the EGL surface has been created and made current.
         */
        public void surfaceCreated() {
            mProgram = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
            if (mProgram == 0) {
                throw new RuntimeException("failed creating program");
            }

            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            checkLocation(maPositionHandle, "aPosition");
            maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
            checkLocation(maTextureHandle, "aTextureCoord");

            muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            checkLocation(muMVPMatrixHandle, "uMVPMatrix");
            muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
            checkLocation(muSTMatrixHandle, "uSTMatrix");

            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);

            mTextureID = textures[0];
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
            checkGlError("glBindTexture mTextureID");

            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            checkGlError("glTexParameter");
        }

        /**
         * Replaces the fragment shader.  Pass in null to reset to default.
         */
        public void changeFragmentShader(String fragmentShader) {
            if (fragmentShader == null) {
                fragmentShader = FRAGMENT_SHADER;
            }
            GLES20.glDeleteProgram(mProgram);
            mProgram = createProgram(VERTEX_SHADER, fragmentShader);
            if (mProgram == 0) {
                throw new RuntimeException("failed creating program");
            }
        }

        private int loadShader(int shaderType, String source) {
            int shader = GLES20.glCreateShader(shaderType);
            checkGlError("glCreateShader type=" + shaderType);
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
            return shader;
        }

        private int createProgram(String vertexSource, String fragmentSource) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            if (vertexShader == 0) {
                return 0;
            }
            int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
            if (pixelShader == 0) {
                return 0;
            }

            int program = GLES20.glCreateProgram();
            if (program == 0) {
                Log.e(TAG, "Could not create program");
            }
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
            return program;
        }

        public void checkGlError(String op) {
            int error;
            while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
                Log.e(TAG, op + ": glError " + error);
                throw new RuntimeException(op + ": glError " + error);
            }
        }

        public static void checkLocation(int location, String label) {
            if (location < 0) {
                throw new RuntimeException("Unable to locate '" + label + "' in program");
            }
        }
    }

    /**
     * Checks for EGL errors.
     */
    private void checkEglError(String msg) {
        int error;
        if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }
}
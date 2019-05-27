package com.example.leodw.worldepth.ui.camera;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.slam.ReconVM;
import com.example.leodw.worldepth.slam.Slam;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import androidx.navigation.Navigation;

public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    private ReconVM mReconVM;

    private Slam mSlam;
    //private SurfaceTexture mSlamOutputSurface;

    private Button captureBtn;
    private ImageView mMapButton;
    private AutoFitTextureView mTextureView;

    private boolean mRecordingState;

    private Integer sensorOrientation;

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    private ByteBuffer mPixelBuf; // used by saveFrame()
    private ImageReader mImageReader;

    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;
    private Allocation in, out;

    private OnFrameRenderedListener mFrameRenderedListener;
    private Handler mFrameRenderedListenerHandler;

    private static int frameCount = 0;

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession mPreviewSession;
    private CaptureRequest.Builder mPreviewBuilder;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private Size mPreviewSize;
    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 640;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 480;

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    //Video file
    private String nextVideoAbsolutePath;

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {

            cameraDevice = camera;
            startPreview();
            mCameraOpenCloseLock.release();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            if(cameraDevice != null) {
                cameraDevice.close();
                camera = null;
            }
            Log.e(TAG, "" + error);
        }
    };

    /**
     * This goes through each possible camera output size and chooses the smallest
     * size of the sizes that are bigger than the TextureView and smaller than the display dimensions.
     *
     * @param choices
     * @param mTextureViewWidth
     * @param mTextureViewHeight
     * @param maxWidth
     * @param maxHeight
     * @param aspectRatio
     * @return
     */
    private static Size chooseOptimalSize(Size[] choices, int mTextureViewWidth,
                                          int mTextureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= mTextureViewWidth &&
                        option.getHeight() >= mTextureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    /**
     * starts the camera preview
     */
    private void startPreview() {
        if (null == cameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
        try {
            closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            final Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            mPreviewSession = session;
                            updatePreview();
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Activity activity = getActivity();
                            if (activity != null) {
                                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the camera preview on the background thread
     */
    private void updatePreview() {
        if (cameraDevice == null)
            return;
        setUpCaptureRequestBuilder(mPreviewBuilder);
        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null,
                    mBackgroundHandler);
        } catch (CameraAccessException e1) {
            e1.printStackTrace();
        }
    }


    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //check real-time permissions
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.CAMERA,
            }, REQUEST_CAMERA_PERMISSION);
            Log.i(TAG, "permission not granted, asking");

        }
        return inflater.inflate(R.layout.camera_fragment, container, false);

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        rs = RenderScript.create(getContext());
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        mReconVM = ViewModelProviders.of(getActivity()).get(ReconVM.class);
        mTextureView = view.findViewById(R.id.textureView);
        assert mTextureView != null;
        captureBtn = (Button) view.findViewById(R.id.captureButton);
        mMapButton = (ImageView) view.findViewById(R.id.cameraToMapButton);
        captureBtn.setOnTouchListener((v, event) -> {
            Log.d(TAG, event.getAction() + "");
            switch (event.getAction()) {
                case (MotionEvent.ACTION_DOWN):
                    Log.d(TAG, "Capturing");
                    Log.d(TAG, mRecordingState + "");
                    if (!mRecordingState) {
                        startRecording();
                        Log.d(TAG, "Start Recording");
                        mRecordingState = true;
                        return true;
                    }
                    return false;
                case (MotionEvent.ACTION_UP):
                    Log.d(TAG, "Stop Capturing");
                    if (mRecordingState) {
                        stopRecording();
                        mRecordingState = false;
                        Navigation.findNavController(getView()).navigate(R.id.action_cameraFragment_to_reconstructionFragment);
                        mFrameRenderedListenerHandler.post(() -> mFrameRenderedListener.onFrameRendered(
                                new TimeFramePair<Bitmap, Double>(mReconVM.getPoisonPill(), (double) 0)));
                        return true;
                    }
                    return false;
                default:
                    Log.d(TAG, event.getAction() + "");
                    return false;
            }
        });
        mMapButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_cameraFragment_to_mapFragment));
    }

    /**
     * Configures the renderer on the render thread and sets a callback to startCameraRecording()
     * when the SurfaceTexture is configured.
     */
    private void startRecording() {
        //Queue for images and timestamps to send to Slam.
        BlockingQueue<TimeFramePair<Bitmap, Long>> q = new LinkedBlockingQueue<>();
        //Poison pill to signal end of queue.
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(),
                ImageFormat.YUV_420_888, 20);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                frameCount++;
                Log.d(TAG, ""+frameCount);
                Image image = reader.acquireLatestImage();
                if (image == null) return;
                Image.Plane Y = image.getPlanes()[0];
                Image.Plane U = image.getPlanes()[1];
                Image.Plane V = image.getPlanes()[2];

                int Yb = Y.getBuffer().remaining();
                int Ub = U.getBuffer().remaining();
                int Vb = V.getBuffer().remaining();

                byte[] data = new byte[Yb + Ub + Vb];


                Y.getBuffer().get(data, 0, Yb);
                U.getBuffer().get(data, Yb, Ub);
                V.getBuffer().get(data, Yb + Ub, Vb);
                if (yuvType == null)
                {
                    yuvType = new Type.Builder(rs, Element.U8(rs)).setX(data.length);
                    in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

                    rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(image.getWidth()).setY(image.getHeight());
                    out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
                }

                in.copyFrom(data);

                yuvToRgbIntrinsic.setInput(in);
                yuvToRgbIntrinsic.forEach(out);

                Bitmap bmp = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
                out.copyTo(bmp);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 640 * bmp.getWidth()/bmp.getHeight(), 640, true);
                double frameTimeStamp = (double) Calendar.getInstance().getTimeInMillis() /1000;
                //Bitmap bmp = getBitmap(NV21toJPEG(data, image.getWidth(), image.getHeight()));
                //writeToFile(bmp, frameTimeStamp);
                try {
                    mFrameRenderedListenerHandler.post(() -> mFrameRenderedListener
                            .onFrameRendered(new TimeFramePair<Bitmap, Double>(scaledBmp, frameTimeStamp)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                image.close();
            }
        }, mBackgroundHandler);

        startCameraRecording();
        setOnFrameRenderedListener((timeFramePair) -> mReconVM.sendFrame(timeFramePair), new Handler(Looper.getMainLooper()));
        mReconVM.setReconProgress(ReconVM.ReconProgress.SLAM);
    }

    private void writeToFile(Bitmap bmp, Double timeStamp){
        String filename = "" + timeStamp + ".png";
        boolean intro = true; //already has intro text
        File dir = new File("data/user/0/com.example.leodw.worldepth/files/rgb");
        if(!dir.exists()){
            dir.mkdir();
            intro = false;
        }
        try {
            String written = "" + timeStamp + " rgb/" + filename;
            saveFrame(bmp, dir.getAbsolutePath() + "/" + filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                    dir.getAbsolutePath() + ".txt", true));
            if(!intro){
                writer.newLine();
                writer.write("# color images");
                writer.newLine();
                writer.write("# file: 'sample'");
                writer.newLine();
                writer.write("# timestamp filename");
            }
            writer.newLine();
            writer.write(written);
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void saveFrame(Bitmap bmp, String filename) throws IOException {
        /*mPixelBuf.rewind();
        GLES20.glReadPixels(0, 0, mSurfaceWidth, mSurfaceHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                mPixelBuf);*/

        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filename));
            /*Bitmap bmp = Bitmap.createBitmap(mSurfaceWidth, mSurfaceHeight, Bitmap.Config.ARGB_8888);
            mPixelBuf.rewind();
            bmp.copyPixelsFromBuffer(mPixelBuf);*/
            bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
            //bmp.recycle();
        } finally {
            if (bos != null) bos.close();
        }
    }


    public Bitmap getBitmap(byte[] bytes) {
        //mPixelBuf.rewind();
        //GLES20.glReadPixels(0, 0, mSurfaceWidth, mSurfaceHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
        //        mPixelBuf);

        //BufferedOutputStream bos = null;
        //mPixelBuf.rewind();
        //byte[] bytes = new byte[mPixelBuf.capacity()];
        //mPixelBuf.get(bytes);
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        //return answer;
        return Bitmap.createScaledBitmap(bmp, 640 * bmp.getWidth()/bmp.getHeight(), 640, true);

    }

    private static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        return out.toByteArray();
    }

    private void startCameraRecording() {
        if (cameraDevice == null) return;
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        //closePreviewSession();
        if (null == cameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            //closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_ZERO_SHUTTER_LAG);
            List<Surface> surfaces = new ArrayList<>();


            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            //Set up Surface for SLAM
            Surface slamOutputSurface = mImageReader.getSurface();
            surfaces.add(slamOutputSurface);
            mPreviewBuilder.addTarget(slamOutputSurface);


            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    mPreviewSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, new Handler(Looper.getMainLooper()));
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        nextVideoAbsolutePath = null;
        //startPreview();
    }

    private void openCamera(int tvWidth, int tvHeight) {
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            assert manager != null;
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            //Largest available size
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            Point displaySize = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
            int displayWidth = displaySize.x;
            int displayHeight = displaySize.y;

            //mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), tvWidth, tvHeight, displayWidth, displayHeight, largest);
            mPreviewSize = largest;
            //mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());

            //Suppose this value is obtained from Step 2.
            //I simply test here by hardcoding a 3:4 aspect ratio, where my phone has a thinner aspect ratio.
            float cameraAspectRatio = (float) largest.getHeight() / largest.getWidth();

            //Preparation
            int finalWidth = displayWidth;
            int finalHeight = displayHeight;
            int widthDifference = 0;
            int heightDifference = 0;
            float screenAspectRatio = (float) displayWidth / displayHeight;

            //Determines whether we crop width or crop height
            if (screenAspectRatio > cameraAspectRatio) { //Keep width crop height
                finalHeight = (int) (displayWidth / cameraAspectRatio);
                heightDifference = finalHeight - displayHeight;
            } else { //Keep height crop width
                finalWidth = (int) (displayHeight * cameraAspectRatio);
                widthDifference = finalWidth - displayWidth;
            }

            //Apply the result to the Preview
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTextureView.getLayoutParams();
            lp.width = finalWidth;
            lp.height = finalHeight;
            //Below 2 lines are to center the preview, since cropping default occurs at the right and bottom
            lp.leftMargin = - (widthDifference / 2);
            lp.topMargin = - (heightDifference / 2);
            mTextureView.setLayoutParams(lp);
            mTextureView.setAspectRatio(finalWidth, finalHeight);

            //check real-time permissions, this should be false on the first time the camera is ever opened
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "request permissions failed");
                mCameraOpenCloseLock.release();
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != cameraDevice) {
                cameraDevice.close();
                cameraDevice = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getActivity(), "Can't use camera without permission", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if (mTextureView.isAvailable())
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        else
            mTextureView.setSurfaceTextureListener(textureListener);
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * The camera preview runs on this thread
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    public interface OnFrameRenderedListener {
        void onFrameRendered(TimeFramePair<Bitmap, Double> timeFramePair);
    }

    public void setOnFrameRenderedListener(OnFrameRenderedListener listener, Handler handler) {
        this.mFrameRenderedListener = listener;
        this.mFrameRenderedListenerHandler = handler;
    }

}
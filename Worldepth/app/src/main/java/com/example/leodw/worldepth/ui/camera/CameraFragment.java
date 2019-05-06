package com.example.leodw.worldepth.ui.camera;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.slam.ReconVM;
import com.example.leodw.worldepth.slam.Slam;

import java.util.ArrayList;
import java.util.Arrays;
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

    private Renderer mRenderer;
    private Slam mSlam;
    private SurfaceTexture mSlamOutputSurface;

    private Button captureBtn;
    private ImageView mMapButton;
    private AutoFitTextureView mTextureView;

    private boolean mRecordingState;

    private Integer sensorOrientation;

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

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
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
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
        mReconVM = ViewModelProviders.of(getActivity()).get(ReconVM.class);
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.textureView);
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
                        mRenderer.stopRenderThread();
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
        BlockingQueue<TimeFramePair<Bitmap, Long>> q = new LinkedBlockingQueue<TimeFramePair<Bitmap, Long>>();
        //Poison pill to signal end of queue.
        mRenderer = new Renderer(mReconVM.getPoisonPill());
        mRenderer.setOnSurfaceTextureReadyListener(texture -> {
            mSlamOutputSurface = texture;
            startCameraRecording();
        }, new Handler(Looper.getMainLooper()));
        mRenderer.setOnFrameRenderedListener((timeFramePair) -> mReconVM.sendFrame(timeFramePair), new Handler(Looper.getMainLooper()));
        mRenderer.start(mTextureView.getWidth(), mTextureView.getHeight());
        mReconVM.setReconProgress(ReconVM.ReconProgress.SLAM);
    }

    private void startCameraRecording() {
        if (cameraDevice == null) return;
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        closePreviewSession();
        if (null == cameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }
        try {
            //closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            //Set up Surface for SLAM
            mSlamOutputSurface.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Surface slamOutputSurface = new Surface(mSlamOutputSurface);
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
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        nextVideoAbsolutePath = null;
        startPreview();
    }

    private void openCamera(int width, int height) {
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            //Largest available size
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            // Find out if we need to swap dimension to get the preview size relative to sensor
            // coordinate.
            int displayRotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
            sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            boolean swappedDimensions = false;
            switch (displayRotation) {
                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    if (sensorOrientation == 90 || sensorOrientation == 270) {
                        swappedDimensions = true;
                    }
                    break;
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    if (sensorOrientation == 0 || sensorOrientation == 180) {
                        swappedDimensions = true;
                    }
                    break;
                default:
                    Log.e(TAG, "Display rotation is invalid: " + displayRotation);
            }
            Point displaySize = new Point();
            getActivity().getWindowManager().getDefaultDisplay().getSize(displaySize);
            int rotatedPreviewWidth = width;
            int rotatedPreviewHeight = height;
            int maxPreviewWidth = displaySize.x;
            int maxPreviewHeight = displaySize.y;

            if (swappedDimensions) {
                rotatedPreviewWidth = height;
                rotatedPreviewHeight = width;
                maxPreviewWidth = displaySize.y;
                maxPreviewHeight = displaySize.x;
            }

            if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                maxPreviewWidth = MAX_PREVIEW_WIDTH;
            }

            if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                maxPreviewHeight = MAX_PREVIEW_HEIGHT;
            }

            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth, maxPreviewHeight, largest);


            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            configureTransform(width, height);
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

    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize) {
            return;
        }
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }

}
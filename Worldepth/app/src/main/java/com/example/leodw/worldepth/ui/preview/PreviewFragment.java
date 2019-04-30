package com.example.leodw.worldepth.ui.preview;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.leodw.worldepth.ui.preview.obj.ObjModel;
import com.example.leodw.worldepth.ui.preview.ply.PlyModel;
import com.example.leodw.worldepth.ui.preview.stl.StlModel;
import com.example.leodw.worldepth.ui.preview.util.Util;
import com.example.leodw.worldepth.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.navigation.Navigation;


public class PreviewFragment extends Fragment {

    private static final String TAG = "PreviewFragment";

    private ViewerViewModel mViewerViewModel;

    private static final int READ_PERMISSION_REQUEST = 100;
    private static final int OPEN_DOCUMENT_REQUEST = 101;

    private static final String[] SAMPLE_MODELS
            = new String[]{"Dragon2.stl", "Roadster.stl"};
    private static int sampleModelIndex;

    private ModelViewerApplication app;
    @Nullable
    private ModelSurfaceView modelView;
    private ViewGroup containerView;

    private Button mLoadSample;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.viewer_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        app = ModelViewerApplication.getInstance();

        containerView = view.findViewById(R.id.container_view);
        if (getActivity().getIntent().getData() != null && savedInstanceState == null) {
            beginLoadModel(getActivity().getIntent().getData());
        }
        mLoadSample = view.findViewById(R.id.loadSampleButton);
        mLoadSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSampleModel();
            }
        });
        Button backToCamera = view.findViewById(R.id.viewerBackToCamera);
        backToCamera.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_viewerFragment_to_cameraFragment));
        Button nextButton = view.findViewById(R.id.postButton);
        nextButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_viewerFragment_to_locationFragment));
    }

    @Override
    public void onStart() {
        super.onStart();
        createNewModelView(app.getCurrentModel());
        if (app.getCurrentModel() != null) {
            getActivity().setTitle(app.getCurrentModel().getTitle());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (modelView != null) {
            modelView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (modelView != null) {
            modelView.onResume();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_PERMISSION_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    beginOpenModel();
                } else {
                    Toast.makeText(getActivity(), R.string.read_permission_failed, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == OPEN_DOCUMENT_REQUEST && resultCode == getActivity().RESULT_OK && resultData.getData() != null) {
            Uri uri = resultData.getData();
            getActivity().grantUriPermission(getActivity().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            beginLoadModel(uri);
        }
    }

    private void checkReadPermissionThenOpen() {
        if ((ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_PERMISSION_REQUEST);
        } else {
            beginOpenModel();
        }
    }

    private void beginOpenModel() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_DOCUMENT_REQUEST);
    }

    private void beginLoadModel(@NonNull Uri uri) {
        new ModelLoadTask().execute(uri);
    }

    private void createNewModelView(@Nullable Model model) {
        if (modelView != null) {
            containerView.removeView(modelView);
        }
        ModelViewerApplication.getInstance().setCurrentModel(model);
        modelView = new ModelSurfaceView(getActivity(), model);
        containerView.addView(modelView, 0);
    }

    private class ModelLoadTask extends AsyncTask<Uri, Integer, Model> {
        protected Model doInBackground(Uri... file) {
            InputStream stream = null;
            try {
                Uri uri = file[0];
                ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
                String fileName = getFileName(cr, uri);
                stream = cr.openInputStream(uri);
                if (stream != null) {
                    Model model;
                    if (!TextUtils.isEmpty(fileName)) {
                        if (fileName.toLowerCase().endsWith(".stl")) {
                            model = new StlModel(stream);
                        } else if (fileName.toLowerCase().endsWith(".obj")) {
                            model = new ObjModel(stream);
                        } else if (fileName.toLowerCase().endsWith(".ply")) {
                            model = new PlyModel(stream);
                        } else {
                            // assume STL type
                            model = new StlModel(stream);
                        }
                        model.setTitle(fileName);
                    } else {
                        // assume STL type
                        model = new StlModel(stream);
                    }
                    return model;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Util.closeSilently(stream);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Model model) {
            if (getActivity().isDestroyed()) {
                return;
            }
            if (model != null) {
                setCurrentModel(model);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.open_model_error, Toast.LENGTH_SHORT).show();
            }
        }

        @Nullable
        private String getFileName(@NonNull ContentResolver cr, @NonNull Uri uri) {
            if ("content".equals(uri.getScheme())) {
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                Cursor metaCursor = ContentResolverCompat.query(cr, uri, projection, null, null, null, null);
                if (metaCursor != null) {
                    try {
                        if (metaCursor.moveToFirst()) {
                            return metaCursor.getString(0);
                        }
                    } finally {
                        metaCursor.close();
                    }
                }
            }
            return uri.getLastPathSegment();
        }
    }

    private void setCurrentModel(@NonNull Model model) {
        createNewModelView(model);
        Toast.makeText(getActivity().getApplicationContext(), R.string.open_model_success, Toast.LENGTH_SHORT).show();
        getActivity().setTitle(model.getTitle());
    }


    private void loadSampleModel() {
        try {
            File file = new File(getContext().getFilesDir().getAbsolutePath() + "/", "SLAM.ply");
            Log.d(TAG, file.getName());
            InputStream stream = new FileInputStream(file);
            setCurrentModel(new PlyModel(stream));
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

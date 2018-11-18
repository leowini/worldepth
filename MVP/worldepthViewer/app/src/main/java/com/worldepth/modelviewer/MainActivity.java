package com.worldepth.modelviewer;

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
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.worldepth.modelviewer.obj.ObjModel;
import com.worldepth.modelviewer.ply.PlyModel;
import com.worldepth.modelviewer.stl.StlModel;
import com.worldepth.modelviewer.util.Util;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private static final int READ_PERMISSION_REQUEST = 100;
    private static final int OPEN_DOCUMENT_REQUEST = 101;

    private static final String[] SAMPLE_MODELS
            = new String[] { "Dragon2.stl", "Roadster.stl"};
    private static int sampleModelIndex;

    private ModelViewerApplication app;
    @Nullable private ModelSurfaceView modelView;
    private ViewGroup containerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = ModelViewerApplication.getInstance();

        containerView = findViewById(R.id.container_view);
        if (getIntent().getData() != null && savedInstanceState == null) {
            beginLoadModel(getIntent().getData());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        createNewModelView(app.getCurrentModel());
        if (app.getCurrentModel() != null) {
            setTitle(app.getCurrentModel().getTitle());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (modelView != null) {
            modelView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (modelView != null) {
            modelView.onResume();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_open_model:
                checkReadPermissionThenOpen();
                return true;
            case R.id.menu_load_sample:
                loadSampleModel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                    Toast.makeText(this, R.string.read_permission_failed, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == OPEN_DOCUMENT_REQUEST && resultCode == RESULT_OK && resultData.getData() != null) {
            Uri uri = resultData.getData();
            grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            beginLoadModel(uri);
        }
    }

    private void checkReadPermissionThenOpen() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
        modelView = new ModelSurfaceView(this, model);
        containerView.addView(modelView, 0);
    }

    private class ModelLoadTask extends AsyncTask<Uri, Integer, Model> {
        protected Model doInBackground(Uri... file) {
            InputStream stream = null;
            try {
                Uri uri = file[0];
                ContentResolver cr = getApplicationContext().getContentResolver();
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
            if (isDestroyed()) {
                return;
            }
            if (model != null) {
                setCurrentModel(model);
            } else {
                Toast.makeText(getApplicationContext(), R.string.open_model_error, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(), R.string.open_model_success, Toast.LENGTH_SHORT).show();
        setTitle(model.getTitle());
    }


    private void loadSampleModel() {
        try {
            InputStream stream = getApplicationContext().getAssets()
                    .open(SAMPLE_MODELS[sampleModelIndex++ % SAMPLE_MODELS.length]);
            setCurrentModel(new StlModel(stream));
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

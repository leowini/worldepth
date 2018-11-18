package com.worldepth.modelviewer;

import android.app.Application;
import android.support.annotation.Nullable;

public class ModelViewerApplication extends Application
{
    private static ModelViewerApplication INSTANCE;

    // Store the current model globally, so that we don't have to re-decode it upon
    // relaunching the main or VR activities.
    // TODO: handle this a bit better.
    @Nullable private Model currentModel;

    public static ModelViewerApplication getInstance() {
        return INSTANCE;
    }

    @Nullable
    public Model getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(@Nullable Model model) {
        currentModel = model;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        INSTANCE = this;
    }
}

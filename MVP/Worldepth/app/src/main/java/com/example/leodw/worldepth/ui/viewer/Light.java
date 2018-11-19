package com.example.leodw.worldepth.ui.viewer;

import android.opengl.Matrix;
import android.support.annotation.NonNull;

public class Light {

    @NonNull private float[] lightPosInWorldSpace;
    private final float[] lightPosInEyeSpace = new float[4];
    private float[] ambientColor = new float[] {0.1f, 0.1f, 0.4f};
    private float[] diffuseColor = new float[] {1.0f, 1.0f, 1.0f};
    private float[] specularColor = new float[] {1.0f, 1.0f, 1.0f};

    public Light(@NonNull float[] position) {
        this.lightPosInWorldSpace = position;
    }

    public void setPosition(@NonNull float[] position) {
        this.lightPosInWorldSpace = position;
    }

    public void setAmbientColor(@NonNull float[] color) {
        ambientColor = color;
    }

    public float[] getAmbientColor() {
        return ambientColor;
    }

    public void setDiffuseColor(@NonNull float[] color) {
        diffuseColor = color;
    }

    public float[] getDiffuseColor() {
        return diffuseColor;
    }

    public void setSpecularColor(@NonNull float[] color) {
        specularColor = color;
    }

    public float[] getSpecularColor() {
        return specularColor;
    }

    public void applyViewMatrix(@NonNull float[] viewMatrix) {
        Matrix.multiplyMV(lightPosInEyeSpace, 0, viewMatrix, 0, lightPosInWorldSpace, 0);
    }

    public float[] getPositionInEyeSpace() {
        return lightPosInEyeSpace;
    }

}

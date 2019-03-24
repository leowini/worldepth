package com.example.leodw.worldepth.ui.preview;

import android.opengl.Matrix;
import android.support.annotation.NonNull;

public abstract class Model {
    // Center of mass to be populated by subclasses
    protected float centerMassX;
    protected float centerMassY;
    protected float centerMassZ;
    protected float floorOffset;

    @NonNull private String title;

    protected int glProgram = -1;
    protected float[] modelMatrix = new float[16];
    protected float[] mvMatrix = new float[16];
    protected float[] mvpMatrix = new float[16];

    protected float maxX;
    protected float maxY;
    protected float maxZ;
    protected float minX;
    protected float minY;
    protected float minZ;

    public Model() {
        maxX = Float.MIN_VALUE;
        maxY = Float.MIN_VALUE;
        maxZ = Float.MIN_VALUE;
        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;
        title = "";
    }

    public void init(float boundSize) {
        initModelMatrix(boundSize);
    }

    @NonNull public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    protected void initModelMatrix(float boundSize) {
        initModelMatrix(boundSize, 0.0f, 0.0f, 0.0f);
    }

    protected void initModelMatrix(float boundSize, float rotateX, float rotateY, float rotateZ) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, rotateX, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotateY, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotateZ, 0.0f, 0.0f, 1.0f);
        scaleModelMatrixToBounds(boundSize);
        Matrix.translateM(modelMatrix, 0, -centerMassX, -centerMassY, -centerMassZ);
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }

    public float getFloorOffset() {
        return floorOffset;
    }

    abstract public void draw(float[] viewMatrix, float[] projectionMatrix, @NonNull Light light);

    protected void adjustMaxMin(float x, float y, float z) {
        if (x > maxX) {
            maxX = x;
        }
        if (y > maxY) {
            maxY = y;
        }
        if (z > maxZ) {
            maxZ = z;
        }
        if (x < minX) {
            minX = x;
        }
        if (y < minY) {
            minY = y;
        }
        if (z < minZ) {
            minZ = z;
        }
    }

    protected float getBoundScale(float boundSize) {
        float scaleX = (maxX - minX) / boundSize;
        float scaleY = (maxY - minY) / boundSize;
        float scaleZ = (maxZ - minZ) / boundSize;
        float scale = scaleX;
        if (scaleY > scale) {
            scale = scaleY;
        }
        if (scaleZ > scale) {
            scale = scaleZ;
        }
        return scale;
    }

    private void scaleModelMatrixToBounds(float boundSize) {
        float scale = getBoundScale(boundSize);
        if (scale != 0f) {
            scale = 1f / scale;
            Matrix.scaleM(modelMatrix, 0, scale, scale, scale);
        }
    }
}

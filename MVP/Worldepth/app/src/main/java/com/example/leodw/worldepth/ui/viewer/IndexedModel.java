package com.example.leodw.worldepth.ui.viewer;

import android.opengl.GLES20;
import java.nio.IntBuffer;

public class IndexedModel extends ArrayModel {
    protected static final int BYTES_PER_INT = 4;

    protected IntBuffer indexBuffer;
    protected int indexCount;

    @Override
    protected void drawFunc() {
        if (indexBuffer == null || indexCount == 0) {
            return;
        }
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_INT, indexBuffer);
    }
}

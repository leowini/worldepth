package com.example.leodw.worldepth.ui.preview;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.preview.util.Util;
import java.nio.FloatBuffer;

public class ArrayModel extends Model {
    protected static final int BYTES_PER_FLOAT = 4;
    protected static final int COORDS_PER_VERTEX = 3;
    protected static final int VERTEX_STRIDE = COORDS_PER_VERTEX * BYTES_PER_FLOAT;
    protected static final int INPUT_BUFFER_SIZE = 0x10000;

    protected int vertexCount;
    @Nullable protected FloatBuffer vertexBuffer;
    @Nullable protected FloatBuffer normalBuffer;

    @Override
    public void init(float boundSize) {
        if (GLES20.glIsProgram(glProgram)) {
            GLES20.glDeleteProgram(glProgram);
            glProgram = -1;
        }
        glProgram = Util.compileProgram(R.raw.model_vertex, R.raw.single_light_fragment,
                new String[] {"a_Position", "a_Normal"});
        super.init(boundSize);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix, @NonNull Light light) {
        if (vertexBuffer == null || normalBuffer == null) {
            return;
        }
        GLES20.glUseProgram(glProgram);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(glProgram, "u_MVP");
        int positionHandle = GLES20.glGetAttribLocation(glProgram, "a_Position");
        int normalHandle = GLES20.glGetAttribLocation(glProgram, "a_Normal");
        int lightPosHandle = GLES20.glGetUniformLocation(glProgram, "u_LightPos");
        int ambientColorHandle = GLES20.glGetUniformLocation(glProgram, "u_ambientColor");
        int diffuseColorHandle = GLES20.glGetUniformLocation(glProgram, "u_diffuseColor");
        int specularColorHandle = GLES20.glGetUniformLocation(glProgram, "u_specularColor");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, vertexBuffer);

        GLES20.glEnableVertexAttribArray(normalHandle);
        GLES20.glVertexAttribPointer(normalHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, normalBuffer);

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUniform3fv(lightPosHandle, 1, light.getPositionInEyeSpace(), 0);
        GLES20.glUniform3fv(ambientColorHandle, 1, light.getAmbientColor(), 0);
        GLES20.glUniform3fv(diffuseColorHandle, 1, light.getDiffuseColor(), 0);
        GLES20.glUniform3fv(specularColorHandle, 1, light.getSpecularColor(), 0);

        drawFunc();

        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    protected void drawFunc() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }
}

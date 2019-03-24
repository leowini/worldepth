package com.example.leodw.worldepth.ui.preview.ply;

import android.icu.lang.UCharacter;
import android.icu.lang.UCharacterCategory;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.preview.IndexedModel;
import com.example.leodw.worldepth.ui.preview.Light;
import com.example.leodw.worldepth.ui.preview.util.Util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static com.example.leodw.worldepth.ui.preview.util.Util.readIntLe;

public class PlyModel extends IndexedModel {

    private final float[] pointColor = new float[]{1.0f, 1.0f, 1.0f};
    private int faceCount;

    public PlyModel(@NonNull InputStream inputStream) throws IOException {
        super();
        BufferedInputStream stream = new BufferedInputStream(inputStream, INPUT_BUFFER_SIZE);
        readText(stream);
        if (vertexCount <= 0 || vertexBuffer == null) {
            throw new IOException("Invalid model.");
        }
    }

    @Override
    public void init(float boundSize) {
        if (GLES20.glIsProgram(glProgram)) {
            GLES20.glDeleteProgram(glProgram);
            glProgram = -1;
        }
        glProgram = Util.compileProgram(R.raw.point_cloud_vertex, R.raw.single_color_fragment,
                new String[]{"a_Position"});
        initModelMatrix(boundSize);
    }

    @Override
    public void initModelMatrix(float boundSize) {
        final float yRotation = 180f;
        initModelMatrix(boundSize, 0.0f, yRotation, 0.0f);
        float scale = getBoundScale(boundSize);
        if (scale == 0.0f) {
            scale = 1.0f;
        }
        floorOffset = (minY - centerMassY) / scale;
    }

    private void readText(@NonNull BufferedInputStream stream) throws IOException {
        List<Float> vertices = new ArrayList<>();
        List<Integer> faces = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream), INPUT_BUFFER_SIZE);
        String line;
        String[] lineArr;

        /*
        TODO:
        This is currently pretty limited. We expect the header to contain a line of
        "element vertex nnn", and the list of vertices to follow immediately after the
        header, and each vertex to have the format "x, y, z, ...".
        */

        stream.mark(0x100000);
        boolean isBinary = false;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("format ")) {
                if (line.contains("binary")) {
                    isBinary = true;
                }
            } else if (line.startsWith("element vertex")) {
                lineArr = line.split(" ");
                vertexCount = Integer.parseInt(lineArr[2]);
            } else if (line.startsWith("element face")) {
                lineArr = line.split(" ");
                faceCount = Integer.parseInt(lineArr[2]);
                System.out.println(faceCount);
            } else if (line.startsWith("end_header")) {
                break;
            }
        }

        if (vertexCount <= 0) {
            return;
        }

        if (isBinary) {
            stream.reset();
            System.out.println("binary");
            readVerticesBinary(vertices, faces, stream);
        } else {
            System.out.println("text");
            readVerticesText(vertices, reader);
            readFacesText(faces, reader);
        }

        float[] floatArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            floatArray[i] = vertices.get(i);
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(floatArray.length * BYTES_PER_FLOAT);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(floatArray);
        vertexBuffer.position(0);

        int[] intArray = new int[faces.size()];
        for (int i = 0; i < faces.size(); i++) {
            intArray[i] = faces.get(i);
        }
        ByteBuffer fbb = ByteBuffer.allocateDirect(intArray.length * BYTES_PER_INT);
        fbb.order(ByteOrder.nativeOrder());
        faceIndexBuffer = fbb.asIntBuffer();
        faceIndexBuffer.put(intArray);
        faceIndexBuffer.position(0);
    }

    private void readVerticesText(List<Float> vertices, BufferedReader reader) throws IOException {
        String[] lineArr;
        float x, y, z;

        double centerMassX = 0.0;
        double centerMassY = 0.0;
        double centerMassZ = 0.0;

        for (int i = 0; i < vertexCount; i++) {
            lineArr = reader.readLine().trim().split(" ");
            x = Float.parseFloat(lineArr[0]);
            y = Float.parseFloat(lineArr[1]);
            z = Float.parseFloat(lineArr[2]);
            vertices.add(x);
            vertices.add(y);
            vertices.add(z);

            adjustMaxMin(x, y, z);
            centerMassX += x;
            centerMassY += y;
            centerMassZ += z;
        }

        this.centerMassX = (float) (centerMassX / vertexCount);
        this.centerMassY = (float) (centerMassY / vertexCount);
        this.centerMassZ = (float) (centerMassZ / vertexCount);
    }

    private void readVerticesBinary(List<Float> vertices, List<Integer> faces, @NonNull BufferedInputStream stream) throws IOException {
        byte[] tempBytes = new byte[0x1000];
        stream.mark(1);
        stream.read(tempBytes);
        String tempStr = new String(tempBytes);
        int contentsPos = tempStr.indexOf("end_header") + 11;
        stream.reset();
        stream.skip(contentsPos);

        float x, y, z;

        double centerMassX = 0.0;
        double centerMassY = 0.0;
        double centerMassZ = 0.0;

        for (int i = 0; i < vertexCount; i++) {
            stream.read(tempBytes, 0, BYTES_PER_FLOAT * 3);
            x = Float.intBitsToFloat(readIntLe(tempBytes, 0));
            y = Float.intBitsToFloat(readIntLe(tempBytes, BYTES_PER_FLOAT));
            z = Float.intBitsToFloat(readIntLe(tempBytes, BYTES_PER_FLOAT * 2));
            vertices.add(x);
            vertices.add(y);
            vertices.add(z);

            adjustMaxMin(x, y, z);
            centerMassX += x;
            centerMassY += y;
            centerMassZ += z;
        }

        this.centerMassX = (float) (centerMassX / vertexCount);
        this.centerMassY = (float) (centerMassY / vertexCount);
        this.centerMassZ = (float) (centerMassZ / vertexCount);

        for (int i = 0; i < faceCount; i++) {
            stream.read(tempBytes, 0, 1);
            byte[] temp = tempBytes;
            int length = (tempBytes[0] & 0xff);
            stream.read(tempBytes, 0, BYTES_PER_INT * length);
            for (int j = 0; j < length; j++) {
                faces.add(readIntLe(tempBytes, BYTES_PER_INT * j));
            }
        }
    }

    private void readFacesText(List<Integer> faces, BufferedReader reader) throws IOException {
        String[] lineArr;

        for (int i = 0; i < faceCount; i++) {
            lineArr = reader.readLine().trim().split(" ");
            for (int j = 1; j < lineArr.length; j++) {
                faces.add(Integer.parseInt(lineArr[j]));
            }
        }
    }

    @Override
    public void draw(float[] viewMatrix, float[] projectionMatrix, @NonNull Light light) {
        if (vertexBuffer == null) {
            return;
        }
        GLES20.glUseProgram(glProgram);

        int mvpMatrixHandle = GLES20.glGetUniformLocation(glProgram, "u_MVP");
        int positionHandle = GLES20.glGetAttribLocation(glProgram, "a_Position");
        int pointThicknessHandle = GLES20.glGetUniformLocation(glProgram, "u_PointThickness");
        int ambientColorHandle = GLES20.glGetUniformLocation(glProgram, "u_ambientColor");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, vertexBuffer);

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUniform1f(pointThicknessHandle, 3.0f);
        GLES20.glUniform3fv(ambientColorHandle, 1, pointColor, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, faceCount * 3, GLES20.GL_UNSIGNED_INT, faceIndexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}

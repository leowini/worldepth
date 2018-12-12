package com.example.leodw.worldepth.ui.viewer.text;

import android.opengl.GLES20;
import android.support.annotation.NonNull;

import com.example.leodw.worldepth.R;
import com.example.leodw.worldepth.ui.viewer.IndexedModel;
import com.example.leodw.worldepth.ui.viewer.util.Util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static com.example.leodw.worldepth.ui.viewer.util.Util.readIntLe;

public class TxtModel extends IndexedModel {

    private final float[] pointColor = new float[] { 1.0f, 1.0f, 1.0f };

    public TxtModel(@NonNull InputStream inputStream) throws IOException {
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
                new String[] {"a_Position"});
        initModelMatrix(boundSize);
    }

    @Override
    public void initModelMatrix(float boundSize) {
        final float yRotation = 180f;
        initModelMatrix(boundSize, 0.0f, yRotation, 0.0f);
        float scale = getBoundScale(boundSize);
        if (scale == 0.0f) { scale = 1.0f; }
        floorOffset = (minY - centerMassY) / scale;
    }


    private void readText(@NonNull BufferedInputStream stream) throws IOException {
        List<Float> vertices = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream), INPUT_BUFFER_SIZE);
        String line;
        String[] lineArr;



        stream.mark(0x100000);
        while ((line = reader.readLine()) != null) {
            line = line.trim();
        }

        if (vertexCount <= 0) {
            return;
        }


            readVerticesText(vertices, reader);


        float[] floatArray = new float[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            floatArray[i] = vertices.get(i);
        }
        ByteBuffer vbb = ByteBuffer.allocateDirect(floatArray.length * BYTES_PER_FLOAT);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(floatArray);
        vertexBuffer.position(0);
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

        this.centerMassX = (float)(centerMassX / vertexCount);
        this.centerMassY = (float)(centerMassY / vertexCount);
        this.centerMassZ = (float)(centerMassZ / vertexCount);
    }


}

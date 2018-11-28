package com.example.leodw.worldepth.slam;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.leodw.worldepth.ui.camera.Renderer;

import java.io.ByteArrayOutputStream;

public class Slam implements Renderer.FrameListener {
    public static final String TAG = "Slam";

    public native void passImage(byte[] img);

    @Override
    public void sendFrameToSlam(Bitmap frame) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        frame.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        passImage(byteArray);
    }

}
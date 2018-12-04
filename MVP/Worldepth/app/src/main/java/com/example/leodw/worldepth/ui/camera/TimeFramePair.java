package com.example.leodw.worldepth.ui.camera;

public class TimeFramePair<Bitmap,Long> {

    private final Bitmap left;
    private final Long right;

    public TimeFramePair(Bitmap left, Long right) {
        this.left = left;
        this.right = right;
    }

    public Bitmap getFrame() {
        return left;
    }

    public Long getTime() {
        return right;
    }

}

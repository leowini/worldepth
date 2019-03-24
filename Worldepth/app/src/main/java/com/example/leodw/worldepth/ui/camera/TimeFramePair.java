package com.example.leodw.worldepth.ui.camera;

public class TimeFramePair<L,R> {

    private static final String TAG = "TimeFramePair";

    private final L left;
    private final R right;

    public TimeFramePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getFrame() {
        return left;
    }

    public R getTime() {
        return right;
    }

}

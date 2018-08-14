package com.hipmunk.shinytext;

public class ChippyModel {
    public int mScore = 0;
    public float positionX, positionY, mAccelerationY;
    public boolean didTap = false;
    public final int mWidth = 84, mHeight = 38;

    public Point[] trail = new Point[5];

    public ChippyModel() {
        reset();
    }

    public void update() {
        if (didTap) {
            didTap = false;
            mAccelerationY += .7;
        }
        mAccelerationY -= .03;
        if (mAccelerationY < -1.2) mAccelerationY = -1.2f;
        if (mAccelerationY > 1.4) mAccelerationY = 1.4f;
        positionY -= mAccelerationY;

        if (positionY > 98) {
            reset();
        } else if (positionY < 2) {
            reset();
        }
        updateTrail(positionY);
        mScore++;
    }

    private void updateTrail(final float y) {
        for (int i = trail.length -1; i > 0; i--) {
            trail[i].positionY = trail[i-1].positionY;
        }
        trail[0].positionY = y;
    }

    public void reset() {
        mScore = 0;
        mAccelerationY = .6f;
        positionX = 10;
        positionY = 60;
        trail[0] = new Point(positionX + 1, positionY);
        trail[1] = new Point(positionX - 1, positionY);
        trail[2] = new Point(positionX - 3, positionY);
        trail[3] = new Point(positionX - 6, positionY);
        trail[4] = new Point(positionX - 9, positionY);
    }
}

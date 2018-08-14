package com.hipmunk.shinytext;

public class Enemy {

    public final int DEFAULT_POSITION_VALUE = -900;
    public float positionX, positionY, sizeX, sizeY;
    public float mAccelerationX, mAccelerationY;
    public float mMaxAccelerationX, mMaxAccelerationY, mMinAccelerationX, mMinAccelerationY;

    public Enemy() {
        reset();
    }

    public void reset() {
        positionX = DEFAULT_POSITION_VALUE;
        positionY = DEFAULT_POSITION_VALUE;
        mAccelerationX = 0;
        mAccelerationY = 0;
        mMaxAccelerationX = 0;
        mMaxAccelerationY = 0;
        sizeX = 0;
        sizeY = 0;
    }

    public void update() {
        //TODO: check if acceleration is outside bounds and then reverse acceleration
        // if (mAccelerationX > mMaxAccelerationX)

    }

    public boolean collidesWithPlayer(final ChippyModel chippy) {
        float chippyRight = chippy.positionX + chippy.mWidth;
        float chippyLeft = chippy.positionX;
        float chippyTop = chippy.positionY;
        float chippyBottom = chippy.positionY + chippy.mHeight;
        if (chippyLeft < positionX + sizeX && chippyRight > positionX &&
                chippyTop < positionY + sizeY && chippyBottom > positionY) {
            return true;
        }
        return false;
    }
}

package com.hipmunk.shinytext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class ChippyActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ChippyView(this));
    }



    public class ChippyView extends View {

        final Bitmap mChippyPlane;
        final ChippyModel mModel;
        final Paint mTrailPaint, mBackgroundPaint, mScorePaint;

        public ChippyView(final Context context) {
            super(context);
            mChippyPlane = BitmapFactory.decodeResource(getResources(), R.drawable.chippy_plane);
            mModel = new ChippyModel();
            mTrailPaint = new Paint();
//            mTrailPaint.setARGB(180, 225, 225, 225);
            mTrailPaint.setARGB(230, 85, 235, 185);
            mTrailPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setARGB(255, 132, 164, 234);
            mBackgroundPaint.setShader(new LinearGradient(0, 100, 0, 2400,
                    Color.rgb(40, 100, 255),
                    Color.rgb(210, 210, 245),
                    Shader.TileMode.CLAMP));

            mScorePaint = new Paint();
            mScorePaint.setARGB(255, 244, 232, 66);
            mScorePaint.setTextSize(64);
            mScorePaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
        }

        @Override
        public void draw(final Canvas canvas) {
            super.draw(canvas);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundPaint);
            if (mChippyPlane != null && mModel != null) {
                // drawing chippy
                final float xPos = mModel.positionX * .01f * getWidth();
                final float yPos = mModel.positionY * .01f * getHeight();
                canvas.drawBitmap(mChippyPlane, xPos, yPos, null);

                // drawing trail
                if (mTrailPaint != null) {
                    mShouldOffsetXTrail = !mShouldOffsetXTrail;
                    int trailIndex = 0;
                    for (Point p : mModel.trail) {
                        if (p == null) break;
                        final int yOffsetCase = trailIndex % 3;
                        final int yOffset;
                        switch (yOffsetCase) {
                            case 1:
                                yOffset = 4;
                                break;
                            case 2:
                                yOffset = 0;
                                break;
                            default:
                                yOffset = -4;
                                break;
                        }

                        final float xTrail = p.positionX * .01f * getWidth();
                        final float yTrail = p.positionY * .01f * getHeight();


                        mTrailPaint.setTextSize(20 + (int)(2*(trailIndex++)));
                        canvas.drawText("$",
                                xTrail + mOffsetX,
                                yTrail + mChippyPlane.getHeight() / 2 + yOffset,
//                                4 + (trailIndex++),
                                mTrailPaint);
//                        canvas.drawCircle(xTrail + mOffsetX,
//                                yTrail + mChippyPlane.getHeight() / 2 + yOffset,
//                                4 + (trailIndex++), mTrailPaint);
                    }
                    mOffsetX -= 2f;
                    if (mOffsetX <= -41) mOffsetX += 52;
                }
                if (mScorePaint != null) {
                    canvas.drawText(mModel.mScore + " miles", 20, 100, mScorePaint);
                }
                mModel.update();
            }

            invalidate();
        }
        private boolean mShouldOffsetXTrail = false;
        private float mOffsetX = 0;

        @Override
        public boolean onTouchEvent(final MotionEvent event) {
            System.out.println("Chippy event: " + event.toString());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                System.out.println("Chippy pressed down");
                if (mModel != null) {
                    mModel.didTap = true;
                }
            } else {
                System.out.println("Chippy pressed something else");
            }
            return super.onTouchEvent(event);
        }
    }

    public class ChippyModel {
        public int mScore = 0;
        public float positionX, positionY, mAccelerationY;
        public boolean didTap = false;

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

    public class Point {
        public float positionX, positionY;
        public Point(final float x, final float y) {
            positionX = x;
            positionY = y;
        }
    }
}

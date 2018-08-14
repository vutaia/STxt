package com.hipmunk.shinytext;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class ChippyActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ChippyView(this));
    }

    public class ChippyView extends View {

        // visual objects
        final ChippyModel mModel;
        final Bitmap mBackgroundCloudsBitmap, mChippyPlane, mEnemyPlaneBitmap0, mEnemyPlaneBitmap1, mBossPlane;
        // final Bitmap mForegroundCloudsBitmap;
        final Paint mTrailPaint, mBackgroundPaint, mScorePaint;

        //
        private boolean mShouldOffsetXTrail = false;
        private float mOffsetX = 0;

        final ArrayList<Enemy> mEnemies = new ArrayList<>();
        final Random mRandom = new Random(System.currentTimeMillis());

        public ChippyView(final Context context) {
            super(context);
            mBackgroundCloudsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clouds5);
            // Needs a better fog foreground image
            // mForegroundCloudsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.foreground_fog);
            mChippyPlane = BitmapFactory.decodeResource(getResources(), R.drawable.chippy_plane);
            mEnemyPlaneBitmap0 = BitmapFactory.decodeResource(getResources(), R.drawable.enemy0);
            mEnemyPlaneBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.enemy1);
            mBossPlane = BitmapFactory.decodeResource(getResources(), R.drawable.boss0);
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
            initEnemies();
        }

        private void initEnemies() {
            mEnemies.clear();
            mEnemies.add(new Enemy(120, 30, 14, 5, -1, .1f));
            mEnemies.add(new Enemy(140, 70, 14, 5, -1.2f, 0));
        }

        private void spawnBoss() {
            mEnemies.add(new Enemy(160, 30, 80, 18, -1.8f, .2f));
        }

        private void spawnNewEnemies(final ChippyModel chippy) {
            if (!mEnemies.isEmpty()) {
                if (mEnemies.size() >= 2) {
                    // resetting top enemy
                    final Enemy topEnemy = mEnemies.get(0);
                    topEnemy.positionX = 120 + (mRandom.nextInt(10) * (mRandom.nextBoolean() ? 1 : -1));
                    topEnemy.positionY = 30 + (mRandom.nextInt(15) * (mRandom.nextBoolean() ? 1 : -1));
                    topEnemy.mAccelerationX = -1.1f + (mRandom.nextInt(100) * .001f * (mRandom.nextBoolean() ? 1 : -1));
                    topEnemy.mAccelerationY = (chippy.positionY + 2 - topEnemy.positionY) / topEnemy.positionX;
                            // (chippy.positionY - topEnemy.positionY) * topEnemy.mAccelerationX * .01f;
                            //// * (chippy.positionY < topEnemy.positionY ? 1 : -1);

                    // resetting top enemy
                    final Enemy bottomEnemy = mEnemies.get(1);
                    bottomEnemy.positionX = 120 + (mRandom.nextInt(10) * (mRandom.nextBoolean() ? 1 : -1));
                    bottomEnemy.positionY = 70 + (mRandom.nextInt(15) * (mRandom.nextBoolean() ? 1 : -1));
                    bottomEnemy.mAccelerationX = -1.1f + (mRandom.nextInt(100) * .001f * (mRandom.nextBoolean() ? 1 : -1));
                    bottomEnemy.mAccelerationY = (chippy.positionY + 2 - bottomEnemy.positionY) / bottomEnemy.positionX;
                            // (chippy.positionY - bottomEnemy.positionY) * bottomEnemy.mAccelerationX * .01f;
                            //// * (chippy.positionY < topEnemy.positionY ? -1 : 1);
                }
            }
        }

        private float mBackgroundCloudsOffset = 0, mBackgroundCloudWidth = -1;
        private DecimalFormat mScoreFormatter = new DecimalFormat("#,###");

        @Override
        public void draw(final Canvas canvas) {
            super.draw(canvas);

            // background shit
            if (mBackgroundCloudWidth < 0) {
                mBackgroundCloudWidth = mBackgroundCloudsBitmap.getWidth();
            }
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundPaint);
            canvas.drawBitmap(mBackgroundCloudsBitmap, mBackgroundCloudsOffset, canvas.getHeight() - mBackgroundCloudsBitmap.getHeight(), null);
            mBackgroundCloudsOffset -= 1.6f;
            if (mBackgroundCloudWidth + mBackgroundCloudsOffset < canvas.getWidth()) {
                canvas.drawBitmap(mBackgroundCloudsBitmap, mBackgroundCloudWidth + mBackgroundCloudsOffset, canvas.getHeight() - mBackgroundCloudsBitmap.getHeight(), null);
            }
            if (Math.abs(mBackgroundCloudsOffset) > mBackgroundCloudWidth) {
                mBackgroundCloudsOffset = mBackgroundCloudWidth - Math.abs(mBackgroundCloudsOffset);
            }

            // player shit
            if (mChippyPlane != null && mModel != null) {
                // drawing chippy
                final float xPos = mModel.positionX * .01f * getWidth();
                final float yPos = mModel.positionY * .01f * getHeight();
                final float xSize = mModel.mWidth * .01f * getWidth();
                final float ySize = (mModel.mHeight + 1) * .01f * getHeight();
                // canvas.drawBitmap(mChippyPlane, xPos, yPos, null);
                // drawing with scale
                canvas.drawBitmap(mChippyPlane, null, new Rect((int)xPos, (int)yPos, (int)(xPos + xSize), (int)(yPos + ySize)), null);

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
                                yTrail + ySize / 2 + yOffset,
//                                4 + (trailIndex++),
                                mTrailPaint);
//                        canvas.drawCircle(xTrail + mOffsetX,
//                                yTrail + mChippyPlane.getHeight() / 2 + yOffset,
//                                4 + (trailIndex++), mTrailPaint);
                    }
                    mOffsetX -= 2f;
                    if (mOffsetX <= -41 && mModel.canTap) {
                        mOffsetX += 52;
                    }
                }
                if (mScorePaint != null) {
                    canvas.drawText(mScoreFormatter.format(mModel.mScore).replaceAll(",", ",") + " km", 32, 100, mScorePaint);
                }
                final boolean isGameOver = mModel.update();
                if (isGameOver) {
                    initEnemies();
                } else if (mModel.mScore % 180 == 0) {
                    spawnNewEnemies(mModel);
                    if (mModel.mScore > 0 && mModel.mScore % (180 * 3) == 0) {
                        spawnBoss();
                    }
                }

                if (mEnemies != null) {
                    int enemyCount = 0;
                    for (Enemy e: mEnemies) {
                        enemyCount++;
                        if (e.collidesWithPlayer(mModel)) {
                            mModel.reset();
                            initEnemies();
                            break;
                        }
                        if (enemyCount < 3) {
                            final float posX = e.positionX * .01f * getWidth(), posY = e.positionY * .01f * getHeight();
                            final float sizeX = e.sizeX * .01f * getWidth(), sizeY = e.sizeY * .01f * getHeight();
                            // drawing owls
                            canvas.drawBitmap(enemyCount == 1 ? mEnemyPlaneBitmap0 : mEnemyPlaneBitmap1,
                                    null, new Rect((int)posX, (int)posY, (int)(posX + sizeX), (int)(posY + sizeY)), null);
//                            canvas.drawRect(posX, posY,
//                                    posX + sizeX, posY + sizeY,
//                                    mScorePaint);
                        } else {
                            final float posX = e.positionX * .01f * getWidth(), posY = (e.positionY - 2) * .01f * getHeight();
                            final float xBossSize = e.sizeX * .01f * getWidth();
                            final float yBossSize = (e.sizeY) * .01f * getHeight();
                            // drawing with scale, and top offset
                            canvas.drawBitmap(mBossPlane, null, new Rect((int)posX, (int)posY, (int)(posX + xBossSize), (int)(posY + yBossSize)), null);
                        }

                        e.update();
                    }
                }
            }

            // drawing foreground (fog, etc.)
            // canvas.drawBitmap(mForegroundCloudsBitmap, 0, canvas.getHeight() - mForegroundCloudsBitmap.getHeight() - 100, null);

            invalidate();
        }

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
}

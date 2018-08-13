package com.hipmunk.shinytext;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    double ax = 0, ay = 0, az = 0;   // these are the acceleration in x,y and z axis
    private Shader mShinyTextShader = null, mShinyTextShader2 = null;
    private TextView mShinyText = null, mCaption = null;
//    private ImageView mShinyLogo = null;
//    private Bitmap mShinyLogoBitmap = null;
    // private Paint mShinyLogoPaint = null;
    private int mScreenX = 800, mScreenY = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);

        mShinyText = (TextView) findViewById(R.id.shinytext);
        mCaption = (TextView) findViewById(R.id.caption);
//        mShinyLogo = (ImageView) findViewById(R.id.shinyLogo);
        mShinyTextShader = new LinearGradient(0, 0, 0, 0,
                new int[]{Color.rgb(51, 148, 222), Color.rgb(51, 148, 222), Color.rgb(51, 148, 222)},
                new float[]{0, 1, 1}, Shader.TileMode.CLAMP);
        mShinyText.getPaint().setShader(mShinyTextShader);
//        mShinyLogoBitmap = ((BitmapDrawable)mShinyLogo.getDrawable()).getBitmap();
//        mShinyLogoPaint = ((BitmapDrawable)mShinyLogo.getDrawable()).getPaint();

        final View root = findViewById(android.R.id.content);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mScreenX = root.getWidth();
                mScreenY = root.getHeight();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];

            double percentage = (ay * 1.2 / 10.0);
            int ry = (int) (percentage * mScreenX * 1.1);
            if (ry < 0) ry = 0;
            System.out.println("ShinyText(y): " + ay + "; " + ay + "; " + az);

            // if (percentage > 1) percentage = 1;

            mShinyTextShader = new LinearGradient(0, 0, ry, 10,
                    new int[]{Color.rgb(51, 148, 222), Color.rgb(51, 148, 222), Color.argb(200, 218, 239, 255), Color.rgb(51, 148, 222), Color.rgb(51, 148, 222)},
                    new float[]{0, .4f, .5f, .6f, 1},
                    Shader.TileMode.CLAMP);

            mShinyTextShader2 = new LinearGradient(0, 0, ry, 10,
                    new int[]{Color.rgb(48, 48, 48), Color.rgb(48, 48, 48), Color.argb(200, 235, 235, 235), Color.rgb(48, 48, 48), Color.rgb(48, 48, 48)},
                    new float[]{0, .4f, .5f, .6f, 1},
                    Shader.TileMode.CLAMP);

            // hipBlue = (51, 148, 222)
            if (mShinyText != null) {
                mShinyText.getPaint().setShader(mShinyTextShader);
                mShinyText.postInvalidate();
            }

            if (mCaption != null) {
                mCaption.getPaint().setShader(mShinyTextShader2);
                mCaption.postInvalidate();
            }

//            if (mShinyLogoPaint != null) {
                // mShinyLogoPaint.setShader(mShinyTextShader);
                // mShinyLogo.invalidate();
//            }
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int i) { }
}

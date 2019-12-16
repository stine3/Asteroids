package com.mmkcn.asteroids;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

public class Controller extends Activity implements SensorEventListener {

    private static final String TAG = "mmkcnController";

    private Screen screen;

    public Model model;
    private SensorManager sensorManager;


    private CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, (int) (1000.0 * Model.ticDurationS)) {
        public void onTick(long millisUntilFinished) {
            //Log.v("TAG", "timer.onTick()");

            // create graphics on start
            if (model.ticCounter == 0) {
                model.spaceShip = new SpaceShip(200, 400, model);
                // model.asteroid = new Asteroid(500f, 55f, 0f, -50f);
            }

            model.ticCounter++;
            model.deleteDead();

            // orientation in z direction: move forwards/backwards
            // TODO check for phone orientation (90 or -90 degrees at start?)
            if (orientationAngles[2] > 85.0 && orientationAngles[2] < 95.0) {
                model.spaceShip.x = model.spaceShip.x + 0;
            } else if (orientationAngles[2] < 90.0) {
                model.spaceShip.x = model.spaceShip.x - 5;

            } else if (orientationAngles[2] > 90.0) {
                model.spaceShip.x = model.spaceShip.x + 5;
            }
            // orientation in y direction: orientation of spaceship
            if (orientationAngles[1] > -5f && orientationAngles[1] < 5f) {
                model.spaceShip.rotate(0);
            } else if (orientationAngles[1] < 0f) {
                model.spaceShip.rotate(-5);

            } else if (orientationAngles[1] > 0f) {
                model.spaceShip.rotate(5);
            }

            model.spaceShip.move();


            for (Bullet bullet : model.arBullets) {
                bullet.move();
            }

            // model.asteroid.move();

            // if( model.asteroid.collision(model.spaceShip) ) {
            // Log.v(TAG, "collision() ------------------------------------------------------ ");
            //}
            screen.invalidate();
        }

        public void onFinish() {
            Log.v(TAG, "timer.onFinish()");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);

        model = new Model(this);
        screen = new Screen(getApplicationContext());
        screen.setModel(model);


        setContentView(screen);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        // we need the screen size before we can initialize the view
        Log.d(TAG, "onWindowFocusChanged() ");
        if (!model.isInit) {
            Log.d(TAG, "onWindowFocusChanged(): Initialisierung !!! ");

            // initialize model
            model.init(screen.getHeight(), screen.getWidth());
            timer.start();
        }
    }

    @Override
    protected void onResume() {     // persistence
        super.onResume();
        Log.d(TAG, "onResume()");

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        Sensor magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (model.isInit) {
            timer.start();
        }
    }

    @Override
    protected void onPause() {      // persistence
        super.onPause();
        Log.d(TAG, "onPause()");

        sensorManager.unregisterListener(this);
        timer.cancel();
        model.save();   // onPause always gets called when closing an app -> we save our values at this point
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        model.spaceShip.fire();
        return super.onTouchEvent(event);
    }

    private final float[] accelerometerReading = new float[3];
    private final float[] magnetometerReading = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerReading[0] = event.values[0];
            accelerometerReading[1] = event.values[1];
            accelerometerReading[2] = event.values[2];
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetometerReading[0] = event.values[0];
            magnetometerReading[1] = event.values[1];
            magnetometerReading[2] = event.values[2];
        }
        updateOrientationAngles();
        Log.d(TAG, "angles: x: " + orientationAngles[0] + " y: " + orientationAngles[1] + " z: " + orientationAngles[2]);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        orientationAngles[0] = (float) Math.toDegrees(orientationAngles[0]);
        orientationAngles[1] = (float) Math.toDegrees(orientationAngles[1]);
        orientationAngles[2] = (float) Math.toDegrees(orientationAngles[2]);
    }

}

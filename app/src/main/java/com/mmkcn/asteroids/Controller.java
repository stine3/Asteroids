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

import java.util.Random;

public class Controller extends Activity implements SensorEventListener {

    private static final String TAG = "mmkcnController";

    private Screen screen;

    public Model model;
    private SensorManager sensorManager;

    private CountDownTimer asTimer = new CountDownTimer(100000, 6000) {
        @Override
        public void onTick(long millisUntilFinished) {
            model.arAsteroid.add(model.asteroid.generateRandomAst(screen.width, screen.height));
        }
        @Override
        public void onFinish() {

        }
    };

    private CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, (int) (1000.0 * Model.ticDurationS)) {
        public void onTick(long millisUntilFinished) {
            //Log.v("TAG", "timer.onTick()");

            // create graphics on start
            if (model.ticCounter == 0) {
                model.spaceShip = new SpaceShip(screen.width / 2, screen.height / 2, model);
                model.asteroid = new Asteroid(0, 0, 0, 0);
            }

            manageSensorMovement();// moves spaceship according to sensors
            model.manageCollisions(); // manages bullet and asteroid collisions
            model.ticCounter++;
            model.deleteDead(); // delete hit bullets and asteroids


            // move all bullets and asteroids
            for (Asteroid asteroid : model.arAsteroid) {
                asteroid.move();
            }
            for (Bullet bullet : model.arBullets) {
                bullet.move();
            }
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
            asTimer.start();
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
        //Log.d(TAG, "angles: x: " + xOri + " y: " + yOri + " z: " + zOri);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public float xOri;
    public float yOri;
    public float zOri;

    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        xOri = (float) Math.toDegrees(orientationAngles[0]);
        yOri = (float) Math.toDegrees(orientationAngles[1]);
        zOri = (float) Math.toDegrees(orientationAngles[2]);
    }

    public void manageSensorMovement() {

        // if x > 0, then homebutton is left
        if (xOri > 0) {
            if (zOri > 85f && zOri < 95f) {
                // do nothing, don't move
            } else if (zOri < 90f) {
                // tilt phone forwards, moves spaceship backwards
                model.spaceShip.moveBackwards();

            } else if (zOri > 90f) {
                // tilt phone backwards, moves spaceship forwards
                model.spaceShip.move();
            }

            // orientation in y direction: orientation of spaceship
            if (yOri > -5f && yOri < 5f) {
                model.spaceShip.rotate(0);
            } else if (yOri < 0f) {

                model.spaceShip.rotate(-5);

            } else if (yOri > 0f) {
                model.spaceShip.rotate(5);
            }

            screen.orientation = false;

            // else x is negative, so the homebutton is right
        } else {
            if (zOri < -85f && zOri > -95f) {
                // do nothing, don't move
            } else if (zOri < -90f) {
                // tilt phone forwards, moves spaceship backwards
                model.spaceShip.moveBackwards();

            } else if (zOri > -90f) {
                // tilt phone backwards, moves spaceship forwards
                model.spaceShip.move();
            }

            // orientation in y direction: orientation of spaceship
            if (yOri > -5f && yOri < 5f) {
                model.spaceShip.rotate(0);
            } else if (yOri > 0f) {

                model.spaceShip.rotate(-5);

            } else if (yOri < 0f) {
                model.spaceShip.rotate(5);
            }
            screen.orientation = true;
        }
    }
}

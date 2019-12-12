package com.mmkcn.asteroids;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;

public class Controller extends Activity implements SensorEventListener {

    private static final String TAG = "mmkcnController";

    private Screen screen;

    public Model model;
    private SensorManager sensorManager;
    private Sensor sensor;


    private CountDownTimer timer = new CountDownTimer(Long.MAX_VALUE, (int) (1000.0 * Model.ticDurationS)) {
        public void onTick(long millisUntilFinished) {
            Log.v("TAG", "timer.onTick()");

            // create graphics on start
            if (model.ticCounter == 0) {
                model.spaceShip = new SpaceShip(100f, 50f, model);
                // model.asteroid = new Asteroid(500f, 55f, 0f, -50f);
            }

            model.ticCounter++;
            model.deleteDead();

            // example movement // TODO motion controlling
            model.spaceShip.move();
            model.spaceShip.rotate(2);
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
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

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

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

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
        updateOrientationAngles();
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading,
                    0, accelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading,
                    0, magnetometerReading.length);
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            //model.spaceShip.move();
        }

        Log.d(TAG, "angles: x: " + orientationAngles[0] + " y: " + orientationAngles[1] + " z: " + orientationAngles[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, magnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles);

        // "mOrientationAngles" now has up-to-date information.
    }
}

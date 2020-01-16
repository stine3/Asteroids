package com.mmkcn.asteroids;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

public class Controller extends Activity implements SensorEventListener {

    private static final String TAG = "mmkcnController";

    private Screen screen;
    public Model model;
    private SensorManager sensorManager;
    private Handler handler = new Handler();

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

            moveSpaceship();
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
            if (model.spaceShip.lives == 0) {
                model.isRunning = false;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        if (model.isInit) {
            timer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        sensorManager.unregisterListener(this);
        timer.cancel();
        model.save();   // onPause always gets called when closing an app -> we save our values at this point
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (model.isRunning) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    model.spaceShip.fire();
                }

            }, 500);
        } else {
            model.isRunning = true;
        }
        return super.onTouchEvent(event);
    }


    private float[] sensorValues = new float[3];

    @Override
    public void onSensorChanged(SensorEvent event) {
        // x axis irrelevant
        // y axis rotation
        // z axis is move
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorValues[0] = event.values[0];
            sensorValues[1] = event.values[1];
            sensorValues[2] = event.values[2];
        }
    }

    public void moveSpaceship() {
        if (sensorValues[1] < -1) {
            // rotate left
            model.spaceShip.rotate(-8);
        }
        if (sensorValues[1] > 1) {
            //rotate right
            model.spaceShip.rotate(8);
        }
        if (sensorValues[2] < -1) {
            // move backwards
            model.spaceShip.moveBackwards();
        }
        if (sensorValues[2] > 1) {
            // move forwards
            model.spaceShip.move();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

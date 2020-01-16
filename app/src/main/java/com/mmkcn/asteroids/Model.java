package com.mmkcn.asteroids;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class Model {

// TODO: getter and setter, serialize whole object instead of every moveable


    // static attributes
    private static final String TAG = "mmkcnModel";
    public static final float ticDurationS = 0.1f;  // Framerate
    private static final String serialisationFile = "asteroids.dat";

    // screen values
    public static final int logScreenHeight = 1000;  // logische Hoehe, die ist fix !!!, hierauf wird intern gemappt
    public int logScreenWidth = 0;            // ergibt sich aus dem AspectRatio des aktuellen canvas

    private Activity myActivity;

    public boolean isInit = false;
    public Integer ticCounter = 0;        // counts the amount of timer tics
    public SpaceShip spaceShip;
    public Asteroid asteroid;
    public ArrayList<Bullet> arBullets = new ArrayList<Bullet>();
    public ArrayList<Asteroid> arAsteroid = new ArrayList<Asteroid>();

    public Integer points = 0;

    private float scale = 0; // scale bitmaps to logical screen size

    public Model(Activity myAct) {
        myActivity = myAct;
    }

    public void init(int phyScreenHeight, int phyScreenWidth) {
        Log.d(TAG, "init() Model");

        logScreenWidth = (int) ((Model.logScreenHeight / (float) phyScreenHeight) * (float) phyScreenWidth);
        Log.v(TAG, "onWindowFocusChanged(): phys. height: " + phyScreenHeight + "  phys. width: " + phyScreenWidth);
        Log.v(TAG, "onWindowFocusChanged(): log. height: " + logScreenHeight + "  log. width: " + logScreenWidth);
        scale = logScreenHeight / logScreenWidth;
        // initialize class attributes
        Moveable.setClassAttributes(ticDurationS, phyScreenWidth, phyScreenHeight);
        Bitmap spaceship = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.spaceship);
        spaceship = Bitmap.createScaledBitmap(spaceship, (int) (50 * scale), (int) (50 * scale), true);
        SpaceShip.setClassAttributes(spaceship);

        Bitmap bullet = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.bulletv3);
        bullet = Bitmap.createScaledBitmap(bullet, (int) (30 * scale), (int) (30 * scale), true);
        Bullet.setClassAttributes(bullet);

        Bitmap asteroid = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.asteroid);
        asteroid = Bitmap.createScaledBitmap(asteroid, (int) (50 * scale), (int) (50 * scale), true);
        Asteroid.setClassAttributes(asteroid);

        load();
        isInit = true;
    }

    public void add(Bullet bullet) {
        arBullets.add(bullet);
    }


    public void deleteDead() {
        Iterator<Bullet> itB = arBullets.iterator();

        while (itB.hasNext()) {
            Bullet bullet = itB.next();
            if (!bullet.isAlive) {
                itB.remove();
            }
        }
        Iterator<Asteroid> itA = arAsteroid.iterator();

        while (itA.hasNext()) {
            Asteroid asteroid = itA.next();
            if (!asteroid.isAlive) {
                itA.remove();
            }
        }
    }


    public void manageCollisions() {
        for (Asteroid asteroid : arAsteroid) {
            if (spaceShip.collision(asteroid)) {
                Log.v(TAG, "collision() ------------------------------------------------------ ");
                asteroid.isAlive = false;
                spaceShip.lives--;
                // TODO remove spaceship when lives are 0 or something
            }
            for (Bullet bullet : arBullets) {
                if (bullet.collision(asteroid)) {
                    asteroid.isAlive = false;
                    bullet.isAlive = false; // delete asteroid and bullet
                    points = points + 100;
                }
            }
        }
    }

    public void save() {

        FileOutputStream foStream = null;

        try {
            // private outputStream with Context class
            foStream = myActivity.openFileOutput(serialisationFile, Context.MODE_PRIVATE);

            ObjectOutputStream o = new ObjectOutputStream(foStream);
            o.writeObject(ticCounter);
            o.writeObject(spaceShip);
            o.writeObject(arBullets);
            o.writeObject(arAsteroid);

            Log.d(TAG, "save(): serialized objects");
        } catch (IOException e) {
            Log.d(TAG, "IOException: " + e.toString());
        }
        // catch exception
        finally {
            try {
                foStream.close();
            }             // stream could still be open
            catch (Exception e) {
                Log.d(TAG, "Exception: " + e.toString());
            }
        }
    }

    private void load() {
        Log.d(TAG, "load()");
        InputStream fiStream = null;
        try {
            fiStream = myActivity.openFileInput(serialisationFile);
            ObjectInputStream o = new ObjectInputStream(fiStream);

            ticCounter = (Integer) o.readObject();
            spaceShip = (SpaceShip) o.readObject();
            spaceShip.init(this);
            arBullets = (ArrayList<Bullet>) o.readObject();

            for (Bullet bullet : arBullets) {
                bullet.init();
            }
            arAsteroid = (ArrayList<Asteroid>) o.readObject();
            for (Asteroid asteroid : arAsteroid) {
                asteroid.init();
            }

            Log.d(TAG, "load(): load serialized objects");
        } catch (IOException e) {
            Log.d(TAG, "IOException: " + e.toString());
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "ClassNotFoundException: " + e.toString());
        } finally {
            try {
                fiStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception: " + e.toString());
            }
        }
    }
}

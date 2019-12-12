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
    private static final String serialisationFile = "Persistenz4.dat";

    // screen values
    public static final int logScreenHeight = 1000;  // logische Hoehe, die ist fix !!!, hierauf wird intern gemappt
    public int logScreenWidth = 0;            // ergibt sich aus dem AspectRatio des aktuellen canvas

    private Activity myActivity;

    public boolean isInit = false;
    public Integer ticCounter = 0;        // counts the amount of timer tics
    public SpaceShip spaceShip;
    // public Asteroid asteroid;
    public ArrayList<Bullet> arBullets = new ArrayList<Bullet>();


    public Model(Activity myAct) {
        myActivity = myAct;
    }

    public void init(int phyScreenHeight, int phyScreenWidth) {
        Log.d(TAG, "init() Model");

        logScreenWidth = (int) ((Model.logScreenHeight / (float) phyScreenHeight) * (float) phyScreenWidth);
        Log.v(TAG, "onWindowFocusChanged(): phys. height: " + phyScreenHeight + "  phys. width: " + phyScreenWidth);
        Log.v(TAG, "onWindowFocusChanged(): log. height: " + logScreenHeight + "  log. width: " + logScreenWidth);

        // initialize class attributes
        Moveable.setClassAttributes(ticDurationS, logScreenWidth, logScreenHeight);
        Bitmap spaceship = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.spaceship);
        spaceship = Bitmap.createScaledBitmap(spaceship, 50, 50, true);
        SpaceShip.setClassAttributes(spaceship);

        Bitmap bullet = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.bulletv3);
        bullet = Bitmap.createScaledBitmap(bullet, 30, 30, true);
        Bullet.setClassAttributes(bullet);

        // bitmap = BitmapFactory.decodeResource(myActivity.getResources(), R.drawable.asteroid25_32);  //##
        //  Asteroid.setClassAttributes(bitmap);

        load();
        isInit = true;
    }

    public void add(Bullet bullet) {
        arBullets.add(bullet);
    }

    public void deleteDead() {
        Iterator<Bullet> it = arBullets.iterator();

        while (it.hasNext()) {
            Bullet bullet = it.next();
            if (!bullet.isAlive) {
                it.remove();
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
            // o.writeObject(asteroid);
            o.writeObject(spaceShip);
            o.writeObject(arBullets);

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
            //asteroid = (Asteroid) o.readObject();
            //asteroid.init();
            spaceShip = (SpaceShip) o.readObject();
            spaceShip.init(this);
            arBullets = (ArrayList<Bullet>) o.readObject();
            for (Bullet bullet : arBullets) {
                bullet.init();
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

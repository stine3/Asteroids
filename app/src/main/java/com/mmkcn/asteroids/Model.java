package com.mmkcn.asteroids;

import android.app.Activity;

import java.util.ArrayList;

public class Model {
    // schoener waere es:
    //   - wenn alle Attribute private waeren und setter, getter-Methoden haetten
    //   - nicht die Grafik-Objekte einzeln serialisiert werden, sondern das model gesamt
    //

    // statische Attribute
    private static final String TAG = "mmkcnModel";
    public static final float ticDurationS = 0.1f;  // Framerate
    private static final String serialisationFile = "Persistenz4.dat";

    // logische screen Werte
    public static final int logScreenHeight = 1000;  // logische Hoehe, die ist fix !!!, hierauf wird intern gemappt
    public int logScreenWidth = 0;            // ergibt sich aus dem AspectRatio des aktuellen canvas

    private Activity myActivity;

    // weitere Objektattribute
    public boolean isInit = false;
    public Integer ticCounter = 0;        // zaehlt die Timeraufrufe
    public SpaceShip spaceShip;
    public Asteroid asteroid;
    public ArrayList<Bullet> arBullets = new ArrayList<Bullet>();


    public Model(Activity myAct) {
        myActivity = myAct;
    }
}

package com.mmkcn.asteroids;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Bullet extends Moveable implements Serializable {

    // Klassenattribute (werden nicht serialisiert)
    private static final String TAG = "mmkcnBullet";
    private static Bitmap bitmap;

    // Objektattribute (werden serialisiert)
    private int timeToLiveTics;

    public static void setClassAttributes(Bitmap bitmap) {
        Bullet.bitmap = bitmap;
    }

    public Bullet(float xStart, float yStart, float direction, float speed, float timeToLiveS) {
        super(xStart - Bullet.bitmap.getWidth()/2, yStart - Bullet.bitmap.getHeight()/2, direction, speed);

        super.init(Bullet.bitmap);

        this.timeToLiveTics = (int) (timeToLiveS/Model.ticDurationS);
    }

    public void init() {
        super.init(Bullet.bitmap);
    }

    @Override
    public void move() {
        super.move();

        timeToLiveTics = timeToLiveTics - 1;

        if (timeToLiveTics == 0) {
            isAlive = false;
        }
    }


}

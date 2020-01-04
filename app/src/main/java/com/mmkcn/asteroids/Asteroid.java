package com.mmkcn.asteroids;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;


import java.io.Serializable;
import java.util.Random;


public class Asteroid extends Moveable implements Serializable {

    private static final String TAG = "mmkcnAsteroid";
    private static Bitmap bitmap;


    public static void setClassAttributes(Bitmap bitmap) {

        Asteroid.bitmap = bitmap;
    }

    public Asteroid(float xStart, float yStart, float direction, float speed) {
        super(xStart, yStart, direction, speed);
        super.init(Asteroid.bitmap);
    }

    public void init() {
        super.init(Asteroid.bitmap);
    }


    public Asteroid generateRandomAst(int screenX, int screenY) {
        Random random = new Random();
        x = random.nextInt(screenX);
        y = random.nextInt(screenY);
        direction = random.nextInt(360);
        speed = random.nextInt(50);
        return new Asteroid(x, y, direction, speed);
    }

    @Override
    public void draw(Canvas canvas) {
        // rotate with rotation matrix
        Matrix mat = new Matrix();
        mat.postRotate(direction, centerX, centerY);
        mat.postTranslate(x, y);
        canvas.drawBitmap(bitmap, mat, null);
    }
}

package com.mmkcn.asteroids;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import java.io.Serializable;

public class SpaceShip extends Moveable implements Serializable {

    private static final String TAG = "mmkcnSpaceShip";
    private static Bitmap bitmap;
    private transient Model model;
    public Integer lives = 3;

    public float direction;  // in degrees

    public SpaceShip(float xStart, float yStart, Model model) {
        super(xStart, yStart, 0f, 0f);
        this.model = model;
        super.init(SpaceShip.bitmap);
    }

    public void init(Model model) {
        this.model = model;
        super.init(SpaceShip.bitmap);
    }

    public static void setClassAttributes(Bitmap bitmap) {
        SpaceShip.bitmap = bitmap;
        Log.i(TAG, "setClassAttributes(): " + bitmap.getWidth() + "  " + bitmap.getHeight());

    }

    public void rotate(float diffAngle) {
        direction = (direction + diffAngle + 360f) % 360;
    }

    public void fire() {
        Bullet bullet = new Bullet(x + centerX, y + centerY, direction, 200, 5);
        model.add(bullet);
    }

    public void move() {
        xSpeed = (float) Math.cos((double) direction * Math.PI / 180f) * 5;
        ySpeed = (float) Math.sin((double) direction * Math.PI / 180f) * 5;
        Log.d(TAG, "xPos: " + x + " / yPos: " + y);
        super.move();
    }

    public void moveBackwards() {
        xSpeed = (float) Math.cos((double) direction * Math.PI / 180f) * -5;
        ySpeed = (float) Math.sin((double) direction * Math.PI / 180f) * -5;
        super.move();
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

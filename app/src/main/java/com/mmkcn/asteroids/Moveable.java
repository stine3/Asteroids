package com.mmkcn.asteroids;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.Serializable;

public class Moveable implements Serializable {

    private static final String TAG = "mmkcnMoveable";
    private static float stTicDurationS;
    private static int stScreenWidth;
    private static int stScreenHeight;

    protected float x = 0f;    // in pixel
    protected float y = 0f;    // in pixel, orientation downwards
    protected float direction; // in degrees, because y is downwards clockwise
    protected float speed;     // in pixel per second
    protected boolean isAlive = true;

    // transient attributes won't be serialized
    private transient float xSpeed = 0f;    // speed = pixel/s
    private transient float ySpeed = 0f;
    protected transient float centerX, centerY;
    private transient Bitmap bitmap;
    protected transient Paint paint;

    public static void setClassAttributes(float ticDurationS, int screenWidth, int screenHeight) {
        stTicDurationS = ticDurationS;
        stScreenWidth = screenWidth;
        stScreenHeight = screenHeight;
    }

    public Moveable(float xStart, float yStart, float direction, float speed) {
        // test, if class attributes are initialized
        float test = stTicDurationS;     // if initializing fails it throws an exception
        this.x = xStart;
        this.y = yStart;
        this.direction = direction;
        this.speed = speed;
    }

    public void init(Bitmap bitmap) {
        // initializing remaining attributes
        this.bitmap = bitmap;
        float pixelPerTimeTic = speed * stTicDurationS;
        xSpeed = (float) Math.cos((double) direction * Math.PI / 180f) * pixelPerTimeTic;
        ySpeed = (float) Math.sin((double) direction * Math.PI / 180f) * pixelPerTimeTic;  // y grows in negative direction!

        centerX = bitmap.getWidth() / 2;
        centerY = bitmap.getHeight() / 2;
    }

    public void move() {
        x = x + xSpeed  ;
        y = y + ySpeed  ;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean collision(Moveable moveable) {
        RectF rec, rec2;
        rec = moveable.getRect();
        rec2 = getRect();
        boolean intersec = rec.intersect(rec2);
        if (intersec) {
            return true;
        } else {
            return false;
        }
    }

    public RectF getRect() {
        RectF rec = new RectF(x, y, x + bitmap.getWidth(), y + bitmap.getHeight());
        return rec;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }
}

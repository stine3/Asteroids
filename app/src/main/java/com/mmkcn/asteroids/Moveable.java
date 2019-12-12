package com.mmkcn.asteroids;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.Serializable;

public class Moveable implements Serializable {

    private static final String TAG = "mmkcnMoveable";
    private static float stTicDurationS;        // via setClassAttributes()
    private static int stScreenWidth;           //            "
    private static int stScreenHeight;

    protected float x = 0f;    // in Pixel
    protected float y = 0f;    // in Pixel, Orientierung: nach unten
    protected float direction; // in Grad, math. Richtung, da y nach unten zeigt, folgt: im Uhrzeigersinn
    protected float speed;     // in Pixel / s
    protected boolean isAlive = true;

    // Objektattribute transient (werden nicht serialisiert, sondern in init() behandelt/berechnet)
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
        // Test, ob die Klassenattribute initialisiert sind:
        float test = stTicDurationS;     // sollte eine exception liefern, falls die Initialisierung
        // via setClassAttributes() nicht erfolgte
        this.x = xStart;
        this.y = yStart;
        this.direction = direction;
        this.speed = speed;
    }

    public void init(Bitmap bitmap) {
        // initialisiert alle (weiteren) transienten Attribute
        this.bitmap = bitmap;

        float pixelPerTimeTic = speed * stTicDurationS;
        xSpeed = (float) Math.cos((double) direction * Math.PI / 180f) * pixelPerTimeTic;
        ySpeed = (float) Math.sin((double) direction * Math.PI / 180f) * pixelPerTimeTic;  // Achtung: y waechst in neg. Richtung

        centerX = bitmap.getWidth() / 2;
        centerY = bitmap.getHeight() / 2;

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
    }

    public void move() {
        x = (x + xSpeed + stScreenWidth) % stScreenWidth;
        y = (y + ySpeed + stScreenHeight) % stScreenHeight;
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

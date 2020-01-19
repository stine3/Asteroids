package com.mmkcn.asteroids;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class Screen extends View {

    public Bitmap background = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background);
    DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
    int width = metrics.widthPixels;
    int height = metrics.heightPixels;
    double scale = height / 1080.0; // 1080px is the height of our background, which we use to scale to display size
    Model model;
    Paint paint = new Paint();
    private static final String TAG = "mmkcnScreen";


    public Screen(Context context) {
        super(context);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(50);
    }

    public void setModel(Model m) {
        model = m;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        background = Bitmap.createScaledBitmap(background, (int) (1920 * scale), (int) (1080 * scale), true); // scale bitmap to screen size
        canvas.drawBitmap(background, 0, 0, paint); // draw bitmap at top left corner (0,0)

        if ((model.ticCounter == 0)) {
            return;  // draw graphics when tic is at 1
        }
        if (model.isRunning) {
            model.spaceShip.draw(canvas);
            for (Asteroid asteroid : model.arAsteroid) {
                asteroid.draw(canvas);
            }
            for (Bullet bullet : model.arBullets) {
                bullet.draw(canvas);
            }
        } else {
            gameOver(canvas);
        }
        drawPoints(canvas);
    }


    private void drawPoints(Canvas canvas) {
        canvas.save();
        canvas.rotate(90);
        canvas.drawText("Points: " + model.points, 25, -25, paint);
        canvas.drawText("Lives: " + model.spaceShip.lives, 25, -75, paint);
        canvas.restore();
    }

    private void gameOver(Canvas canvas) {
        Log.d(TAG, "gameOver()");
        canvas.save();
        canvas.rotate(90);
        paint.setTextSize(50);
        model.arBullets.clear();
        model.arAsteroid.clear();
        canvas.drawText("GAME OVER! Tap to restart", width / 2 - 50, height / 2 * -1 + 100, paint); // random numbers so it's somewhat in the middle
        canvas.restore();
    }

}

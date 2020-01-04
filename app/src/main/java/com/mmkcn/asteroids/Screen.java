package com.mmkcn.asteroids;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;


public class Screen extends View {

    public Bitmap background = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background);
    DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
    int width = metrics.widthPixels;
    int height = metrics.heightPixels;
    double scale = height / 1080.0; // 1080px is the height of our background, which we use to scale to display size
    public boolean orientation = true;


    Model model;
    Paint paint = new Paint();


    public Screen(Context context) {

        super(context);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(paint.getTextSize() * 5);
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

        if(model.spaceShip.lives < 1) {
            model.spaceShip.draw(canvas);
        }
        for (Asteroid asteroid : model.arAsteroid) {
            asteroid.draw(canvas);
        }
        for (Bullet bullet : model.arBullets) {
            bullet.draw(canvas);
        }
        drawPoints(canvas, orientation);
    }

    public void drawPoints(Canvas canvas, boolean rotation) {
        if (rotation) {
            canvas.rotate(90, 0, 25);
            canvas.drawText(model.points.toString(), 0, 0, paint);
            canvas.drawText(model.spaceShip.lives.toString(), 50, -30,paint);
        } else{
            canvas.rotate(90, 0, 25);
            canvas.drawText(model.points.toString(), 0, 0, paint);
        }
    }
}

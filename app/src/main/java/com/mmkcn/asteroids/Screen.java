package com.mmkcn.asteroids;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

public class Screen extends View {

    Model model;

    // testing:
    Paint paint = new Paint();

    public Screen(Context context) {

        super(context);
        setBackgroundColor(Color.BLACK);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(paint.getTextSize() * 5);
    }

    public void setModel(Model m) {
        model = m;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if ((model.ticCounter == 0)) {
            return;  // draw graphics when tic is at 1
        }

        // scale to 1000 virtual pixels
        Matrix matCanvas = new Matrix();
        float yFac = (float) getHeight() / 1000f;
        matCanvas.setScale(yFac, yFac);
        canvas.concat(matCanvas);

        model.spaceShip.draw(canvas);
        //model.asteroid.draw(canvas);

        for (Bullet bullet : model.arBullets) {
            bullet.draw(canvas);
        }

        //debug:
        canvas.drawText(model.ticCounter.toString(), 100, 500, paint);
    }

}

package com.mmkcn.asteroids;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static android.content.ContentValues.TAG;

public class Start extends Activity {

    Button start;
    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.start);
        start = findViewById(R.id.start);

        mp = MediaPlayer.create(this, R.raw.startmelody);
        mp.start();
        mp.setLooping(true);


        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent explicitIntent = new Intent(Start.this, Controller.class);
                startActivity(explicitIntent);
            }
        });
    }


}

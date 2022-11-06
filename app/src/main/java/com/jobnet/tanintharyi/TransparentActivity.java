package com.jobnet.tanintharyi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.jobnet.tanintharyi.R;

import java.util.Timer;
import java.util.TimerTask;

public class TransparentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {

                Intent i = new Intent(TransparentActivity.this, FirstActivity.class);
                startActivity(i);
                finish();
            }
        }, 100);
    }
}

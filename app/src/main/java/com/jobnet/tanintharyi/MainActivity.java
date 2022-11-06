package com.jobnet.tanintharyi;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.jobnet.tanintharyi.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
ImageView icon_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        icon_img=findViewById(R.id.icon_img);


        Animation icon_fade= AnimationUtils.loadAnimation(this,R.anim.icon_fade);
        icon_img.setAnimation(icon_fade);
        Animation text_btou=AnimationUtils.loadAnimation(this,R.anim.btou);


        if(NetTest()) {

            if( SharedPrefManager.getInstance(this).isLoggedIn()) {
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {

                        Intent i = new Intent(MainActivity.this, FirstActivity.class);
                        i.putExtra("reg","0");
                        startActivity(i);
                        finish();
                    }
                }, 3000);
            }else{
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {

                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 3000);
            }
            ///////////
        }

    }
    private boolean NetTest() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}

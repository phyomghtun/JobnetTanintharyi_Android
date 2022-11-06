package com.jobnet.tanintharyi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jobnet.tanintharyi.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReadActivity extends AppCompatActivity {
    CircleImageView profile;
    TextView name, date, post;
    String str_contact_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        post = findViewById(R.id.post);

        Intent intent = getIntent();
        String str_profile = intent.getExtras().getString("profile");
        String str_name = intent.getExtras().getString("name");
        String str_date = intent.getExtras().getString("date");
        String str_post = intent.getExtras().getString("post");
        str_contact_phone = intent.getExtras().getString("contact_phone");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setTitle(str_name);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Glide.with(ReadActivity.this)
                .load(str_profile)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(profile);
        name.setText(str_name);
        date.setText(str_date);
        post.setText(str_post + "\n" + "\n" + "\n");

    }

    public void Call(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+str_contact_phone));
        if (ContextCompat.checkSelfPermission(ReadActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ReadActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
        }
        else
        {
            startActivity(callIntent);
        }

    }
    ///////
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.copy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       if(item.getItemId() == R.id.copy) {
            try {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", post.getText());
                assert manager != null;
                manager.setPrimaryClip(clipData);
                Toast.makeText(ReadActivity.this,"Copied post",Toast.LENGTH_LONG).show();
            } catch(Exception e) {
                //e.toString();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

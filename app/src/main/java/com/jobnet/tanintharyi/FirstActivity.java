package com.jobnet.tanintharyi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jobnet.tanintharyi.Models.User2;
import com.jobnet.tanintharyi.R;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class FirstActivity extends AppCompatActivity {
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    String noti_total=null;
    User user ;
    CircleImageView profile_img;
    TextView textCartItemCount;
    int mCartItemCount = 1;
    ProgressDialog progress;
    ViewPager viewPager;
    TabLayout tabLayout;
    ArrayList<Fragment> fragments;
    Toolbar toolbar;
    TextView textView;
    String state="";
    String user_name,user_pass,user_phone,user_profile;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_first);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        profile_img=findViewById(R.id.profile);
        user = SharedPrefManager.getInstance(this).getUser();

        auth = FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");

        Intent intent = getIntent();
        try {
            state = intent.getExtras().getString("reg");
        }catch(Exception e){

        }


        if(state.equals("1")){
            user_name=user.getName();
            user_pass="000000";
            user_phone="j"+user.getPhone()+"@gmail.com";

            progress = new ProgressDialog(FirstActivity.this);
            progress.setMessage("loading");
            progress.show();
            progress.setCancelable(false);
            Profile();

         }

        if (auth.getCurrentUser() == null) {
            user_pass="000000";
            user_phone="j"+user.getPhone()+"@gmail.com";
            auth.signInWithEmailAndPassword(user_phone,user_pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                            }
                        }
                    });

        }

        if (ContextCompat.checkSelfPermission(FirstActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FirstActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // toolbar.setLogo(R.drawable.tb_ic);
        // toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setTitle(R.string.app_name);
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);

     //  Profile();

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        fragments =new ArrayList<>();

        fragments.add(new HomeFragment());
        fragments.add(new ProfileFragment());
        fragments.add(new NotiFragment());


        FragmentAdapter pagerAdapter = new FragmentAdapter(getSupportFragmentManager(), getApplicationContext(), fragments);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setCustomView(R.layout.home_icon);
        tabLayout.getTabAt(1).setCustomView(R.layout.profile_icon);
        //set custom view
        tabLayout.getTabAt(2).setCustomView(R.layout.notification_badge);

        textView = (TextView) tabLayout.getTabAt(2).getCustomView().findViewById(R.id.text);
        Noti();

    }

    private void Noti() {

        final String phone=user.getPhone();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.NOTI_COUNT,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            noti_total= obj.getString("id");
                            textView.setText(noti_total);
                          //  Toast.makeText(FirstActivity.this,my_profile,Toast.LENGTH_LONG).show();
                           // Glide.with(FirstActivity.this).load(my_profile).placeholder(R.drawable.profile).error(R.drawable.profile).into(profile_img);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }
    //////
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_src, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         if(item.getItemId() == R.id.chat) {

             Intent i=new Intent(FirstActivity.this,GroupChatActivity.class);
             startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }
    private void Profile() {

        final String phone=user.getPhone();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.PROFILE_URL,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            user_profile= obj.getString("profile");
                            //  Toast.makeText(FirstActivity.this,my_profile,Toast.LENGTH_LONG).show();

                            ///////////
                            auth.createUserWithEmailAndPassword(user_phone,user_pass)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                FirebaseUser firebaseUser=auth.getCurrentUser();
                                                User2 u=new User2();
                                                u.setName(user_name);
                                                u.setEmail(user_phone);
                                                u.setProfile(user_profile);

                                                reference.child(firebaseUser.getUid()).setValue(u)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){

                                                                    progress.dismiss();

                                                                }

                                                            }
                                                        });
                                            }
                                        }
                                    });
                           // Toast.makeText(FirstActivity.this, user_profile, Toast.LENGTH_SHORT).show();

                            /////////////

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FirstActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                return params;
            }
        };

        VolleySingleton.getInstance(FirstActivity.this).addToRequestQueue(stringRequest);

    }
}

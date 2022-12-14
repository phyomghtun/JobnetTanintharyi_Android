package com.jobnet.tanintharyi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.jobnet.tanintharyi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    private String my_profile=null;
    private User user ;
    private CircleImageView profile_img;
    String str_phone,str_profile,str_name,str_date,str_contact_phone,str_township,str_post,temp_post;
    TextView name,tv_date;
    Spinner dynamicSpinner;
    EditText contact_phone,et_post;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        profile_img=(CircleImageView)findViewById(R.id.profile);
        dynamicSpinner = (Spinner) findViewById(R.id.static_spinner);
        contact_phone=findViewById(R.id.contact_phone);
        et_post=findViewById(R.id.et_post);

        name=findViewById(R.id.name);
        tv_date=findViewById(R.id.date);
        user = SharedPrefManager.getInstance(PostActivity.this).getUser();
        str_phone=user.getPhone();
        Profile();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // toolbar.setLogo(R.drawable.tb_ic);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
       // toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("Create Post");
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, FirstActivity.class));
                finish();
            }
        });
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
       // Toast.makeText(PostActivity.this,date,Toast.LENGTH_LONG).show();
        str_date=date;
        tv_date.setText(str_date);
///////////////////////////////
        String[] items = new String[] {"??????????????????????????????????????????","???????????????????????????????????????","???????????????????????????????????????????????????", "?????????????????????????????????????????????????????????","???????????????????????????????????????", "???????????????????????????????????????","????????????????????????????????????????????????", "?????????????????????????????????????????????","???????????????????????????????????????????????????", "??????????????????????????????????????????????????????"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);

        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
              // Toast.makeText(PostActivity.this,(String) parent.getItemAtPosition(position),Toast.LENGTH_LONG).show();
                if(parent.getItemAtPosition(position).equals("??????????????????????????????????????????") || parent.getItemAtPosition(position).equals("??????????????????????????????????????????")){
                    str_township="dawei";
                }else if(parent.getItemAtPosition(position).equals("???????????????????????????????????????") || parent.getItemAtPosition(position).equals("???????????????????????????????????????")){
                    str_township="yephyu";
                }else if(parent.getItemAtPosition(position).equals("???????????????????????????????????????????????????") || parent.getItemAtPosition(position).equals("???????????????????????????????????????????????????")){
                    str_township="launglon";
                }else if(parent.getItemAtPosition(position).equals("?????????????????????????????????????????????????????????") || parent.getItemAtPosition(position).equals("?????????????????????????????????????????????????????????")){
                    str_township="thayetchaung";
                }else if(parent.getItemAtPosition(position).equals("???????????????????????????????????????") || parent.getItemAtPosition(position).equals("???????????????????????????????????????")){
                    str_township="palaw";
                }else if(parent.getItemAtPosition(position).equals("???????????????????????????????????????") || parent.getItemAtPosition(position).equals("???????????????????????????????????????")){
                    str_township="myeik";
                }else if(parent.getItemAtPosition(position).equals("????????????????????????????????????????????????") || parent.getItemAtPosition(position).equals("????????????????????????????????????????????????")){
                    str_township="kyunsu";
                }else if(parent.getItemAtPosition(position).equals("?????????????????????????????????????????????") || parent.getItemAtPosition(position).equals("???????????????????????????????????????????????????")){
                    str_township="tanintharyi";
                }else if(parent.getItemAtPosition(position).equals("???????????????????????????????????????????????????") || parent.getItemAtPosition(position).equals("???????????????????????????????????????????????????")){
                    str_township="bokpyin";
                }else if(parent.getItemAtPosition(position).equals("??????????????????????????????????????????????????????") || parent.getItemAtPosition(position).equals("??????????????????????????????????????????????????????")){
                    str_township="kawthoung";
                }
              //  Toast.makeText(PostActivity.this,str_township,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        str_contact_phone=contact_phone.getText().toString();
        temp_post=et_post.getText().toString();
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

                            my_profile= obj.getString("profile");
                            str_name=obj.getString("name");
                            name.setText(str_name);
                            str_profile=my_profile;
                           //  Toast.makeText(PostActivity.this,str_name,Toast.LENGTH_LONG).show();
                            Glide.with(PostActivity.this).load(my_profile).placeholder(R.drawable.profile).error(R.drawable.profile).into(profile_img);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        VolleySingleton.getInstance(PostActivity.this).addToRequestQueue(stringRequest);

    }
/////////////////////
public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.post_menu, menu);
    final MenuItem postItem = menu.findItem(R.id.post);

    if(temp_post.equals("")){
        postItem.setVisible(false);
        final Handler handler = new Handler();
         Runnable runnable = new Runnable() {
            @Override
            public void run() {
                temp_post=et_post.getText().toString();
               if(!temp_post.equals("")){
                   postItem.setVisible(true);
                   /////////
                   final Handler handler = new Handler();
                   Runnable runnable = new Runnable() {
                       @Override
                       public void run() {
                           temp_post=et_post.getText().toString();
                           if(temp_post.equals("")){
                               postItem.setVisible(false);
                           }
                           handler.postDelayed(this, 300);
                       }
                   };
                   handler.postDelayed(runnable, 300);
                   ////////
               }
                handler.postDelayed(this, 300);
            }
        };
        handler.postDelayed(runnable, 300);
    }else{
        postItem.setVisible(true);
    }
    return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.post ) {
            str_contact_phone=contact_phone.getText().toString();
            if (TextUtils.isEmpty(str_contact_phone) || str_contact_phone.length()<5) {
                contact_phone.setError("Please Enter");
                contact_phone.requestFocus();
            }else if(!temp_post.equals("")){
                byte[] temp = temp_post.getBytes();
                str_post=new String(encodeBase64(temp));
                Upload_Post();
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please Wait...");
                progressDialog.getProgress();
                progressDialog.show();
                progressDialog.setCancelable(false);

            }

        }
        return super.onOptionsItemSelected(item);
    }

    /////////////////////
public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK
            && event.getRepeatCount() == 0) {
        event.startTracking();
        return true;
    }
    return super.onKeyDown(keyCode, event);
}

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {

            startActivity(new Intent(PostActivity.this, FirstActivity.class));
            finish();

            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
    ////////
    private static byte[] decodeBase64(String dataToDecode)
    {
        byte[] dataDecoded = Base64.decode(dataToDecode, Base64.DEFAULT);
        return dataDecoded;
    }

    private static byte[] encodeBase64(byte[] dataToEncode)
    {
        byte[] dataEncoded = Base64.encode(dataToEncode, Base64.DEFAULT);
        return dataEncoded;
    }
    //////////////////////

    @SuppressLint("ResourceAsColor")
    private void Upload_Post() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.POST_UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            //if no error in response
                            if (!obj.getBoolean("error")) {

                                //  Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                System.out.println("------------------------->No Error<-------------------------");
                                Toast.makeText(getApplicationContext(),"Post Upload Successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(PostActivity.this, FirstActivity.class));
                                finish();


                            } else {
                                System.out.println("------------------------->Error<-------------------------");
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("------------------------->Volley Error<-------------------------");
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", str_name);
                params.put("phone", str_phone);
                params.put("profile", str_profile);
                params.put("post", str_post);
                params.put("township", str_township);
                params.put("contact", str_contact_phone);
                params.put("date", str_date);
               // System.out.println(params.get("name"));
                return params;

            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }
}

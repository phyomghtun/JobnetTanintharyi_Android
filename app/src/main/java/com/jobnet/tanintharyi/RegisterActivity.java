package com.jobnet.tanintharyi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.annotation.SuppressLint;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    RadioGroup rg;
    int xDim, yDim;
    String ph;
    BitmapFactory.Options options;
    RadioButton radioM,radioF;
    TextView login;
    ImageView icon_img;
    Button register,ok;
    EditText et_name,et_phone,et_password;
    String s_gender=null;
    private static final String ROOT_URL = "http://seoforworld.com/api/v1/file-upload.php";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST =1 ;
    private Bitmap bitmap;
    private String filePath;
    CircleImageView profile;
    String temp="empty";
    ProgressBar progress;
    CardView card;
    int profile_num=0;
    Bitmap bitmap1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        login=findViewById(R.id.login);
        profile=findViewById(R.id.profile);
        et_name=findViewById(R.id.name);
        et_phone=findViewById(R.id.phone);
        et_password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        progress=findViewById(R.id.progress);
        card=findViewById(R.id.card);
        ok=findViewById(R.id.ok);
        icon_img=findViewById(R.id.icon_img);

        rg = (RadioGroup) findViewById(R.id.radioGroup);
        radioM=findViewById(R.id.radioM);
        radioF=findViewById(R.id.radioF);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
        }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress.setVisibility(View.VISIBLE);
                register.setClickable(false);
                if(radioM.isChecked()){
                    s_gender=radioM.getText().toString();
                }
                else{
                    s_gender=radioF.getText().toString();

                }
                card.setVisibility(View.INVISIBLE);
                registerUser();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profile_num==1) {
                    ok.setVisibility(View.INVISIBLE);
                    uploadBitmap(bitmap1);
                    profile.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);

                  //  finish();
                  //  startActivity(new Intent(getApplicationContext(), FirstActivity.class));
                }else{
                    Toast.makeText(RegisterActivity.this,"Please choose profile",Toast.LENGTH_LONG).show();
                }
            }
        });
        ////////////////////
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioM:
                       s_gender="Male";
                        break;
                    case R.id.radioF:
                        s_gender="Female";
                        break;
                }
            }
        });
    }
    /////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profile.setImageBitmap(bitmap1);
                profile_num=1;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {

        //getting the tag from the edittext
        final String tags = ph;

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                          //  Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            Intent i=new Intent(RegisterActivity.this,FirstActivity.class);
                            i.putExtra("reg","1");
                            startActivity(i);
                            finish();
                            progress.setVisibility(View.INVISIBLE);


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
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tags", tags);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

    }

    //////////////////
    @SuppressLint("ResourceAsColor")
    private void registerUser() {

        final String profileString = temp;
        final String name = et_name.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        final String phone = et_phone.getText().toString().trim();
        final String gender = s_gender;
        ph=phone;

        if (TextUtils.isEmpty(name)) {
            progress.setVisibility(View.INVISIBLE);
            card.setVisibility(View.VISIBLE);
            et_name.setError("Please enter your name");
            et_name.requestFocus();
            register.setClickable(true);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            progress.setVisibility(View.INVISIBLE);
            card.setVisibility(View.VISIBLE);
            et_password.setError("Please enter your password");
            et_password.requestFocus();
            register.setClickable(true);
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            progress.setVisibility(View.INVISIBLE);
            card.setVisibility(View.VISIBLE);
            et_phone.setError("Please enter your phone number");
            et_phone.requestFocus();
            register.setClickable(true);
            return;
        }




            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
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
                                    JSONObject userJson = obj.getJSONObject("user");
                                    progress.setVisibility(View.INVISIBLE);
                                    profile.setVisibility(View.VISIBLE);
                                    card.setVisibility(View.INVISIBLE);
                                    ok.setVisibility(View.VISIBLE);
                                    icon_img.setVisibility(View.INVISIBLE);
                                    User user = new User(
                                            userJson.getInt("id"),
                                            userJson.getString("name"),
                                            userJson.getString("phone")
                                    );

                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                                } else {
                                    System.out.println("------------------------->Error<-------------------------");
                                    progress.setVisibility(View.INVISIBLE);
                                    profile.setVisibility(View.INVISIBLE);
                                    card.setVisibility(View.VISIBLE);
                                    register.setClickable(true);
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
                    params.put("name", name);
                    params.put("phone", phone);
                    params.put("password", password);
                    params.put("gender", gender);
                    System.out.println(params.get("name"));
                    return params;

                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }
}

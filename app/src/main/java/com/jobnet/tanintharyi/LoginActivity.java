package com.jobnet.tanintharyi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jobnet.tanintharyi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    TextView register;
    EditText et_phone,et_password;
    Button login;
    ProgressBar progress;
    CardView card;
    String temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        register=findViewById(R.id.register);
        et_phone=findViewById(R.id.phone);
        et_password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        progress=findViewById(R.id.progress);
        card=findViewById(R.id.card);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setClickable(false);
              progress.setVisibility(View.VISIBLE);
              card.setVisibility(View.INVISIBLE);
              userLogin();
            }
        });
    }
    //////////////////
    private void userLogin() {
        final String phone = et_phone.getText().toString();
        final String password = et_password.getText().toString();
        //validating inputs
        if (TextUtils.isEmpty(phone)) {
            progress.setVisibility(View.INVISIBLE);
            card.setVisibility(View.VISIBLE);
            et_phone.setError("Please enter your username");
            et_phone.requestFocus();
            login.setClickable(true);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            progress.setVisibility(View.INVISIBLE);
            card.setVisibility(View.VISIBLE);
            et_password.setError("Please enter your password");
            et_password.requestFocus();
            login.setClickable(true);
            return;
        }

        //if everything is fine
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                progress.setVisibility(View.INVISIBLE);
                                card.setVisibility(View.VISIBLE);
                                login.setClickable(true);
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getString("name"),
                                        userJson.getString("phone")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                //starting the profile activity
                                finish();
                                Intent i=new Intent(LoginActivity.this,FirstActivity.class);
                                i.putExtra("reg","0");
                                startActivity(i);
                            } else {
                                progress.setVisibility(View.INVISIBLE);
                                card.setVisibility(View.VISIBLE);
                                login.setClickable(true);
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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}

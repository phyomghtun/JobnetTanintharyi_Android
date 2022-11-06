package com.jobnet.tanintharyi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.jobnet.tanintharyi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    private View view;
    private Context context;
    private String my_profile=null;
    public User user ;
    Bitmap bitmap1;
    private CircleImageView profile_img;
    private Context mContext;
    String phone;
    ///////////////

    List<PostAdapter> GetDataAdapter1;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    ProgressDialog progressDialog;
    RecyclerView.Adapter recyclerViewadapter;
    ImageView logout,setting;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = inflater.getContext();
        profile_img=(CircleImageView)view.findViewById(R.id.profile);
        logout=view.findViewById(R.id.logout);
        setting=view.findViewById(R.id.setting);
        user = SharedPrefManager.getInstance(context).getUser();
        phone = user.getPhone();
        Profile();
        setHasOptionsMenu(true);
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });
        ////////////
        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview1);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.getProgress();
        progressDialog.show();
        progressDialog.setCancelable(false);

        MY_POST();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.getInstance(context).logout();
                Intent i=new Intent(context,LoginActivity.class);
                startActivity(i);
                ((FirstActivity)mContext).finish();



               // SharedPrefManager.getInstance(context).logout();
               // startActivity(new Intent(context, LoginActivity.class));
                //  startActivity(new Intent(context, LoginActivity.class));
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.show(getActivity().getSupportFragmentManager(), "BottomSheet");
            }
        });

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.filter);
            item.setVisible(false);
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
                            //  Toast.makeText(FirstActivity.this,my_profile,Toast.LENGTH_LONG).show();
                            Glide.with(context).load(my_profile).placeholder(R.drawable.profile).error(R.drawable.profile).into(profile_img);
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                bitmap1 = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                profile_img.setImageBitmap(bitmap1);
                uploadBitmap(bitmap1);

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
        final String tags = user.getPhone();

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            MY_POST();
                            JSONObject obj = new JSONObject(new String(response.data));
                            //  Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
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
        Volley.newRequestQueue(context).add(volleyMultipartRequest);

    }
    /////////////
    void MY_POST() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.MY_POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONArray j= new JSONArray(response);
                            //converting response to json object

                                //  Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                GetDataAdapter1.clear();

                                try {

                                    for (int i = 0; i < j.length(); i++) {

                                        PostAdapter GetDataAdapter2 = new PostAdapter();


                                        try {

                                            JSONObject json = j.getJSONObject(i);

                                            GetDataAdapter2.setId(json.getString("id"));
                                            GetDataAdapter2.setProfile(json.getString("profile"));
                                            GetDataAdapter2.setName(json.getString("name"));
                                            GetDataAdapter2.setDate(json.getString("date"));
                                            GetDataAdapter2.setPhone(json.getString("phone"));
                                            GetDataAdapter2.setPost(new String(decodeBase64(json.getString("post")), "UTF-8"));
                                            GetDataAdapter2.setContact(json.getString("contact"));
                                            GetDataAdapter2.setTownship(json.getString("township"));


                                        } catch (JSONException e) {

                                            e.printStackTrace();
                                        }
                                        GetDataAdapter1.add(GetDataAdapter2);
                                    }
                                }catch (NullPointerException ne){

                                }catch (Exception e){

                                }

                                recyclerViewadapter = new RecyclerViewAdapter2(GetDataAdapter1, context);

                                recyclerView.setAdapter(recyclerViewadapter);
                                progressDialog.dismiss();

                                //////////////////
                            } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("------------------------->Volley Error<-------------------------");
                      //  Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                return params;

            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

    }
    /////
    private static byte[] decodeBase64(String dataToDecode)
    {
        byte[] dataDecoded = Base64.decode(dataToDecode, Base64.DEFAULT);
        return dataDecoded;
    }
/////////////////////////
}
package com.jobnet.tanintharyi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.jobnet.tanintharyi.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotiFragment extends Fragment {
    private View view;
    private Context context;
    private String my_profile=null;
    private User user ;
    private CircleImageView profile_img;
    ///////////
    JsonArrayRequest jsonArrayRequest ;
    RequestQueue requestQueue ;
    List<NotiAdapter> GetDataAdapter1;
    private SwipeRefreshLayout mSwipeLayout;
    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;
    ProgressDialog progressDialog;
    RecyclerView.Adapter recyclerViewadapter;
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_noti, container, false);
        context = inflater.getContext();
        profile_img=(CircleImageView)view.findViewById(R.id.profile);
        user = SharedPrefManager.getInstance(context).getUser();
        // Profile();
        setHasOptionsMenu(true);

        NOTI_URL();

        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview3);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);

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

    /////////////////
    public void NOTI_URL() {

        jsonArrayRequest = new JsonArrayRequest(URLs.NOTI,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        JSON_PARSE_DATA_AFTER_WEBCALL(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        requestQueue = Volley.newRequestQueue(context);

        requestQueue.add(jsonArrayRequest);
    }
    public void JSON_PARSE_DATA_AFTER_WEBCALL(JSONArray array){

        GetDataAdapter1.clear();

        try {
            for (int i = 0; i < array.length(); i++) {

                NotiAdapter GetDataAdapter2 = new NotiAdapter();

                JSONObject json = null;

                try {

                    json = array.getJSONObject(i);

                    GetDataAdapter2.setTitle(new String(decodeBase64(json.getString("title")), "UTF-8"));
                    GetDataAdapter2.setText(new String(decodeBase64(json.getString("noti")), "UTF-8"));


                } catch (JSONException e) {

                    e.printStackTrace();
                }
                GetDataAdapter1.add(GetDataAdapter2);
            }
        }catch (NullPointerException ne){

        }catch (Exception e){

        }

        recyclerViewadapter = new RecyclerViewAdapter3(GetDataAdapter1, context);

        recyclerView.setAdapter(recyclerViewadapter);

    }
    ////////////
    private static byte[] decodeBase64(String dataToDecode)
    {
        byte[] dataDecoded = Base64.decode(dataToDecode, Base64.DEFAULT);
        return dataDecoded;
    }

}
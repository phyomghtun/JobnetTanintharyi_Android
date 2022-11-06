package com.jobnet.tanintharyi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class HomeFragment extends Fragment implements Toolbar.OnMenuItemClickListener{
    private View view;
    private Context context;
    private String my_profile=null;
    public User user ;
    String temp;
    private CircleImageView profile_img;
    ImageView post;
    /////////
    JsonArrayRequest jsonArrayRequest ;
    RequestQueue requestQueue ;
    List<GetDataAdapter> GetDataAdapter1;
    private SwipeRefreshLayout mSwipeLayout;
    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;
    ProgressDialog progressDialog;
    RecyclerView.Adapter recyclerViewadapter;
    AlertDialog alertDialog1;
    CharSequence[] values = {" ထားဝယ္ၿမိဳ႕နယ္ "," ေရျဖဴၿမိဳ႕နယ္ "," ေလာင္းလုံၿမိဳ႕နယ္ "," သရက္ေခ်ာင္းၿမိဳ႕နယ္ "," ပုေလာၿမိဳ႕နယ္ "," ၿမိတ္ၿမိဳ႕နယ္ "," ကၽြန္းစုၿမိဳ႕နယ္ "," တနသၤာရီၿမိဳ႕နယ္ "," ဘုတ္ျပင္းၿမိဳ႕နယ္ "," ေကာ့ေသာင္းၿမိဳ႕နယ္ "};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment, container, false);
        context = inflater.getContext();
        profile_img=(CircleImageView)view.findViewById(R.id.profile);
        post=view.findViewById(R.id.post);
        user = SharedPrefManager.getInstance(context).getUser();
        JOBS_URL();


        GetDataAdapter1 = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
////////////////

        Profile();
        setHasOptionsMenu(true);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, PostActivity.class));
                getActivity().finish();
            }
        });
        //////////
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RANDOM_JOBS_URL();
                Profile();
            } });

        mSwipeLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.getProgress();
        progressDialog.show();
        progressDialog.setCancelable(false);

        return view;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.filter){
            Township() ;
        }

        return super.onOptionsItemSelected(item);
    }
    ///////////////
    public void Profile() {

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
///////////////////////////////
public void JOBS_URL() {

    jsonArrayRequest = new JsonArrayRequest(URLs.JOBS_URL,

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

                GetDataAdapter GetDataAdapter2 = new GetDataAdapter();

                JSONObject json = null;

                try {

                    json = array.getJSONObject(i);

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

        recyclerViewadapter = new RecyclerViewAdapter(GetDataAdapter1, context);

        recyclerView.setAdapter(recyclerViewadapter);
        mSwipeLayout.setRefreshing(false);
        progressDialog.dismiss();
       // Toast.makeText(context,temp,Toast.LENGTH_LONG).show();

    }
    ///////////////////
    private void RANDOM_JOBS_URL() {

        jsonArrayRequest = new JsonArrayRequest(URLs.RAMDOM_JOBS_URL,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response1) {

                        JSON_PARSE_DATA_AFTER_WEBCALL1(response1);
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
    public void JSON_PARSE_DATA_AFTER_WEBCALL1(JSONArray array){

        GetDataAdapter1.clear();

        try {
            for (int i = 0; i < array.length(); i++) {

                GetDataAdapter GetDataAdapter2 = new GetDataAdapter();

                JSONObject json = null;

                try {

                    json = array.getJSONObject(i);

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

        recyclerViewadapter = new RecyclerViewAdapter(GetDataAdapter1, context);

        recyclerView.setAdapter(recyclerViewadapter);
        mSwipeLayout.setRefreshing(false);
        progressDialog.dismiss();
        // Toast.makeText(context,temp,Toast.LENGTH_LONG).show();

    }
    ///////////
    private static byte[] decodeBase64(String dataToDecode)
    {
        byte[] dataDecoded = Base64.decode(dataToDecode, Base64.DEFAULT);
        return dataDecoded;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.filter) {
            Toast.makeText(context, "Filter", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    ///////////
    public void Township(){


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("ၿမိဳ႕နယ္ေရြးခ်ယ္ပီး႐ွာပါ");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("dawei");
                        break;
                    case 1:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("yephyu");
                        break;
                    case 2:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("launglon");
                        break;
                    case 3:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("thayetchaung");
                        break;
                    case 4:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("palaw");
                        break;
                    case 5:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("myeik");
                        break;
                    case 6:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("kyunsu");
                        break;
                    case 7:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("tanintharyi");
                        break;
                    case 8:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("bokpyin");
                        break;
                    case 9:
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.getProgress();
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        FIND_TOWNSHIP("kawthoung");
                        break;

                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }
    ////////////////
    private void FIND_TOWNSHIP(final String town) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.FIND_TOWN_JOBS,
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

                                    GetDataAdapter GetDataAdapter2 = new GetDataAdapter();


                                    try {

                                        JSONObject json = j.getJSONObject(i);

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

                            recyclerViewadapter = new RecyclerViewAdapter(GetDataAdapter1, context);

                            recyclerView.setAdapter(recyclerViewadapter);
                            progressDialog.dismiss();

                            //////////////////
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("------------------------->Volley Error<-------------------------");
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("town", town);
                return params;

            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

    }
}
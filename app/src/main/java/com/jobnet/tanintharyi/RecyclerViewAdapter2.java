package com.jobnet.tanintharyi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.jobnet.tanintharyi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JUNED on 6/16/2016.
 */
public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder> {

    Context context;
    List<PostAdapter> getDataAdapter;
    String str_id;
    ProgressDialog progressDialog;

    public RecyclerViewAdapter2(List<PostAdapter> getDataAdapter, Context context){

        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items2, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

         final PostAdapter getDataAdapter1 =  getDataAdapter.get(position);

        Glide.with(Viewholder.itemView)
                .load(getDataAdapter1.getProfile())
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(Viewholder.profile);
        Viewholder.name.setText(getDataAdapter1.getName());
        Viewholder.date.setText(getDataAdapter1.getDate());
        if(getDataAdapter1.getPost().length()>130) {
            Viewholder.post.setText(getSafeSubstring(getDataAdapter1.getPost(), 130) + " . . . See More");
        }else{
            Viewholder.post.setText(getDataAdapter1.getPost());
        }


        Viewholder.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,ReadActivity.class);
                i.putExtra("profile",getDataAdapter1.getProfile());
                i.putExtra("name",getDataAdapter1.getName());
                i.putExtra("date",getDataAdapter1.getDate());
                i.putExtra("post",getDataAdapter1.getPost());
                i.putExtra("contact_phone",getDataAdapter1.getContact());
                context.startActivity(i);
            }
        });

        Viewholder.my_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///////////

                PopupMenu popup = new PopupMenu(context, view);
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popup.getMenuInflater().inflate(R.menu.mypostmenu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Delete")){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are You Sure You Want To Delete")
                                    .setCancelable(false)
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    })
                                    .setPositiveButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    str_id=getDataAdapter1.getId();
                                                    Post_Delete(str_id);
                                                    context.startActivity(new Intent(context,TransparentActivity.class));

                                                }
                                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }else{
                            Intent i2=new Intent(context,EditActivity.class);
                            i2.putExtra("id",getDataAdapter1.getId());
                            i2.putExtra("profile",getDataAdapter1.getProfile());
                            i2.putExtra("name",getDataAdapter1.getName());
                            i2.putExtra("date",getDataAdapter1.getDate());
                            i2.putExtra("post",getDataAdapter1.getPost());
                            i2.putExtra("contact_phone",getDataAdapter1.getContact());
                            context.startActivity(i2);

                        }
                        return true;
                    }
                });
                popup.show();

                //////////
            }
        });
    }

    public String getSafeSubstring(String s, int maxLength){
        if(!TextUtils.isEmpty(s)){
            if(s.length() >= maxLength){
                return s.substring(0, maxLength);
            }
        }
        return s;
    }
    @Override
    public int getItemCount() {

        return getDataAdapter.size();
    }
    /////////////

    class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView profile;
        public TextView name;
        public TextView date,post;
        public ImageView my_menu;

        public ViewHolder(View itemView) {

            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name) ;
            profile=itemView.findViewById(R.id.profile);
            date=itemView.findViewById(R.id.date);
            post=itemView.findViewById(R.id.post);
            my_menu=itemView.findViewById(R.id.my_menu);

        }
    }
///////////////
private void Post_Delete(final String id) {

    StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.DELETE_POST,
            new Response.Listener<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(String response) {

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                       // String my_profile= obj.getString("profile");
                        Toast.makeText(context,"Successfully",Toast.LENGTH_LONG).show();

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
            params.put("id",id);
            return params;
        }
    };

    VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
}
}

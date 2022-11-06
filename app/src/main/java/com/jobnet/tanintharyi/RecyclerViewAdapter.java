package com.jobnet.tanintharyi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jobnet.tanintharyi.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by JUNED on 6/16/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<GetDataAdapter> getDataAdapter;

    public RecyclerViewAdapter(List<GetDataAdapter> getDataAdapter, Context context){

        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        final GetDataAdapter getDataAdapter1 =  getDataAdapter.get(position);

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

        Viewholder.report_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(view);
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
        public ImageView report_image;

        public ViewHolder(View itemView) {

            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name) ;
            profile=itemView.findViewById(R.id.profile);
            date=itemView.findViewById(R.id.date);
            post=itemView.findViewById(R.id.post);
            report_image=itemView.findViewById(R.id.report_menu);

        }
    }
    //////////////////////////
    private void showPopupWindow(View view) {
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
        popup.getMenuInflater().inflate(R.menu.report_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                return true;
            }
        });
        popup.show();
    }

}

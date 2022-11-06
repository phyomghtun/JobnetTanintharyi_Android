package com.jobnet.tanintharyi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.jobnet.tanintharyi.R;

import java.util.List;

/**
 * Created by JUNED on 6/16/2016.
 */
public class RecyclerViewAdapter3 extends RecyclerView.Adapter<RecyclerViewAdapter3.ViewHolder> {

    Context context;
    List<NotiAdapter> getDataAdapter;

    public RecyclerViewAdapter3(List<NotiAdapter> getDataAdapter, Context context){

        super();
        this.getDataAdapter = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items3, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        final NotiAdapter getDataAdapter1 =  getDataAdapter.get(position);

        Viewholder.title.setText(getDataAdapter1.getTitle());
       // Viewholder.text.setText(getDataAdapter1.getText());
        if(getDataAdapter1.getText().length()>100) {
            Viewholder.text.setText(getSafeSubstring(getDataAdapter1.getText(), 100) + " . . . See More");
        }else{
            Viewholder.text.setText(getDataAdapter1.getText());
        }

        Viewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getDataAdapter1.getTitle());
                builder.setMessage(getDataAdapter1.getText());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
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

        public TextView title,text;

        public ViewHolder(View itemView) {

            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title) ;
            text=itemView.findViewById(R.id.text);

        }
    }

}

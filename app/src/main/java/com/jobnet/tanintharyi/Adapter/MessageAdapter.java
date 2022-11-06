package com.jobnet.tanintharyi.Adapter;

import android.content.ContentProvider;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jobnet.tanintharyi.Models.AllMethods;
import com.jobnet.tanintharyi.Models.Message;
import com.jobnet.tanintharyi.R;
import com.google.firebase.database.DatabaseReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {

    Context context;
    List<Message> messages;
    DatabaseReference messageDb;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;


    public  MessageAdapter(Context context,List<Message> messages,DatabaseReference messageDb){
        this.context=context;
        this.messageDb=messageDb;
        this.messages=messages;
    }

    @NonNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapterViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder holder, int position) {

        Message message=messages.get(position);
        if(message.getName().equals(AllMethods.name)){
            holder.tvmessage.setText(message.getMessage());
            holder.tvmessage.setGravity(Gravity.START);

        }else{
            holder.tvmessage.setText(message.getMessage());
            holder.tvname.setText(message.getName());
            Glide.with(context)
                    .load(message.getProfile()) // image url
                    .placeholder(R.mipmap.ic_launcher) // any placeholder to load at start
                    .error(R.mipmap.ic_launcher)  // any image in case of error
                    .centerCrop()
                    .into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView tvmessage,tvname;
        CircleImageView profile_image;

        public MessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvmessage=(TextView)itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            tvname=itemView.findViewById(R.id.show_name);

        }
    }

    @Override
    public int getItemViewType(int position){
        if(messages.get(position).getName().equals(AllMethods.name)){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }

    }
}

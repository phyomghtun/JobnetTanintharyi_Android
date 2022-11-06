package com.jobnet.tanintharyi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jobnet.tanintharyi.Adapter.MessageAdapter;
import com.jobnet.tanintharyi.Models.AllMethods;
import com.jobnet.tanintharyi.Models.Message;
import com.jobnet.tanintharyi.Models.User2;
import com.jobnet.tanintharyi.R;

import java.util.ArrayList;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference messagedb;
    MessageAdapter messageAdapter;
    User2 u;
    List<Message> messages;

    RecyclerView rvMessage;
    ImageView imgButton;
    EditText etMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setTitle("Jobnet Tanintharyi Group Chat");
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }
    private void init(){
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        u=new User2();

        rvMessage=findViewById(R.id.rvMessage);
        imgButton=findViewById(R.id.btnSend);
        etMessage=findViewById(R.id.etmessage);
        imgButton.setOnClickListener(this);
        messages=new ArrayList<>();

    }
    @Override
    public void onClick(View view) {

        if(!TextUtils.isEmpty(etMessage.getText().toString())){
            Message message=new Message(etMessage.getText().toString(),u.getName(),u.getProfile());
            etMessage.setText("");
            messagedb.push().setValue(message);
        }else{
            Toast.makeText(getApplicationContext(),"Cannot Send",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser currentUser=auth.getCurrentUser();
        u.setUid(currentUser.getUid());
        u.setEmail(currentUser.getEmail());

        database.getReference("Users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u=snapshot.getValue(User2.class);
                u.setUid(currentUser.getUid());
                AllMethods.name=u.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messagedb=database.getReference("messages");
        messagedb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message=snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());
                messages.add(message);
                displayMessages(messages);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message=snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());

                List<Message> newMessages=new ArrayList<Message>();
                for(Message m:messages){
                    if(m.getKey().equals(message.getKey())){
                        newMessages.add(message);
                    }else {
                        newMessages.add(m);
                    }
                }
                messages=newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message message=snapshot.getValue(Message.class);
                message.setKey(snapshot.getKey());
                List<Message> newMessages=new ArrayList<Message>();
                for(Message m:messages){
                    if(!m.getKey().equals(message.getKey())){
                        newMessages.add(m);
                    }
                }
                messages=newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        messages=new ArrayList<>();
    }

    private void displayMessages(List<Message> messages) {
        rvMessage.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        messageAdapter=new MessageAdapter(GroupChatActivity.this,messages,messagedb);
        rvMessage.setAdapter(messageAdapter);

    }
}

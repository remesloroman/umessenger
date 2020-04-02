package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Message {
    String who,whom,message;
    Message(){}
    Message(String who,String whom,String message){
        this.who = who;
        this.whom = whom;
        this.message = message;
    }
}
public class ChatActivity extends AppCompatActivity {
    String whom,username;
    EditText message;
    ImageView send;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");
    LinearLayout linLayout;
    ArrayList<String> messages = new ArrayList<>();
    void update(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linLayout.removeAllViews();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Map<String, String> map = (Map) postSnapshot.getValue();
                    String sender = map.get("sender");
                    String receiver = map.get("receiver");
                    String message = map.get("message");
                    if (sender.equals(username) && receiver.equals(whom)) {
                        View item = layoutInflater.inflate(R.layout.item, linLayout, false);
                        TextView lbl = item.findViewById(R.id.userMessage);
                        lbl.setText(username + ":\n" + message);
                        linLayout.addView(item);
                    }
                    if (sender.equals(whom) && receiver.equals(username)) {
                        View item = layoutInflater.inflate(R.layout.item, linLayout, false);
                        TextView lbl = item.findViewById(R.id.userMessage);
                        lbl.setText(whom + ":\n" + message);
                        linLayout.addView(item);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    LayoutInflater layoutInflater;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true) {
                update();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        layoutInflater = getLayoutInflater();
        Intent intent = getIntent();
        linLayout = findViewById(R.id.linLayout);
        whom = intent.getStringExtra("whom");
        username = intent.getStringExtra("username");
        TextView title = findViewById(R.id.title);
        title.setText(whom);
        message = findViewById(R.id.message);
        send = this.findViewById(R.id.send);
        update();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = message.getText().toString();
                if(mess.length() > 0 && mess.length() < 200) {
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("sender", username);
                    msg.put("receiver", whom);
                    msg.put("message", mess);
                    myRef.push().setValue(msg);
                    message.setText("");
                }
            }
        });
        thread.start();
    }
}

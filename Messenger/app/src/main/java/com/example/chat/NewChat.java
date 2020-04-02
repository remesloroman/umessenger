package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewChat extends AppCompatActivity {
    ListView lv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> users;
    String username;
    ArrayAdapter<String> adapter;
    boolean isOk = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        users = new ArrayList<>();
        lv = findViewById(R.id.lvAdd);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String,Object> user = document.getData();
                    users.add(user.get("username").toString());
                }
                adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,users);
                lv.setAdapter(adapter);

            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                final String addedUser = tv.getText().toString();

                db.collection("chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String,Object> chat = document.getData();
                            String who = chat.get("who").toString();
                            String whom = chat.get("whom").toString();
                            Log.d("AAAAAHAHAHHAHA",who);
                            Log.d("AAAAAHAHAHHAHA",whom);
                            if(who.equals(username) && whom.equals(addedUser)) isOk = false;
                            else if(whom.equals(username) && who.equals(addedUser)) isOk = false;
                        }
                        if((!username.equals(addedUser)) && isOk) {
                            Toast.makeText(getApplicationContext(), "New chat added!", Toast.LENGTH_LONG).show();
                            Map<String, Object> usr = new HashMap<>();
                            usr.put("who", username);
                            usr.put("whom", addedUser);
                            db.collection("chats").add(usr);
                        }else {
                            Toast.makeText(getApplicationContext(), "Invalid choice", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }

}

package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        TextView tv = findViewById(R.id.usernameL);
        tv.setText(username);
    }

    public void backBtn(View view) {
        Intent intent = new Intent(this,MainPage.class);
        intent.putExtra("username",username);
        startActivity(intent);
    }
}

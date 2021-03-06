package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
class User implements Serializable {
    String username,password;
    User(){}
    User(String username,String password){
        this.username = username;
        this.password = password;
    }
}
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button create;
    EditText username,password,repeat_password;

    String un;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    boolean isOk = true;
    void goHome() {
        Intent toHome = new Intent(this,MainPage.class);
        startActivity(toHome);
        Log.d("AKAJDO","We in the main page");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            File path = getFilesDir();
            File file = new File(path, "data.txt");
            if(file.exists()){
                Log.d("AHAHHAHAHAHA","*--------------------------------------Existsts--------------------------------------*");
                int length = (int) file.length();

                byte[] bytes = new byte[length];

                FileInputStream in = new FileInputStream(file);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }
                String contents = new String(bytes);
                Intent intent = new Intent(this,MainPage.class);
                intent.putExtra("username",contents);
                startActivity(intent);
            } else Log.d("AHAHHAHAHAHA","*--------------------------------------DONT_Existsts--------------------------------------*");
        } catch (Exception e) {
            e.printStackTrace();
        }
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        repeat_password = findViewById(R.id.repeat_password);
        create = findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                un = username.getText().toString();
                final String pw = password.getText().toString();
                String pwa = repeat_password.getText().toString();
                if(!un.isEmpty()){
                    if(!pw.isEmpty()){
                        if(pw.equals(pwa)){
                            // ADD ACCOUNT TO DB
                            db.collection("users")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Map<String,Object> userTest = document.getData();
                                                    if(userTest.get("username").equals(un)) isOk = false;
                                                    Log.d("AHAHHA",userTest.get("username").toString());
                                                }
                                                if(isOk){
                                                    try{
                                                        File path = getApplicationContext().getFilesDir();
                                                        File file = new File(path, "data.txt");
                                                        FileOutputStream stream = new FileOutputStream(file);
                                                        stream.write(un.getBytes());
                                                    }catch (Exception e){}
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("username",un);
                                                    user.put("password",pw);
                                                    db.collection("users").add(user);
                                                    Toast.makeText(getApplicationContext(),"Account created",Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(getApplicationContext(),MainPage.class);
                                                    intent.putExtra("username",un);
                                                    startActivity(intent);
                                                }else {
                                                    Toast.makeText(getApplicationContext(),"POLBU LOSHOKOY ILI DA? TAKOY LOGIN EST",Toast.LENGTH_LONG).show();
                                                    isOk = true;
                                                }
                                            }
                                        }
                                    });
                            /////////////////////
                        }else {
                            Toast.makeText(getApplicationContext(),"Passwords dont simular",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Invalid password!",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Invalid login!",Toast.LENGTH_LONG).show();
                }

            }
        });
        TextView login = findViewById(R.id.loginText);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent log = new Intent(this,LoginActivity.class);
        startActivity(log);
    }
}

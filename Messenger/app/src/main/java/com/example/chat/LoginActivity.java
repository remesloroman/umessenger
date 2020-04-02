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
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText password,loginInput;
    Button enter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean isOk=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        password = findViewById(R.id.passwordInput);
        loginInput = findViewById(R.id.loginInput);
        enter = findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String loginVal = loginInput.getText().toString();
                final String passVal = password.getText().toString();
                if(!loginVal.equals("") && !loginVal.equals(" ")){
                    if(!passVal.equals("") && !passVal.equals(" ")){
                        // DB REQUEST
                        Log.d("GAGGAGA","All values correct");
                        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d("GAGGAGA","Good conn. Starting cycle");
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String,Object> user = document.getData();
                                        Log.d("GAGGAGA",user.get("username").toString());
                                        if(user.get("username").equals(loginVal)){
                                            Log.d("GAGGAGA","username(db) == username(input)");
                                            if(user.get("password").equals(passVal)){
                                                Log.d("GAGGAGA","password(db) == password(input)");
                                                isOk = true;
                                            }
                                        }
                                    }
                                    if(isOk){
                                        try {
                                            File path = getApplicationContext().getFilesDir();
                                            File file = new File(path, "data.txt");
                                            FileOutputStream stream = new FileOutputStream(file);
                                            stream.write(loginVal.getBytes());
                                        }
                                        catch (IOException e) {
                                            Log.e("Exception", "File write failed: " + e.toString());
                                        }
                                        Log.d("GAGGAGA","Start activity main page");
                                        isOk = false;
                                        Intent intent = new Intent(getApplicationContext(),MainPage.class);
                                        intent.putExtra("username",loginVal);
                                        startActivity(intent);
                                    }else {
                                        Log.d("GAGGAGA","Invalid data.");
                                        Toast.makeText(getApplicationContext(),"This user doesnt found",Toast.LENGTH_LONG).show();
                                    }
                                }else {
                                    Log.d("GAGGAGA","Problems with connection");
                                    //Toast.makeText(getApplicationContext(),"Invalid connection to database!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        /////////////
                    }else {
                        Toast.makeText(getApplicationContext(),"Invalid password!",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Invalid login!",Toast.LENGTH_LONG).show();
                }
            }
        });
        TextView backToSign = findViewById(R.id.backToSign);
        backToSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

package com.example.chat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainPage extends AppCompatActivity {
    private TextView tvl;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String username;
    ListView lvMain;
    Toolbar toolbar;
    ArrayList<Map<String, Object>> data;
    DrawerLayout drawer;
    NavigationView navigationView;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newChat = new Intent(getApplicationContext(),NewChat.class);
                newChat.putExtra("username",username);
                startActivity(newChat);
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        View header = navigationView.getHeaderView(0);
        tvl = header.findViewById(R.id.TextOnNav);
        tvl.setText(username);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_profile:
                        Intent toProf = new Intent(getApplicationContext(),ProfileActivity.class);
                        toProf.putExtra("username",username);
                        startActivity(toProf); break;
                    case R.id.nav_settings:
                        Toast.makeText(getApplicationContext(),"In develop",Toast.LENGTH_SHORT).show();
                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);break;
                    case R.id.nav_logout:
                        File path = getFilesDir();
                        File file = new File(path, "data.txt");
                        file.delete();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        lvMain = findViewById(R.id.lvMain);
        data = new ArrayList<>();
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // УРА! ТУТ Я ПОЛУЧАЮ ИМЯ ЮСЕРА НА КОТОРОГО ПОЛЬЗОВАТЕЛЬ КЛИКНУЛ И ОТПРАВЛЯЮ ЕГО В ChatActivity
                TextView tv = view.findViewById(R.id.userMessage);
                String whom = tv.getText().toString();
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                intent.putExtra("whom",whom);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_view);
        return NavigationUI.navigateUp(navController, drawer)
                || super.onSupportNavigateUp();
    }
    @Override
    public void onResume() {
        db.collection("chats").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                data = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String,Object> msg = document.getData();

                        if(msg.get("who").toString().equals(username)) {
                            Map<String, Object> m;
                            m = new HashMap<String, Object>();
                            m.put("message",msg.get("whom"));
                            m.put("image", R.drawable.user);
                            data.add(m);
                        }
                        if(msg.get("whom").toString().equals(username)) {
                            Map<String, Object> m;
                            m = new HashMap<String, Object>();
                            m.put("message",msg.get("who"));
                            m.put("image", R.drawable.user);
                            data.add(m);
                        }


                    }
                    String from[] = {"image","message"};
                    int[] to = {R.id.userImg,R.id.userMessage};
                    SimpleAdapter sAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.item, from, to);
                    lvMain.setAdapter(sAdapter);
                }
            }
        });
        super.onResume();
    }

}

package com.example.a16031940.sgbaby;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mainToolbar = findViewById(R.id.MainToolBar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("SGBABY");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToLogin();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.signOut:

                logout();

                return true;

            case R.id.actionSettings:

                Intent settingsIntent = new Intent(Home.this,SetUpActivity.class);
                startActivity(settingsIntent);
                return true;

            default:
                return false;
        }

    }

    public void sendToLogin(){
        Intent intent = new Intent(Home.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void logout(){
        mAuth.signOut();
        sendToLogin();
    }


}

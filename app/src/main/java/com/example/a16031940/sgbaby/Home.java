package com.example.a16031940.sgbaby;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private FloatingActionButton addPostBtn;
    private String current_user_id;

    private BottomNavigationView mainbottomNav;

    private RecyclerView blog_recycler_view;
    private List<BlogPost> blogPostList;
    private BlogRecyclerViewAdapter blogRecyclerViewAdapter;


    private GridHome gridHome;
    private LinearHome linearHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        blog_recycler_view = findViewById(R.id.BlogrecyclerView);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mainToolbar = findViewById(R.id.MainToolBar);
        addPostBtn = findViewById(R.id.add_post_btn);
//        blogPostList = new ArrayList<>();
//        blogRecyclerViewAdapter = new BlogRecyclerViewAdapter(blogPostList);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("SGBABY");
//
//blog_recycler_view.setLayoutManager(new LinearLayoutManager(getBaseContext()));
//blog_recycler_view.setAdapter(blogRecyclerViewAdapter);
        if(mAuth.getCurrentUser() != null) {

            mainbottomNav = findViewById(R.id.mainBottomNav);

            // FRAGMENTS
            gridHome = new GridHome();
            linearHome = new LinearHome();

            initializeFragment();

            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                    switch (item.getItemId()) {

                        case R.id.grid:

                            replaceFragment(gridHome, currentFragment);
                            return true;

                        case R.id.linear:

                            replaceFragment(linearHome, currentFragment);
                            return true;

                        default:
                            return false;


                    }

                }
            });


            addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(Home.this, NewPostActivity.class);
                    startActivity(newPostIntent);

                }
            });

        }
//        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
//                    if(doc.getType() == DocumentChange.Type.ADDED){
//                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
//                        blogPostList.add(blogPost);
//
//                        blogRecyclerViewAdapter.notifyDataSetChanged();
//
//                    }
//                }
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToLogin();
        }else{
            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if(!task.getResult().exists()){
                            Intent setUpIntent = new Intent(Home.this,SetUpActivity.class);
                            startActivity(setUpIntent);
                            finish();
                        }
                    }else{
                        String error = task.getException().getMessage().toString();
                        Toast.makeText(Home.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
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

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, gridHome);
        fragmentTransaction.add(R.id.main_container, linearHome);

        fragmentTransaction.hide(gridHome);

        fragmentTransaction.commit();

    }


    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == linearHome){

            fragmentTransaction.hide(gridHome);

        }

        if(fragment == gridHome){

            fragmentTransaction.hide(linearHome);

        }

        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }

}

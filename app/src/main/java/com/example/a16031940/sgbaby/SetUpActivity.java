package com.example.a16031940.sgbaby;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SetUpActivity extends AppCompatActivity {

    ImageView profileImage;
    Button setUpBtn;
    EditText setupName;
    private Uri mainImageURI = null;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private ProgressBar setupProgess;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;
    private Boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        profileImage = findViewById(R.id.ProfileImage);
        setupName = findViewById(R.id.username);
        setUpBtn = findViewById(R.id.setupbtn);
        setupProgess = findViewById(R.id.setupProgress);
        user_id = firebaseAuth.getCurrentUser().getUid();

        setupProgess.setVisibility(View.VISIBLE);
        setUpBtn.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);

                        setupName.setText(name);
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.defaultprofile);
                        Glide.with(SetUpActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(profileImage);
                        Toast.makeText(SetUpActivity.this, image, Toast.LENGTH_LONG).show();

                    }
                } else {
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(SetUpActivity.this, "FireStore retrieve error : " + error, Toast.LENGTH_LONG).show();
                }

                setupProgess.setVisibility(View.INVISIBLE);
                setUpBtn.setEnabled(true);

            }
        });

        setUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = setupName.getText().toString();
                if (!TextUtils.isEmpty(username) && mainImageURI != null) {

                    setupProgess.setVisibility(View.VISIBLE);

                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();
                        final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storeFirestore(task, username);

                                } else {
                                    Toast.makeText(SetUpActivity.this, "Error is : " + task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                    setupProgess.setVisibility(View.INVISIBLE);

                                }
                            }
                        });

                    }else {
                        storeFirestore(null, username);
                    }
                }
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetUpActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(SetUpActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();

                        ActivityCompat.requestPermissions(SetUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {


                        BringImagePicker();

                    }
                } else {

                    BringImagePicker();

                }

            }
        });
    }

    private void storeFirestore(Task<UploadTask.TaskSnapshot> task, String username) {
        String download_uri;
        if (task != null) {
            download_uri = task.getResult().getDownloadUrl().toString();
        } else {
            download_uri = mainImageURI.toString();
        }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("image", download_uri);
        Log.d("downloadURI", download_uri);

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(SetUpActivity.this, "User settings updated!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SetUpActivity.this, Home.class);
                    startActivity(intent);
                    finish();

                } else {
                    String error = task.getException().getMessage().toString();
                    Toast.makeText(SetUpActivity.this, "FireStore error : " + error, Toast.LENGTH_LONG).show();
                }
                setupProgess.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetUpActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                profileImage.setImageURI(mainImageURI);
                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}

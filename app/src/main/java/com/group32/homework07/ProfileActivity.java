package com.group32.homework07;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity{

    public static final int USER_PROFILE_REQUEST_CODE = 1;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;



    private EditText editFirstname;
    private EditText editLastname;
    private Switch switchGender;
    private ImageButton imageButtonProfilePicture;

    private Button buttonSave, buttonCancel, buttonLogout;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup ui elements
        editFirstname = (EditText) findViewById(R.id.editProfileFirstname);
        editLastname = (EditText) findViewById(R.id.editProfileLastname);
        switchGender = (Switch) findViewById(R.id.editProfileSwitch);
        buttonSave = (Button) findViewById(R.id.buttonProfileSave);
        buttonCancel = (Button) findViewById(R.id.buttonProfileCancel);
        buttonLogout = (Button) findViewById(R.id.buttonProfileSignOut);
        imageButtonProfilePicture = (ImageButton) findViewById(R.id.imageProfilePicture);

        mAuth = FirebaseAuth.getInstance();
        //mAuth.addAuthStateListener(this);

        currentUser = new User();
        currentUser.setGender(false);

        // Check user is logged in and display profile data
        if (mAuth.getCurrentUser() != null) {
            DatabaseReference userDatabaseReference =  FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());
            userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null){
                        currentUser = user;
                        displayUser();
                    } else {
                        // If there is no profile the user MUST save the profile information
                        // Therefore no cancel is allowed
                        buttonCancel.setVisibility(View.GONE);
                        currentUser = new User();
                        currentUser.setEmail(mAuth.getCurrentUser().getEmail());
                        currentUser.setUid(mAuth.getCurrentUser().getUid());
                        currentUser.setGender(false);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        Uri photoUrl = null;


        // Can we check if name is already provided?
        for (UserInfo userData:mAuth.getCurrentUser().getProviderData()) {
            if (userData.getDisplayName() != null && userData.getDisplayName().length() > 0) {
                currentUser.setFirstName(userData.getDisplayName().split(" ")[0]);
                currentUser.setLastName(userData.getDisplayName().split(" ")[1]);
            }
        }
        displayUser();

        mDatabase = FirebaseDatabase.getInstance();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        imageButtonProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePicture();
            }
        });
    }

    private void changePicture(){
        Intent getPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getPhotoIntent.setType("image/*");
        startActivityForResult(getPhotoIntent,USER_PROFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == USER_PROFILE_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                InputStream profilePictureInputStream = this.getContentResolver().openInputStream(data.getData());
                StorageReference profilePictureReference = FirebaseStorage.getInstance().getReference("profilePictures").child(mAuth.getCurrentUser().getUid());
                profilePictureReference.putStream(profilePictureInputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        displayUser();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void displayUser(){
        editFirstname.setText(currentUser.getFirstName());
        editLastname.setText(currentUser.getLastName());
        switchGender.setChecked(currentUser.getGender());

        //Check if there is profile picture in the storage
        FirebaseStorage.getInstance().getReference("profilePictures").child(mAuth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(ProfileActivity.this).load(uri).into(imageButtonProfilePicture);
            }
        });
    }

    private void saveUser(){
        String firstname = editFirstname.getText().toString();
        String lastname = editLastname.getText().toString();

        if (firstname.length() == 0){
            editFirstname.setError("First name cannot be empty");
            return;
        }
        if (lastname.length()==0){
            editLastname.setError("Last name cannot be empty");
            return;
        }
        Boolean gender = switchGender.isChecked();

        currentUser.setFirstName(firstname);
        currentUser.setLastName(lastname);
        currentUser.setGender(gender);

        DatabaseReference userDatabaseReference = mDatabase.getReference("users").child(mAuth.getCurrentUser().getUid());
        userDatabaseReference.setValue(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(ProfileActivity.this,InboxActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signOut(){
        mAuth.signOut();
        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}

package com.group32.homework07;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;

    private EditText editFirstname;
    private EditText editLastname;
    private Switch switchGender;

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

        mAuth = FirebaseAuth.getInstance();
        //mAuth.addAuthStateListener(this);

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
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


        mDatabase = FirebaseDatabase.getInstance();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser();
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }


    private void displayUser(){
        editFirstname.setText(currentUser.getFirstName());
        editLastname.setText(currentUser.getLastName());
        switchGender.setChecked(currentUser.getGender());
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
        finish();
    }
}

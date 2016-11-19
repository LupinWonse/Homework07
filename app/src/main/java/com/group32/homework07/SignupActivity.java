package com.group32.homework07;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button buttonSignup, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();


        buttonSignup = (Button) findViewById(R.id.buttonSignUpSignup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        buttonCancel = (Button) findViewById(R.id.buttonSignUpCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void signup(){
        // Validation
        EditText editEmail, editPassword, editRepeat, editFirstname, editLastname;
        Switch switchGender;

        editEmail = (EditText) findViewById(R.id.editSignUpEmail);
        editFirstname = (EditText) findViewById(R.id.editSignUpFirstname);
        editLastname = (EditText) findViewById(R.id.editSignUpLastname);
        editPassword = (EditText) findViewById(R.id.editSignUpPassword);
        editRepeat = (EditText) findViewById(R.id.editSignUpRepeat);

        switchGender = (Switch) findViewById(R.id.switchSignUpGender);

        final String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String repeat = editRepeat.getText().toString();
        final String firstname = editFirstname.getText().toString();
        final String lastname = editLastname.getText().toString();
        final Boolean gender = switchGender.isChecked();

        boolean inputValid = true;

        if (email.length() == 0){
            editEmail.setError("Please enter your email");
            inputValid = false;
        }
        if (password.length() == 0){
            editPassword.setError("Please enter a password");
            inputValid = false;
        }
        if (firstname.length() == 0){
            editFirstname.setError("Please enter your first name");
            inputValid = false;
        }
        if (lastname.length() == 0){
            editLastname.setError("Please enter your last name");
            inputValid = false;
        }
        if (repeat.length() == 0){
            editRepeat.setError("Please repeat your password");
            inputValid = false;
        }
        if (!password.equals(repeat)){
            editRepeat.setError("Passwords do not match");
            inputValid = false;
        }
        // If we encountered any error stop here
        if (!inputValid){
            return;
        }


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(SignupActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                } else {
                    // Add the user data into the database:
                    String userId = task.getResult().getUser().getUid();
                    User user = new User(firstname,lastname,userId,email,gender);
                    FirebaseDatabase.getInstance().getReference("users").child(userId).setValue(user);
                    finish();
                }
            }
        });
    }


}

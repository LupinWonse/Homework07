package com.group32.homework07;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity implements ConversationRecyclerViewAdapter.IConversationListHandler{

    public static int CHOOSE_CONTACT_REQUEST_CODE = 1;
    public static int CHOOSE_CONTACT_SUCCESS_RESULT_CODE = 1;

    private FirebaseAuth mAuth;
    private User currentUser;
    private ArrayList<Conversation> conversations;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuProfile:
                Intent intent = new Intent(this,ProfileActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mAuth = FirebaseAuth.getInstance();
        // Check if the user was somehow logged out if so close the activity
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    finish();
                }
            }
        });

        if(mAuth.getCurrentUser() != null){
            FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentUser = dataSnapshot.getValue(User.class);
                    ((TextView) findViewById(R.id.textInboxUsername)).setText(currentUser.fullName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        findViewById(R.id.imageButtonInboxAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InboxActivity.this,ContactsActivity.class);
                startActivityForResult(intent, CHOOSE_CONTACT_REQUEST_CODE);
            }
        });

        RecyclerView conversationList = (RecyclerView) findViewById(R.id.recylcerConversationList);
        conversationList.setAdapter(new ConversationRecyclerViewAdapter(this));
        conversationList.setLayoutManager(new LinearLayoutManager(this));

        // Setup the action bar
        ActionBar actionBar = getSupportActionBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_CONTACT_REQUEST_CODE && resultCode == CHOOSE_CONTACT_SUCCESS_RESULT_CODE) {
            String uid = data.getStringExtra("uid");

            // Create empty conversation and add this to the database
            DatabaseReference userConversations = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid());
            String conversationId =  userConversations.push().getKey();
            Conversation newConversation = new Conversation(uid,conversationId);
            userConversations.child(conversationId).setValue(newConversation);


            // Pass the conversation object to the chat activity
        }
    }

    @Override
    public void displayConversation(String conversationId, String toUser) {
        Intent intent = new Intent(this,ChatActivity.class);
        intent.putExtra("conversationId",conversationId);
        intent.putExtra("toUser",toUser);
        startActivity(intent);
    }

}

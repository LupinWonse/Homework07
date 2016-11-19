package com.group32.homework07;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity implements ConversationRecyclerViewAdapter.IConversationListHandler{

    public static int CHOOSE_CONTACT_REQUEST_CODE = 1;

    private FirebaseAuth mAuth;
    private User currentUser;
    private ArrayList<Conversation> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mAuth = FirebaseAuth.getInstance();

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_CONTACT_REQUEST_CODE) {
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

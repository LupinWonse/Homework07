package com.group32.homework07;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ContactsActivity extends AppCompatActivity implements ContactsRecyclerViewAdapter.IContactsListHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        RecyclerView contactsList = (RecyclerView) findViewById(R.id.recyclerContactsList);
        contactsList.setAdapter(new ContactsRecyclerViewAdapter(this));
        contactsList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onContactChosen(String uid) {
        Intent intent = new Intent();
        intent.putExtra("uid",uid);
        setResult(InboxActivity.CHOOSE_CONTACT_SUCCESS_RESULT_CODE,intent);
        finish();
    }
}

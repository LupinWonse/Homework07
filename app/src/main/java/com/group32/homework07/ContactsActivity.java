package com.group32.homework07;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        RecyclerView contactsList = (RecyclerView) findViewById(R.id.recyclerContactsList);
        contactsList.setAdapter(new ContactsRecyclerViewAdapter());
        contactsList.setLayoutManager(new LinearLayoutManager(this));
    }
}

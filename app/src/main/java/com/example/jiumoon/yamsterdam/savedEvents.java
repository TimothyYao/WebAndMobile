package com.example.jiumoon.yamsterdam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class savedEvents extends AppCompatActivity {
    ArrayList<String> savedNames;
    ArrayAdapter<String> arrayAdapter;
    ListView savedView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        savedView = (ListView) this.findViewById(R.id.savedView);
        savedNames = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, savedNames);
        savedView.setAdapter(arrayAdapter);


        // Reading all contacts
        DatabaseHandler db = DatabaseHandler.getInstance(getApplicationContext());
        final List<Event> events = db.getAllEvents();
        for (Event event : events) {
            String log = event.getName();
            savedNames.add(event.getName());
            Log.d("Name: ", log);
            arrayAdapter.notifyDataSetChanged();
        }

        savedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //String selectedEvent[] = {myEvents.get(position).getName(), myEvents.get(position).getDate(), myEvents.get(position).getDescription(), myEvents.get(position).getAddress()};

                Intent intent = new Intent(savedEvents.this, showEvent.class);
                intent.putExtra("extra_name", events.get(position).getName());
                intent.putExtra("extra_date", events.get(position).getDate());
                intent.putExtra("extra_description", events.get(position).getDescription());
                intent.putExtra("extra_address", events.get(position).getAddress());
                intent.putExtra("extra_id", events.get(position).getID());
                //TODO:Pass in event details...
                //intent.putExtra(EXTRA_MESSAGE2, selectedEventDetails);
                startActivity(intent);
            }
        });
        Button backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(savedEvents.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}

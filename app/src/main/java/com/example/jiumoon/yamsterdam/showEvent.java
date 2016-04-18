package com.example.jiumoon.yamsterdam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class showEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_show_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        //Get the info
        final String name = intent.getStringExtra("extra_name");
        final String date = intent.getStringExtra("extra_date");
        final String description = intent.getStringExtra("extra_description");
        final String address = intent.getStringExtra("extra_address");

        //Log.d("swag", message);
        TextView toolbar_text = (TextView)findViewById(R.id.toolbar_title);
        toolbar_text.setText(name);

        //Update event page txt
        TextView date_text = (TextView)findViewById(R.id.dateView);
        date_text.setText(date);
        TextView description_text = (TextView)findViewById(R.id.descriptionView);
        description_text.setText(description);
        TextView address_text = (TextView)findViewById(R.id.addressView);
        address_text.setText(address);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHandler db = DatabaseHandler.getInstance(getApplicationContext());
                db.addEvent(new Event(name, date, description, address));
                Snackbar.make(view, "Saved Events: " + db.getEventsCount(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}

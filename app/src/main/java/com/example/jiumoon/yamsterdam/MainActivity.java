package com.example.jiumoon.yamsterdam;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.jiumoon.yamsterdam.SELECTEDEVENT";
    public final static String EXTRA_MESSAGE2 = "com.example.jiumoon.yamsterdam.SELECTEDEVENTDETAILS";
    ListView responseView;
    ProgressBar progressBar;
    EditText locationText;
    int eventCount = 0;
    String location = "Charlottesville";
    static final String API_KEY = "mRMksGwGMhrwbC94";
    static final String API_URL = "http://api.eventful.com/json/events/search?...";
    ArrayList<String> myEvents;
    ArrayList<String> eventDetails;
    ArrayAdapter<String> arrayAdapter;
    //String[] from = new String[] {"rowid", "col_1", "col_2", "col_3"};
    //int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        responseView = (ListView) findViewById(R.id.responseView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        locationText = (EditText) findViewById(R.id.locationText);
        //TODO: Change this to a SimpleAdapter to show both title and date
        myEvents = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myEvents);
        responseView.setAdapter(arrayAdapter);

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = locationText.getText().toString();
                new RetrieveFeedTask().execute();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(locationText.getWindowToken(), 0);
            }
        });
        responseView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String selectedEvent = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this,showEvent.class);
                intent.putExtra(EXTRA_MESSAGE, selectedEvent);
                //TODO:Pass in event details...
                //intent.putExtra(EXTRA_MESSAGE2, selectedEventDetails);
                startActivity(intent);
            }
        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL(API_URL + "&location=" + location + "&app_key=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    //Read events
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);


            // TODO: check this.exception
            // TODO: do something with the feed
            int keysCount = 0;
            try {
                JSONObject object = new JSONObject(response);
                object = object.getJSONObject("events");
                JSONArray events = object.getJSONArray("event");
                JSONObject firstEvent = events.getJSONObject(0);
                Iterator<String> keys = firstEvent.keys();
                String out = "";
//                while (keys.hasNext()) {
//                    out += keys.next() + "\n";
//                }
                myEvents.clear();
                for (int i = 0; i < events.length(); i++) {
                    JSONObject e = events.getJSONObject(i);
                    myEvents.add(e.getString("title"));
                    //Log.d("hello",e.getString("title"));
                    //out += e.getString("title");
                    //out += "\n";
                }
                //Log.d("hello",myEvents.toString());
                arrayAdapter.notifyDataSetChanged();


                //responseView.setText(out);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

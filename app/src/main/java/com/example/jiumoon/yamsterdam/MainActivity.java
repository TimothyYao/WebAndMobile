package com.example.jiumoon.yamsterdam;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener {

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    //Speech recognition
    //private SpeechRecognizer mSpeechRecognizer;

    ListView responseView;
    ProgressBar progressBar;
    EditText locationText;
    double latitude;
    double longitude;
    boolean useLocation;
    int within = 25;
    int eventCount = 0;
    String location = "Charlottesville";
    static final String API_KEY = "mRMksGwGMhrwbC94";
    static final String API_URL = "http://api.eventful.com/json/events/search?...";
    ArrayList<String> eventNames;
    ArrayList<Event> myEvents;
    ArrayAdapter<String> arrayAdapter;
    private final int SPEECH_RECOGNITION_CODE = 1;
    private Button btnMicrophone;
    private Button queryButton;
    //String[] from = new String[] {"rowid", "col_1", "col_2", "col_3"};
    //int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3, R.id.item4 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Text to speech thing
        btnMicrophone = (Button) findViewById(R.id.btn_mic);
        btnMicrophone.setOnClickListener(new View.OnClickListener(){
            @Override
        public void onClick (View v){
                startSpeechToText();
            }
        });

        responseView = (ListView) findViewById(R.id.responseView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        locationText = (EditText) findViewById(R.id.locationText);
        //TODO: Change this to a SimpleAdapter to show both title and date
        eventNames = new ArrayList<String>();
        myEvents = new ArrayList<Event>();
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, eventNames);
        responseView.setAdapter(arrayAdapter);


        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = locationText.getText().toString().replaceAll(" ","%20");
                Log.d("locationtext", location);
                useLocation = false;
                new RetrieveFeedTask().execute();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(locationText.getWindowToken(), 0);
            }
        });
        responseView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //String selectedEvent[] = {myEvents.get(position).getName(), myEvents.get(position).getDate(), myEvents.get(position).getDescription(), myEvents.get(position).getAddress()};

                Intent intent = new Intent(MainActivity.this, showEvent.class);
                intent.putExtra("extra_name", myEvents.get(position).getName());
                intent.putExtra("extra_date", myEvents.get(position).getDate());
                intent.putExtra("extra_description", myEvents.get(position).getDescription());
                intent.putExtra("extra_address", myEvents.get(position).getAddress());
                intent.putExtra("extra_id", -1);
                //TODO:Pass in event details...
                //intent.putExtra(EXTRA_MESSAGE2, selectedEventDetails);
                startActivity(intent);
            }
        });

        Button locationButton = (Button) findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(locationText.getWindowToken(), 0);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    useLocation = true;
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    location = latitude + "," + longitude;
                    new RetrieveFeedTask().execute();
                } else {
                    eventNames.add("location not found ):");
                    arrayAdapter.notifyDataSetChanged();
                }


            }
        });

        //Launch Saved Events
        Button savedButton = (Button) findViewById(R.id.savedEventsButton);
        savedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, savedEvents.class);
                startActivity(intent);
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    //Voice Recognition
    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say a location");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition unavailable for your device",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    Log.d("VOICE TO TEXT",text);
                    locationText.setText(text, TextView.BufferType.EDITABLE);
                    queryButton.performClick();
                    //txtOutput.setText(text);
                }
                break;
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        eventNames.clear();
        myEvents.clear();
        Log.d("swag", "" + CurrentEvents.events.size());
        for (Event event : CurrentEvents.events) {
            eventNames.add(event.getName());
            myEvents.add(event);
        }
        arrayAdapter.notifyDataSetChanged();
        checkPlayServices();

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //responseView.setText("");
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL(API_URL + "&location=" + location + "&within=" + within + "&app_key=" + API_KEY);
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
                if (object.isNull("events")) {
                    eventNames.add("No events found ):");
                    arrayAdapter.notifyDataSetChanged();
                    return;
                }
                object = object.getJSONObject("events");
                JSONArray events = object.getJSONArray("event");
                JSONObject firstEvent = events.getJSONObject(0);
                Iterator<String> keys = firstEvent.keys();
                String out = "";
//                while (keys.hasNext()) {
//                    out += keys.next() + "\n";
//                }
                eventNames.clear();
                myEvents.clear();
                CurrentEvents.events.clear();
                for (int i = 0; i < events.length(); i++) {
                    eventCount++;
                    JSONObject e = events.getJSONObject(i);
                    String title = e.getString("title");
                    String date = e.getString("start_time");
                    String description = e.getString("description");
                    String address = e.getString("venue_address");
                    eventNames.add(title);
                    myEvents.add(new Event(title,date,description,address));
                    CurrentEvents.events.add(new Event(title,date,description,address));

                    //Log.d("hello",e.getString("title"));
                    //out += e.getString("title");
                    //out += "\n";
                }
                arrayAdapter.notifyDataSetChanged();
                Log.d("swag", "b " + myEvents.size());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

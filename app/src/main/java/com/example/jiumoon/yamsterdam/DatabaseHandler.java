package com.example.jiumoon.yamsterdam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler sInstance;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "eventsManager";

    // Events table name
    private static final String TABLE_EVENTS = "events";

    // Events Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DATE = "date";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_ADDRESS = "address";

    public static synchronized DatabaseHandler getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DATE + " TEXT," + KEY_DESCRIPTION + " TEXT,"
                +KEY_ADDRESS + " TEXT" + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

        // Create tables again
        onCreate(db);
    }

    // Adding new Event
    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, event.getName());
        values.put(KEY_DATE, event.getDate());
        values.put(KEY_DESCRIPTION, event.getDescription());
        values.put(KEY_ADDRESS, event.getAddress());

        // Inserting Row
        db.insert(TABLE_EVENTS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Event
    public Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EVENTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_DATE, KEY_DESCRIPTION, KEY_ADDRESS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Event event = new Event(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4));
        // return Event
        return event;
    }

    public List<Event> getAllEvents() {
        List<Event> EventList = new ArrayList<Event>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4));
                // Adding Event to list
                EventList.add(event);
            } while (cursor.moveToNext());
        }

        // return Event list
        return EventList;
    }

    public int getEventsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EVENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    public void deleteEvent(int id) {
        if (id < 0) {
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
}
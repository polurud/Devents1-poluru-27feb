package com.dartmouth.kd.devents;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.SQLException;

import java.util.ArrayList;

/**
 * Created by kathrynflattum on 2/25/18.
 */

public class CampusEventDbHelper extends SQLiteOpenHelper {
    // Database name string
    public static final String DATABASE_NAME = "CampusEventsDB";
    // Table name string. (Only one table)
    private static final String TABLE_EVENT_ENTRIES = "EVENTS";

    // Version code
    private static final int DATABASE_VERSION = 1;

    // Table schema, column names
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "event_title";
    public static final String KEY_DATE = "event_date";
    public static final String KEY_START = "event_start";
    public static final String KEY_END = "event_end";
    public static final String KEY_LOCATION = "event_location";
    public static final String KEY_DESCRIPTION = "event_description";
    public static final String KEY_URL = "event_url";
    public static final String KEY_LATITUDE = "event_lat";
    public static final String KEY_LONGITUDE = "event_long";

    // SQL query to create the table for the first time
    // Data types are defined below
    private static final String CREATE_TABLE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_EVENT_ENTRIES
            + "("
            + KEY_ROWID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TITLE
            + " TEXT, "
            + KEY_DATE
            + " DATETIME NOT NULL, "
            + KEY_START
            + " DATETIME NOT NULL, "
            + KEY_END
            + " DATETIME NOT NULL, "
            + KEY_LOCATION
            + " TEXT, "
            + KEY_DESCRIPTION
            + " TEXT, "
            + KEY_URL
            + " TEXT, "
            + KEY_LATITUDE
            + " DOUBLE, "
            + KEY_LONGITUDE
            + " DOUBLE "
            + ");";

    private static final String[] mColumnList = new String[]{KEY_ROWID,
            KEY_TITLE, KEY_DATE, KEY_START, KEY_END,
            KEY_LOCATION, KEY_DESCRIPTION, KEY_URL, KEY_LATITUDE, KEY_LONGITUDE};

    public CampusEventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }

    // Insert a item given each column value
    public long insertEntry(CampusEvent event) {

        ContentValues value = new ContentValues();

        value.put(KEY_TITLE, event.getmTitle());
        value.put(KEY_DATE, event.getmDateTimeInMillis());
        //THIS NEEDS TO BE CHANGED
        value.put(KEY_START, event.getmDateTimeInMillis());
        value.put(KEY_END, event.getmDateTimeInMillis());

        value.put(KEY_LOCATION, event.getmLocation());
        value.put(KEY_DESCRIPTION, event.getmDescription());
        value.put(KEY_URL, event.getmUrl());
        value.put(KEY_LATITUDE, event.getmLatitude());
        value.put(KEY_LONGITUDE, event.getmLongitude());
        SQLiteDatabase dbObj = getWritableDatabase();
        long id = dbObj.insert(TABLE_EVENT_ENTRIES, null, value);
        dbObj.close();
        return id;
    }

    // Remove a entry by giving its index
    public void removeEvent(long rowIndex) {
        SQLiteDatabase dbObj = getWritableDatabase();
        dbObj.delete(TABLE_EVENT_ENTRIES, KEY_ROWID + "=" + rowIndex, null);
        dbObj.close();
    }

    // Query a specific entry by its index. Return a cursor having each column
    // value
    public CampusEvent fetchEventByIndex(long rowId) throws SQLException {
        SQLiteDatabase dbObj = getReadableDatabase();
        CampusEvent event = null;

        Cursor cursor = dbObj.query(true, TABLE_EVENT_ENTRIES, mColumnList,
                KEY_ROWID + "=" + rowId, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            event = cursorToEvent(cursor, true);
        }

        cursor.close();
        dbObj.close();

        return event;
    }

    // Query the entire table, return all rows
    public ArrayList<CampusEvent> fetchEvents() {
        SQLiteDatabase dbObj = getReadableDatabase();
        ArrayList<CampusEvent> entryList = new ArrayList<CampusEvent>();

        Cursor cursor = dbObj.query(TABLE_EVENT_ENTRIES, mColumnList, null,
                null, null, null, null);

        while (cursor.moveToNext()) {
            CampusEvent event = cursorToEvent(cursor, false);
            entryList.add(event);
        }

        cursor.close();
        dbObj.close();

        return entryList;
    }

    private CampusEvent cursorToEvent(Cursor cursor, boolean needGps) {
        CampusEvent event = new CampusEvent();
        event.setmId(cursor.getLong(cursor.getColumnIndex(KEY_ROWID)));
        event.setmDateTime(cursor.getLong(cursor.getColumnIndex(KEY_DATE)));
        event.setmTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
        Long start = cursor.getLong(cursor.getColumnIndex(KEY_START));
        String startString = start.toString();
        //event.setmStart(startString);
        //event.setmEnd(cursor.getLong(cursor.getColumnIndex(KEY_END)));
        event.setmLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
        event.setmDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
        event.setmUrl(cursor.getString(cursor.getColumnIndex(KEY_URL)));
        //Long location = cursor.getColumnIndex(KEY_LOCATION_PIN);
        event.setmLatitude(cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)));
        event.setmLongitude(cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)));
        return event;
    }
}

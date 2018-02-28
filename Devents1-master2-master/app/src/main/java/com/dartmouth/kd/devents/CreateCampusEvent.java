package com.dartmouth.kd.devents;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;


public class CreateCampusEvent extends FragmentActivity {

    private CampusEventDbHelper mEventDbHelper;
    CampusEvent newEvent;
    ListView listview;
    public static final int LIST_ITEM_ID_TITLE = 0;
    public static final int LIST_ITEM_ID_DATE = 1;
    public static final int LIST_ITEM_ID_START = 2;
    public static final int LIST_ITEM_ID_END = 3;
    public static final int LIST_ITEM_ID_LOCATION = 4;
    public static final int LIST_ITEM_ID_DESCRIPTION = 5;
    public static final int LIST_ITEM_ID_URL = 6;
    public static final String TAG = "KF";

    Place myLocation;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_campus_event);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        listview = (ListView)findViewById(R.id.listview);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                int dialogId;
                Intent myIntent;
                // Figuring out what dialog to show based on the position clicked
                // (more readable, also could use dialogId = position + 2)
                switch (position) {
                    case LIST_ITEM_ID_TITLE:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_TITLE;
                        break;
                    case LIST_ITEM_ID_LOCATION:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_LOCATION;
                        break;
                    case LIST_ITEM_ID_DESCRIPTION:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_DESCRIPTION;
                        break;
                    case LIST_ITEM_ID_DATE:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_DATE;
                        break;
                    case LIST_ITEM_ID_START:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_START;
                        break;
                    case LIST_ITEM_ID_END:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_END;
                        break;
                    case LIST_ITEM_ID_URL:
                        dialogId = DialogFragment.DIALOG_ID_MANUAL_INPUT_URL;
                        break;
                    default:
                        dialogId = DialogFragment.DIALOG_ID_ERROR;
                }

                displayDialog(dialogId);
            }
        });

        newEvent = new CampusEvent();
        mEventDbHelper = new CampusEventDbHelper(this);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                myLocation = place;
                LatLng latlng1 = myLocation.getLatLng();
                newEvent.setmLatitude(latlng1.latitude);
                newEvent.setmLongitude(latlng1.longitude);
                Log.i(TAG, "Showing lat " + latlng1.latitude);
                Log.i(TAG, "Showing long " + latlng1.longitude);

            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }


    // "Save" button is clicked
    public void onSaveClicked(View v) {

        new InsertIntoDbTask().execute(newEvent);
        Log.i(TAG, "Showing lat3 " + newEvent.getmLatitude());
        Log.i(TAG, "Showing long3" + newEvent.getmLongitude());
        finish();
    }

    // "Cancel" button is clicked
    public void onCancelClicked(View v) {
        // Discard the input and close the activity directly
        Toast.makeText(getApplicationContext(), "Event discarded.",
                Toast.LENGTH_SHORT).show();
        finish();
    }



    // Display dialog based on id. See DialogFragment for details
    public void displayDialog(int id) {
        android.app.DialogFragment fragment = DialogFragment.newInstance(id);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog_fragment_tag_general));
    }


    public void onTitleSet(String title) {
        newEvent.setmTitle(title);
    }

    public void onLocationSet(String location) {
        newEvent.setmLocation(location);
    }

    public void onLongitudeSet(Double longitude) {
        newEvent.setmLongitude(longitude);
    }
    public void onLatitudeSet(Double latitude) {
        newEvent.setmLatitude(latitude);
    }

    public void onDescriptionSet(String description) {
        newEvent.setmDescription(description);
    }

    public void onUrlSet(String url) {
        newEvent.setmUrl(url);
    }

    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
        newEvent.setmDate(year, monthOfYear, dayOfMonth);
    }

    public void onStartSet(int hourOfDay, int minute) {
        newEvent.setmStart(hourOfDay, minute);
    }

    public void onEndSet(int hourOfDay, int minute) {
        newEvent.setmEnd(hourOfDay, minute);
    }


    public class InsertIntoDbTask extends AsyncTask<CampusEvent, Void, String> {
        @Override
        protected String doInBackground(CampusEvent... exerciseEntries) {
            long id = mEventDbHelper.insertEntry(exerciseEntries[0]);

            return ""+id;
            // Pop up a toast

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Event #" + result + " saved.", Toast.LENGTH_SHORT)
                    .show();
        }

    }
}

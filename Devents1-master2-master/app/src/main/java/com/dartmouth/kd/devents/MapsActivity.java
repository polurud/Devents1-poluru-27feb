package com.dartmouth.kd.devents;

/*
*Based on Google Maps code taught in class by Professor XD
* Implementing Google "Places" with reference from
* https://developers.google.com/places/android-api/start
http://android-er.blogspot.com/2013/02/convert-between-latlng-and-location.html
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Map;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    TextView tvLocInfo;
    private Context mContext;
    boolean markerClicked;
    PolylineOptions rectOptions;
    Polyline polyline;
    static final LatLng HANOVER = new LatLng(43.7022, 72.2896);
    private CampusEventDbHelper mDbHelper;
    private String TAG = "KF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAGG","Made it in maps activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMap();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Log.d("kf", "map ready ");
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        //WHY DOES THIS NOT WORK?????
        //Move the camera instantly to around Hanover with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HANOVER, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        mDbHelper = new CampusEventDbHelper(this);

        List<CampusEvent> allEvents = mDbHelper.fetchEvents();
        for (CampusEvent event : allEvents) {
            long id = event.getmId();
            float fid = id;
            double lat = event.getmLatitude();
            Log.d(TAG, "lat getting set " + lat);
            double longi = event.getmLongitude();
            Log.d(TAG, "long getting set" + longi);
            String mTitle = event.getmTitle();
            Marker mMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, longi))
                    .title(mTitle));
            mMarker.setZIndex(fid);
            mMarker.showInfoWindow();
        }


    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        if(markerClicked) {
            marker.showInfoWindow();
        }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        float fid = marker.getZIndex();
        long id = (long)fid;

        //going to show the Campus Event associated with the marker

        Intent intent = new Intent(); // The intent to launch the activity after
        // click.
        Bundle extras = new Bundle(); // The extra information needed pass
        // through to next activity.
        CampusEvent event = mDbHelper.fetchEventByIndex(id);
        // Write row id into extras.
        extras.putLong(Globals.KEY_ROWID, event.getmId());
        // Passing information for display in the DisaplayEntryActivity.
        extras.putString(Globals.KEY_TITLE,event.getmTitle());
        extras.putString(Globals.KEY_DATE,
                Utils.parseDate(event.getmDateTimeInMillis(), mContext));
        extras.putString(Globals.KEY_START,
                Utils.parseStart(event.getmDateTimeInMillis(), mContext));
        extras.putString(Globals.KEY_END,
                Utils.parseEnd(event.getmDateTimeInMillis(), mContext));
        extras.putString(Globals.KEY_LOCATION,event.getmLocation());
        extras.putString(Globals.KEY_DESCRIPTION,event.getmDescription());
        extras.putString(Globals.KEY_URL,event.getmUrl());
        extras.putDouble(Globals.KEY_LATITUDE, event.getmLatitude());
        extras.putDouble(Globals.KEY_LONGITUDE, event.getmLongitude());


        // Manual mode requires DisplayEntryActivity
        intent.setClass(this, DisplayEventActivity.class);

        // start the activity
        intent.putExtras(extras);
        startActivity(intent);
    }


    private void setUpMap() {
        if (mMap == null) {
            Log.d(TAG, "Map is being setup");
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.dartmouth.kd.devents.R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    public void onLocationChanged(Location location){}
    public void onProviderDisabled(String provider) {}
    public void onProviderEnabled(String provider) {}
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onExitMap(View view) {
        view.setEnabled(false);
        //Stop tracking service
        finish();
    }

}

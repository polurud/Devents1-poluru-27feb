package com.dartmouth.kd.devents;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MapFragment extends Fragment {

    private Intent myIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View ret = inflater.inflate(com.dartmouth.kd.devents.R.layout.fragment_map, container, false);

        Button mapButton = (Button) ret.findViewById(R.id.startMap);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg){
                myIntent = new Intent(getActivity(),  MapsActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        return ret;
    }

}
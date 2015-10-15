package com.example.shiva.ttplaces;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.shiva.ttplaces.pojo.MyPlace;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity{
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<MyPlace> places;
    private ArrayList<String> placeIds;
    private LatLng myLoc;
    private boolean locSet = false;
    String classToPull = null;
    private CameraUpdate myLocation;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        classToPull="Place";
        runAlertDialog();
        places = new ArrayList<>();
        placeIds = new ArrayList<>();
//        loadTestPlaces();
        setUpMapIfNeeded();
    }

    private void loadTestPlaces(){

        showProgressDialog("Getting Data");
        ParseQuery<ParseObject> query = ParseQuery.getQuery(classToPull);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    Log.i("Objects Retrieved", "" + objects);
                    int size = objects.size();
                    for(int count=0; count <size; count++){
                        String id = objects.get(count).getObjectId();
                        placeIds.add(id);
                    }
                    loadIndividualPlace();
                } else {
                    dismissProgressDialog();
                }
            }
        });
    }

    public void loadIndividualPlace(){
        for(int i=0 ; i<placeIds.size(); i++){
            ParseQuery<ParseObject> query2 = ParseQuery.getQuery(classToPull);
            query2.getInBackground(placeIds.get(i), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {

                        double latitude = object.getParseGeoPoint("locationLatLong").getLatitude();
                        double longitude = object.getParseGeoPoint("locationLatLong").getLongitude();
                        String name = object.getString("Name");
                        String area = object.getString("Area");

                        //MyPlace placeToAdd = new MyPlace(name, "meh" , area , new LatLng(latitude, longitude) );
                        //places.add(placeToAdd);
                        dismissProgressDialog();
                        //We would like to show the places once the pulling from the internet is complete.
                        showMarkers(places);
                    }
                    else {
                        dismissProgressDialog();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    //Used to automatically animate the camera on map load
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            if(!locSet){
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 10));
                }
                locSet = true;
                myLocationChangeListener = null;
            }
        }
    };

    private void setDefaultLocationAndZoom(){
        LatLng triniTob = new LatLng(10.450602, -61.244437);
        myLocation = CameraUpdateFactory.newLatLngZoom(triniTob,10);
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
       // mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        //showMarkers(places)
        setDefaultLocationAndZoom();
        mMap.animateCamera(myLocation);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }

    private void showMarkers(ArrayList<MyPlace> markers){
        for(int i=0; i<markers.size(); i++){
            MyPlace place = markers.get(i);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(place.getPosition());
            markerOptions.title(place.getName());
//            TODO: customise marker based on place.type
            mMap.addMarker(markerOptions);
        }
    }

    public void showProgressDialog(String message){
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        progressDialog.dismiss();
    }

    public void runAlertDialog(){
        AlertDialog alert = new AlertDialog.Builder(MapsActivity.this).create();
        alert.setTitle("Do You Want To View Beacons Or Places?");
        alert.setMessage("Select A Button");
        alert.setButton(Dialog.BUTTON_POSITIVE, "View Beacons", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                classToPull="Beacon";
                loadTestPlaces();
            }
        });
        alert.setButton(Dialog.BUTTON_NEGATIVE, "View Places", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                classToPull="Place";
                loadTestPlaces();
            }
        });
        alert.show();
        }

    }

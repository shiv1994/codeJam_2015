package com.example.shiva.ttplaces;

import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.shiva.ttplaces.pojo.MyPlace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        places = new ArrayList<>();
        placeIds = new ArrayList<>();
        loadTestPlaces();
        setUpMapIfNeeded();
    }

    private void loadTestPlaces(){
        showProgressDialog("Getting Data");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Place");
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
                    // objectRetrievalFailed();
                }
            }
        });
    }

    public void loadIndividualPlace(){
        for(int i=0 ; i<placeIds.size(); i++){
            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Place");
            query2.getInBackground(placeIds.get(i), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {

                        double latitude = object.getParseGeoPoint("locationLatLong").getLatitude();
                        double longitude = object.getParseGeoPoint("locationLatLong").getLongitude();
                        String name = object.getString("Name");
                        String area = object.getString("Area");

                        MyPlace placeToAdd = new MyPlace(name, "meh" , area , new LatLng(latitude, longitude) );
                        places.add(placeToAdd);
                        dismisssProgressDialog();
                        showMarkers(places);
                    } else {
                        // something went wrong
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

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        //showMarkers(places);
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

    public void dismisssProgressDialog(){
        progressDialog.dismiss();
    }

}

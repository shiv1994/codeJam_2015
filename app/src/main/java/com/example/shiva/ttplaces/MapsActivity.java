package com.example.shiva.ttplaces;

import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.shiva.ttplaces.pojo.MyPlace;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity{
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ArrayList<MyPlace> places;
    private LatLng myLoc;
    private boolean locSet = false;
//    TODO: write a function to get JSON objects from server and populate places arraylist

    //TODO: remove this function when actual function to populate list is implemented
    private void loadTestPlaces(ArrayList<MyPlace> list){
        MyPlace mp1 = new MyPlace("Some place in Couva","meh" ,"Couva" , new LatLng(10.555112, -61.320147) );
        list.add(mp1);
        MyPlace mp2 = new MyPlace("Nation Museum","meh" ,"POS" , new LatLng(10.662818, -61.510470) );
        list.add(mp2);
        MyPlace mp3 = new MyPlace("Maracus Bay","meh" ,"Maracus" , new LatLng(10.758909, -61.436263) );
        list.add(mp3);
        MyPlace mp4 = new MyPlace("Fort King George","meh" ,"Scarborough" , new LatLng(11.177967, -60.727075) );
        list.add(mp4);
        MyPlace mp5 = new MyPlace("UWI","meh" ,"St Augustine" , new LatLng(10.643435, -61.400062) );
        list.add(mp5);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        places = new ArrayList<>();
        loadTestPlaces(places);
        setUpMapIfNeeded();
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
        showMarkers(places);
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
}

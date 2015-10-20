package com.example.shiva.ttplaces;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.shiva.ttplaces.pojo.MyPlace;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private LatLng myLoc;
    private boolean locSet = false;
    private CameraUpdate myLocation;
    ProgressDialog progressDialog;
    ArrayList<MyPlace> placeObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//      runAlertDialog();
        setUpMapIfNeeded();
        loadPlaces();
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
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 8));
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
//        showMarkers(places);
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
            if(place.getPlace()) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(place.getPosition());
                markerOptions.title(place.getName());
                mMap.addMarker(markerOptions);
            }
            else{
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(place.getPosition());
                markerOptions.title(place.getName());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMap.addMarker(markerOptions);
            }
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

//    public void runAlertDialog(){
//        AlertDialog alert = new AlertDialog.Builder(MapsActivity.this).create();
//        alert.setTitle("Do You Want To View Beacons Or Places?");
//        alert.setMessage("Select A Button");
//        alert.setButton(Dialog.BUTTON_POSITIVE, "View Beacons", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                classToPull = "Beacon";
//                loadTestPlaces();
//            }
//        });
//        alert.setButton(Dialog.BUTTON_NEGATIVE, "View Places", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                classToPull = "Place";
//                loadTestPlaces();
//            }
//        });
//        alert.show();
//    }

    private void loadPlaces() {

        new LoadPlacesData2(){
            protected void onPreExecute(){
                showProgressDialog("Loading Data");
            }
            protected void onPostExecute(ArrayList<MyPlace> myPlaces){
                placeObjects= myPlaces;
                dismissProgressDialog();
                AlertDialog alert = new AlertDialog.Builder(MapsActivity.this).create();
                alert.setTitle("Important Note:");
                alert.setMessage("Places are highlighted in Red and Beacons in Blue" + "\nPress VIEW PLACES To Continue");
                alert.setButton(Dialog.BUTTON_NEGATIVE, "View Places", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showPlaces();
                    }
                });
                alert.show();
            }
        }.execute();

    }

    public void showPlaces(){
        if(!placeObjects.isEmpty())
            showMarkers(placeObjects);
    }

    public void onBackPressed(){
        Intent i = new Intent(this,HomeActivity.class);
        startActivity(i);
        this.finish();
    }

}


class LoadPlacesData2 extends AsyncTask<Void, Void, ArrayList<MyPlace> > {

    protected ArrayList<MyPlace> doInBackground(Void... params){
        List<ParseObject> objects=null;
        ArrayList<MyPlace> places = new ArrayList<MyPlace>();

        ParseObject temp=null;
        ParseQuery<ParseObject> findAllPlaceObjectsQuery = ParseQuery.getQuery("Place");
        try {
            objects = findAllPlaceObjectsQuery.find();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(objects!=null){
            for (ParseObject obj : objects){
                try {
                    temp = findAllPlaceObjectsQuery.get(obj.getObjectId());
                }
                catch(Exception e){
                    e.printStackTrace();
                }

                String name = obj.getString("Name");
                String area = obj.getString("Area");
                int recreationalAns = obj.getInt("Recreation");
                int educationalAns = obj.getInt("Educational");
                int religiousAns = obj.getInt("Religious");
                int remoteAns = obj.getInt("Remote");
                double lat = temp.getParseGeoPoint("locationLatLong").getLatitude();
                double lng = temp.getParseGeoPoint("locationLatLong").getLongitude();
                boolean place = temp.getBoolean("Place");


                places.add(new MyPlace(name,"",area,new LatLng(lat,lng),recreationalAns,educationalAns,religiousAns,remoteAns,place,0));
            }
        }
        return places;

    }
}

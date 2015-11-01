package com.example.shiva.ttplaces;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shiva.ttplaces.pojo.MyPlace;
import com.example.shiva.ttplaces.pojo.NavDrawer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.Nearby;
import com.google.gson.Gson;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//This class is used throughout the application to present the base functionality to the user.
//It firstly begins by checking to see whether the user has entered his/her preferences into the app.
//If so, the suggestions would be used and the suggestions will be calculated and presented to the user.

public class HomeActivity extends NavDrawer implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private ProgressDialog progressDialog;
    private ListView listView;
    static final int REQUEST_RESOLVE_ERROR = 1001;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError=false;
    private static ArrayList<MyPlace> list;
    private static List<MyPlace> storedPlaces;

    private BeaconScannerService scannerService;
    private Context ctx;
    private Location location;
    private SharedPreferences sharedPreferences;
    private static final String sharedPreferenceName="userAnswers";
    private static final String ANSWER1="ansKey1";
    private static final String ANSWER2="ansKey2";
    private static final String ANSWER3="ansKey3";
    private static final String ANSWER4="ansKey4";
    private static final String ANSWER5="ansKey5";
    private static final String sharedPrefExistKey ="sharedPrefExistKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        list = new ArrayList<MyPlace>();
        storedPlaces = new ArrayList<MyPlace>();

        super.onCreate(savedInstanceState);
        ctx=this.getApplicationContext();

        //Setting up the api client to access the nearby api.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


        setContentView(R.layout.activity_home);

        //The following line of code is used to upload use history to the parse database.
        //It will be used for analytics in the future.
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        //Checking to see whether the user has answered his questions.
        checkPrefsSetSuggest();

    }

    public void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected() ) {
            scannerService.stopSubscription();
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    //If the connectio nis established to the API, we can start the service.
    public void onConnected(Bundle connectionHint) {
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Nearby.Messages.getPermissionStatus(mGoogleApiClient).setResultCallback(
                new ErrorCheckingCallback("getPermissionStatus", new Runnable() {
                    @Override
                    public void run() {
                        //subscribe();
                        scannerService = new BeaconScannerService(mGoogleApiClient, ctx);
                        scannerService.onStartCommand();
                        //scannerService.subscribe();
//                        Intent i = new Intent(ctx, scannerService.getClass());
//                        startService(i);
                    }
                })
        );
    }

    //If the connection fails.
    public void onConnectionFailed(ConnectionResult result){
//        Log.i("Error",""+result.getErrorMessage());
    }

    public void onConnectionSuspended(int cause){

    }

    //This adapter is attached to the page and is populated with the suggested places once the algorithm is ran.
    class myAdapter extends BaseAdapter {
        DecimalFormat df=new DecimalFormat("0.00");
        ArrayList<MyPlace> list;
            Context ctx;
            myAdapter(Context context,ArrayList<MyPlace> list) {
                ctx=context;
                this.list=list;
            }

            @Override
            public int getCount() {
                return list.size();
            }
            @Override
            public Object getItem(int position) {
                return list.get(position);
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater= (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
                if(convertView == null)
                    convertView = inflater.inflate(R.layout.view_suggested,parent,false);
                TextView name=(TextView) convertView.findViewById(R.id.tv_name);
                TextView type=(TextView) convertView.findViewById(R.id.tv_type);
                TextView distance=(TextView) convertView.findViewById(R.id.tv_distance);
                TextView area=(TextView) convertView.findViewById(R.id.tv_area);
                MyPlace temp= list.get(position);

                if(temp.getName()==null) name.setText("Place");
                else
                    name.setText("Name: " + temp.getName());

                if(temp.getType()==null)
                    type.setText("Unknown");
                else
                    type.setText("Type:" + temp.getType());

                //If the current location is not found, we display no distance meaning that the location has not been computed.
                if(temp.getDist()==0)
                    distance.setText("Distance: Turn On Location");
                else
                    distance.setText("Distance: "+ df.format(temp.getDist()/1000) + "km");
                if(temp.getArea()==null)
                    area.setText("Area: Area unavailable");
                else
                    area.setText("Area: "+ temp.getArea());

                return convertView;
            }
    }

    public void loadSuggestionPlaces(View view) {
        new LoadPlacesData().execute();
    }


    public void loadSuggestionPlaces() {
        new LoadPlacesData().execute();
    }

    //This class is used to pull the places from the parse database and based on the places that exist in
    //the system, we load them into an array and use the user preferences to determine the best places to visit.
    class LoadPlacesData extends AsyncTask<Void, List<ParseObject>, ArrayList<MyPlace>> {

        //Showing a dialog to indicate that the operations are being performed.
        protected void onPreExecute(){
            showProgressDialog("Determining Suggestions");
            list.clear();
        }

        protected ArrayList<MyPlace> doInBackground(Void... params){
            List<ParseObject> objects=null;
            ArrayList<MyPlace> places = new ArrayList<>();

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
                    String type = obj.getString("Type");
                    int recreationalAns = obj.getInt("Recreation");
                    int educationalAns = obj.getInt("Educational");
                    int religiousAns = obj.getInt("Religious");
                    int remoteAns = obj.getInt("Remote");
                    double lat = temp.getParseGeoPoint("locationLatLong").getLatitude();
                    double lng = temp.getParseGeoPoint("locationLatLong").getLongitude();
                    boolean place = temp.getBoolean("Place");

                    places.add(new MyPlace(name,type,area,new LatLng(lat,lng),recreationalAns,educationalAns,religiousAns,remoteAns,place,0.0));
                }
            }
            return places;
        }

        protected void onPostExecute(ArrayList<MyPlace> placeObjects){
            if(placeObjects.size()!=0) {
                //The algorithm used to determine the best places to be suggested to the user based upon their suggestions.
                placeFinderAlgorithm(placeObjects);
                for (int i = 0; i < 5; i++) {
                    list.add(placeObjects.get(i));
                }
            }
            else
            {
                MyPlace mp = new MyPlace("Suggestions Not Loaded","NIL","NIL", new LatLng(10.0,10.0),false);
                for (int i = 0; i < 5; i++) {
                    //Adding the top 5 places to the list in the adapter.
                    list.add(mp);
                    //Log.i("Inserting Into List",""+placeObjects.get(i).getName());
                }
            }
            listView = (ListView) findViewById(R.id.lv_suggestions);
            myAdapter adapter = new myAdapter(ctx,list);
            listView.setAdapter(adapter);
            //Dismiss the dialog indicating that the operations have been performed.
            dismissProgressDialog();
            Log.i("LOCATION", ">>" + location.toString());
        }
    }

    //This class is used to ask the user to allow the nearby messages API to be accessed on the phone.
    private class ErrorCheckingCallback implements ResultCallback<Status> {
        private final String method;
        private final Runnable runOnSuccess;

        private ErrorCheckingCallback(String method, @Nullable Runnable runOnSuccess) {
            this.method = method;
            this.runOnSuccess = runOnSuccess;
        }

        @Override
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                Log.i("ON RESULT FUNCTION ERR", method + " succeeded.");
                if (runOnSuccess != null) {
                    runOnSuccess.run();
                }
            } else {
                // Currently, the only resolvable error is that the device is not opted
                // in to Nearby. Starting the resolution displays an opt-in dialog.
                if (status.hasResolution()) {
                    if (!mResolvingError) {
                        try {
                            status.startResolutionForResult(HomeActivity.this,
                                    REQUEST_RESOLVE_ERROR);
                            mResolvingError = true;
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("ON RESULT FUNCTION ERR",""+e);
                        }
                    } else {
                        // This will be encountered on initial startup because we do
                        // both publish and subscribe together.  So having a toast while
                        // resolving dialog is in progress is confusing, so just log it.
                        Log.i("c", method + " failed with status: " + status
                                + " while resolving error.");
                    }
                }
                else {
                    Log.i("ON RESULT FUNCTION ERR",""+ " failed with : " + status
                            + " resolving error: " + mResolvingError);
                }
            }
        }
    }

//    public void stopService(View view) {
//        scannerService.stopSubscription();
//        stopService(new Intent(getBaseContext(), HomeActivity.class));
//    }

    //If preferences have ben set we can load suggestion else we load filler items into the adapter.
    public void checkPrefsSetSuggest(){
        Log.i("CHECK SUGG","CHECKSUGG");
        loadSharedPrefsPlaces();
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        boolean preferencesSet = sharedPreferences.getBoolean(sharedPrefExistKey, false);
        if(preferencesSet){
            if(storedPlaces.size()==0){
                loadSuggestionPlaces();
            }
            else{
                for(MyPlace temp: storedPlaces){
                    Log.i("EHHHH",">>>"+temp.getName()+temp.getArea()+temp.getDist()+temp.getDiff()+temp.getEducationalVal());
                    list.add(temp);
                }
                listView = (ListView) findViewById(R.id.lv_suggestions);
                myAdapter adapter = new myAdapter(ctx,list);
                listView.setAdapter(adapter);
            }
        }
        else{
            MyPlace mp = new MyPlace("Preferences Not Answered","NIL","NIL", new LatLng(10.0,10.0),false);
            for (int i = 0; i < 5; i++) {
                list.add(mp);
            }
        }
    }

    //This is the algorithm which will be used to compute the best suggestions depending on the user's preferences.
    //Essentially the user selects choices and they are assigned numbers.
    //Each place in the parse system also has number assigned to them based upon the aspects of being remote, educational, recreational and religious.
    public void placeFinderAlgorithm(List<MyPlace> placeObjects){
        int recreationAns,educationalAns,religiousAns,remoteAns;
        int getAns;
        Location temp = new Location("Local");
        DecimalFormat df = new DecimalFormat("0.00");
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);

        getAns = sharedPreferences.getInt(ANSWER3, -1);
        recreationAns= getAns;

        getAns = sharedPreferences.getInt(ANSWER2, -1);
        educationalAns = getAns;

        getAns = sharedPreferences.getInt(ANSWER4, -1);
        religiousAns = getAns;

        getAns= sharedPreferences.getInt(ANSWER5, -1);
        remoteAns = getAns;

        for(MyPlace place : placeObjects){
            place.setDiff(Math.abs((place.getRemoteVal() - remoteAns) + (place.getEducationalVal() - educationalAns) + (place.getRecreationVal() - recreationAns) + (place.getReligiousVal() - religiousAns)));
            temp.setLongitude(place.getPosition().longitude);
            temp.setLatitude(place.getPosition().latitude);
            if(location==null){
                place.setDist(0.00);
            }
            else {
                place.setDist(temp.distanceTo(location));
            }
        }

        Collections.sort(placeObjects, new Comparator<MyPlace>() {
            @Override
            public int compare(MyPlace p1, MyPlace p2) {
                return (p1.getDiff() < p2.getDiff()) ? -1 : (p1.getDiff() > p2.getDiff()) ? 1 : 0;
            }
        });
    }

    public void runMaps(View view){ // runs the map activity when the view map button is selected on the home page
        Intent i = new Intent(this,MapsActivity.class);
        startActivity(i);
        this.finish();
    }

    public void showProgressDialog(String message){
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        progressDialog.dismiss();
    }
    @Override
    public void onBackPressed(){
        this.finish();
    }

    public void onPause(){
        super.onPause();
        Log.i("PAUSE", "PAUSED");
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonPlaces = gson.toJson(list);
        editor.remove("PLACE_SUGGESTIONS");
        editor.putString("PLACE_SUGGESTIONS",jsonPlaces);
        editor.commit();
    }

    public void loadSharedPrefsPlaces(){
        Log.i("RESUMED", "RESUMED");
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        if(sharedPreferences.contains("PLACE_SUGGESTIONS")){
            String placesSugg = sharedPreferences.getString("PLACE_SUGGESTIONS", null);
            Gson gson = new Gson();
            MyPlace[] favoriteItems = gson.fromJson(placesSugg,
                    MyPlace[].class);

            storedPlaces = Arrays.asList(favoriteItems);
            storedPlaces = new ArrayList<MyPlace>(storedPlaces);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("PLACE_SUGGESTIONS");
            editor.commit();
        }
    }

}

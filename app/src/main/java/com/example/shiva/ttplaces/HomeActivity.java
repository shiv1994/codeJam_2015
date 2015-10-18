package com.example.shiva.ttplaces;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.Nearby;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends NavDrawer implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    public ListView listView;
    static final int REQUEST_RESOLVE_ERROR = 1001;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError=false;
    public ArrayList<MyPlace> list = new ArrayList<>();
    public static ArrayList<MyPlace> plist= new ArrayList<>();
    ProgressDialog progressDialog;
    private BeaconScannerService scannerService;
    private Context ctx;
    public ArrayList<MyPlace> placeObjects;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ctx=this.getApplicationContext();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        listView = (ListView) findViewById(R.id.lv_suggestions);
        checkPrefsSetSuggest();
        myAdapter adapter = new myAdapter(this,HomeActivity.plist);
        listView.setAdapter(adapter);

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

    public void onConnected(Bundle connectionHint) {
        Nearby.Messages.getPermissionStatus(mGoogleApiClient).setResultCallback(
                new ErrorCheckingCallback("getPermissionStatus", new Runnable() {
                    @Override
                    public void run() {
                        //subscribe();
                        scannerService = new BeaconScannerService(mGoogleApiClient, ctx);
                        //scannerService.subscribe();
                        scannerService.onStartCommand();
                    }
                })
        );
    }

    public void onConnectionFailed(ConnectionResult result){

    }

    public void onConnectionSuspended(int cause){

    }

    public void runTours(View view) {
        Intent i = new Intent(this, TourActivity.class);
        startActivity(i);
    }

    class myAdapter extends BaseAdapter {
        ArrayList<MyPlace> list= new ArrayList<>();
        Context ctx;
        myAdapter(Context context,ArrayList<MyPlace> list) {
            ctx=context;
            this.list.addAll(list);
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
            else   name.setText("Name: " + temp.getName());

            if(temp.getType()==null) type.setText("Unknown");
            else   type.setText("Type:" + temp.getType());

//           TODO: calculate distance from current location using latlng in MyPlace object
              distance.setText("Distance: "+ (position+1) + "km");

            if(temp.getArea()==null) area.setText("Area");
            else   area.setText(temp.getArea());

            return convertView;
        }
    }

    private class ErrorCheckingCallback implements ResultCallback<Status> {
        private final String method;
        private final Runnable runOnSuccess;

//        private ErrorCheckingCallback(String method) {
//            this(method, null);
//        }

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

    public void stopService(View view) {
        scannerService.stopSubscription();
        stopService(new Intent(getBaseContext(), HomeActivity.class));

    }

    public void checkPrefsSetSuggest(){
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        boolean preferencesSet = sharedPreferences.getBoolean(sharedPrefExistKey, false);
        if(preferencesSet){
            loadSuggestionPlaces();
        }
        else{
            loadSuggestionPlaces();
        }
    }

    public void loadSuggestionPlaces() {

        new LoadPlacesData(){

            protected void onPreExecute(){
                showProgressDialog("Loading Data");
            }

            protected void onPostExecute(ArrayList<MyPlace> myPlaces){
                placeObjects= myPlaces;

                placeFinderAlgorithm(placeObjects);

                if(!placeObjects.isEmpty()) {
                    for (int i = 0; i < 5; i++) {
                        list.add(placeObjects.get(i));
                    }
                    plist.clear();
                    plist.addAll(list);
                }
                dismissProgressDialog();
            }
        }.execute();
    }

    public void placeFinderAlgorithm(List<MyPlace> placeObjects){
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        int recreationAns,educationalAns,religiousAns,remoteAns;
        int getAns;

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
        }

        Collections.sort(placeObjects, new Comparator<MyPlace>() {
            @Override
            public int compare(MyPlace p1, MyPlace p2) {
                return (p1.getDiff() < p2.getDiff()) ? -1 : (p1.getDiff() > p2.getDiff()) ? 1 : 0;
            }
        });
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
    public void onBackPressed(){
        this.finish();
    }
}

class LoadPlacesData extends AsyncTask<Void, Void, ArrayList<MyPlace> > {

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

                places.add(new MyPlace(name,"",area,new LatLng(lat,lng),recreationalAns,educationalAns,religiousAns,remoteAns));
            }
        }
        return places;
    }
}

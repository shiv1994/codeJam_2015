package com.example.shiva.ttplaces;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends NavDrawer implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private ListView listView;
    static final int REQUEST_RESOLVE_ERROR = 1001;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError=false;
    private ArrayList<MyPlace> suggestedPlaces;
    private ArrayList<MyPlace> places;
    private ArrayList<String> placeIds;
    private BeaconScannerService scannerService;
    private Context ctx;

    private SharedPreferences sharedPreferences;
    private static final String sharedPreferenceName="userAnswers";
    private static final String ANSWER1="ansKey1";
    private static final String ANSWER2="ansKey2";
    private static final String ANSWER3="ansKey3";
    private static final String ANSWER4="ansKey4";
    private static final String ANSWER5="ansKey5";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        placeIds = new ArrayList<String>();

        super.onCreate(savedInstanceState);
        ctx=this.getApplicationContext();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        setContentView(R.layout.activity_home);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        listView = (ListView) findViewById(R.id.lv_suggestions);
//        TODO: this list need to be populated maybe by some service
        ArrayList<MyPlace> list = new ArrayList<>();

        loadTestPlaces(list);
        myAdapter adapter = new myAdapter(this,list);
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


//    TODO: remove this function after testing
    private void loadTestPlaces(ArrayList<MyPlace> list){
        String name = "Place ",area = "Area ",type = "Type ";
        for (int i = 0; i < 10 ; i++){
            MyPlace mp = new MyPlace(name+i, type+i, area+i ,null);
            list.add(mp);
        }
    }

//    public void beaconActivity(View view){
//        Intent i = new Intent(this, InteractiveBeaconActivity.class);
//        startActivity(i);
//    }

    public void runTours(View view) {
        Intent i = new Intent(this, TourActivity.class);
        startActivity(i);
    }

    class myAdapter extends BaseAdapter {
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

        private ErrorCheckingCallback(String method) {
            this(method, null);
        }

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

    public void checkPrefsSet(){
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        boolean preferencesSet = sharedPreferences.getBoolean(ANSWER1,false);
        if(preferencesSet){
            loadPlaceObjectsID();
        }
        else{

        }
    }

    public void loadPlaceObjectsID(){
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
                    loadAllPlaces(size);
                } else {
                    //Let user know places werent loaded.
                }
            }
        });
    }

    public void loadAllPlaces(int size){
        for(int i=0 ; i<size; i++){
            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Place");
            query2.getInBackground(placeIds.get(i), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {

                        double latitude = object.getParseGeoPoint("locationLatLong").getLatitude();
                        double longitude = object.getParseGeoPoint("locationLatLong").getLongitude();
                        String name = object.getString("Name");
                        String area = object.getString("Area");
                        int recAns = object.getInt("Recreation");

                        MyPlace placeToAdd = new MyPlace(name, "meh" , area , new LatLng(latitude, longitude) );
                        places.add(placeToAdd);
                        placeFinderAlgorithm();
                    }

                    else {
                        //Let user know that places werent loaded.
                    }
                }
            });
        }
    }

    public void placeFinderAlgorithm(){
        sharedPreferences = getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        int recAns;
        String getAns;
        getAns = sharedPreferences.getString(ANSWER5,"N/A");
        if(getAns.equals("N/A")){

        }
        else{
            recAns= Integer.parseInt(getAns);
        }


        suggestedPlaces=new ArrayList<MyPlace>();
        for(MyPlace place : suggestedPlaces){

        }
        
    }
}

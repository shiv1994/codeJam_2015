package com.example.shiva.ttplaces;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import com.google.android.gms.nearby.Nearby;
import com.parse.ParseAnalytics;
import com.parse.ParseUser;
import java.util.ArrayList;

public class HomeActivity extends NavDrawer implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    ListView listView;
    static final int REQUEST_RESOLVE_ERROR = 1001;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError=false;
    BeaconScannerService scannerService;
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

     public void logOut(View view) {
        ParseUser.logOut();
        Intent i = new Intent(this, LoginRegisterActivity.class);
        startActivity(i);
        this.finish();
    }

    public void beaconActivity(View view){
        Intent i = new Intent(this, InteractiveBeaconActivity.class);
        startActivity(i);
    }

    public void mapsActivity(View view) {
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    public void runSuggestions(View view) {
        Intent i = new Intent(this, SuggestionActivity.class);
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

}

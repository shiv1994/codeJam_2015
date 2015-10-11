package com.example.shiva.ttplaces;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

public class InteractiveBeacon extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    static final int REQUEST_RESOLVE_ERROR = 1001;
    private GoogleApiClient mGoogleApiClient;
    private  MessageListener mMessageListener;
    private boolean mResolvingError=false;
    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(3 * 60).build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_beacon);



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        mMessageListener = new MessageListener() {
            // Called each time a new message is discovered nearby.
            @Override
            public void onFound(Message message) {
                Log.i("ON CREATE FUNCTION",""+message);
            }

            // Called when a message is no longer nearby.
            @Override
            public void onLost(Message message) {

                Log.i("ON CREATE FUNCTION", "Lost message: " + message);
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_interactive_beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Permission granted or error resolved successfully then we proceed
                // with publish and subscribe..
                subscribe();
            } else {
                // This may mean that user had rejected to grant nearby permission.
                //showToast("Failed to resolve error with code " + resultCode);
            }
        }
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
            // Using Nearby is battery intensive. To preserve battery, stop subscribing or
            // publishing when the fragment is inactive.
            unsubscribe();
//            unpublish();

            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    public void onConnectionFailed(ConnectionResult result){

    }

    public void onConnected(Bundle connectionHint) {
        Nearby.Messages.getPermissionStatus(mGoogleApiClient).setResultCallback(
                new ErrorCheckingCallback("getPermissionStatus", new Runnable() {
                    @Override
                    public void run() {
                        subscribe();
                    }
                })
        );
    }

    public void onConnectionSuspended(int cause){

    }

    private void subscribe() {
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            Log.i("ON SUBSCRIBE FUNCTION", "WE NEVER CONNECTED");
            if (!mGoogleApiClient.isConnecting()) {
                Log.i("OI", "WE ARE CONNECTING");
                mGoogleApiClient.connect();
            }
        } else {
            Log.i("ON SUBSCRIBE FUNCTION", "trying to subscribe");
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(new SubscribeCallback() {
                        @Override
                        public void onExpired() {
                            super.onExpired();
//                            Log.i(TAG, "no longer subscribing");
//                            updateSharedPreference(Constants.KEY_SUBSCRIPTION_TASK,
//                                    Constants.TASK_NONE);
                        }
                    }).build();

            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {

                            if (status.isSuccess()) {
                                Log.i("ON SUBSCRIBE FUNCTION", "subscribed successfully");
                            } else {
                                Log.i("ON SUBSCRIBE FUNCTION", "could not subscribe:"+status.getStatusMessage());
                            }
                        }
                    });
        }
    }

    private void unsubscribe() {
        Log.i("ON UNSUBSCRIBE FUNCTION", "trying to unsubscribe");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i("ON UNSUBSCRIBE FUNCTION", "unsubscribed successfully");

                            } else {
                                Log.i("ON UNSUBSCRIBE FUNCTION", "could not unsubscribe");
                            }
                        }
                    });
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
                Log.i("OI", method + " succeeded.");
                if (runOnSuccess != null) {
                    runOnSuccess.run();
                }
            } else {
                // Currently, the only resolvable error is that the device is not opted
                // in to Nearby. Starting the resolution displays an opt-in dialog.
                if (status.hasResolution()) {
                    if (!mResolvingError) {
                        try {
                            status.startResolutionForResult(InteractiveBeacon.this,
                                    REQUEST_RESOLVE_ERROR);
                            mResolvingError = true;
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("ON RESULT FUNCTION ERR",""+e);
                        }
                    } else {
                        // This will be encountered on initial startup because we do
                        // both publish and subscribe together.  So having a toast while
                        // resolving dialog is in progress is confusing, so just log it.
                        Log.i("ON RESULT FUNCTION ERR", method + " failed with status: " + status
                                + " while resolving error.");
                    }
                } else {
                    Log.i("ON RESULT FUNCTION ERR",""+ " failed with : " + status
                            + " resolving error: " + mResolvingError);
                }
                }
            }
        }
}

package com.example.shiva.ttplaces;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

/**
 * Created by Shiva on 10/12/2015.
 */
public class BeaconScannerService extends Service {

    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;
    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(3 * 60).build();
    public Context ctx;

    public BeaconScannerService(){

    }

    public BeaconScannerService(GoogleApiClient mGoogleApiClient, Context ctx) {
        this.mGoogleApiClient=mGoogleApiClient;
        this.ctx=ctx;
        mMessageListener = new MessageListener() {
            // Called each time a new message is discovered nearby.
            @Override
            public void onFound(Message message) {
                Log.i("ON CREATE FUNCTION", "" + message);
                notificationToUser(message);
            }

            // Called when a message is no longer nearby.
            @Override
            public void onLost(Message message) {
                Log.i("ON CREATE FUNCTION", "Lost message: " + message);
            }
        };
    }

    public void onStartCommand(){
        subscribe();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Perform your long running operations here.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        subscribe();

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void notificationToUser(Message message)
    {
        Intent resultIntent = new Intent(ctx, InteractiveBeaconActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx);
        mBuilder.setSmallIcon(R.drawable.common_signin_btn_icon_dark)
                .setContentTitle("You are at "+message.getContent().toString())
                .setContentText("Click to start the interactive tour.")
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(ctx, 0, resultIntent, 0));

        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    protected void subscribe() {
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            Log.i("ON SUBSCRIBE FUNCTION", "WE NEVER CONNECTED");
            if (!mGoogleApiClient.isConnecting()) {
                Log.i("OI", "WE ARE CONNECTING");
                mGoogleApiClient.connect();
            }
        }
        else {
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
                                Log.i("ON SUBSCRIBE FUNCTION", "could not subscribe:" + status.getStatusMessage());
                            }
                        }
                    });
        }
    }

    protected void stopSubscription() {
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        else {
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
}

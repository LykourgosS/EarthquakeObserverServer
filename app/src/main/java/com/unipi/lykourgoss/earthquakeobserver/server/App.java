package com.unipi.lykourgoss.earthquakeobserver.server;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

/**
 * Created by LykourgosS <lpsarantidis@gmail.com>
 * on 03,September,2019.
 */

public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        // every time the app is launched onCreate is triggered, but once a channel is already
        // created trying to created again does nothing...
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // notification channels are available for API v.26 and higher
            //will be used for displaying ServerService foreground service's notification
            NotificationChannel serverServiceChannel = new NotificationChannel(
                    Constant.OBSERVER_SERVICE_CHANNEL_ID,
                    "Observer Service Channel (name)",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            // todo set description for each channel!!!
            // if make any changes (i.e. notification's behavior) here uninstall and re-install the app
            serverServiceChannel.setDescription("This is channel's description");

            // creating notification channels
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serverServiceChannel);
        }
    }
}

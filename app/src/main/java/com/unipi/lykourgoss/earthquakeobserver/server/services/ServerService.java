package com.unipi.lykourgoss.earthquakeobserver.server.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.unipi.lykourgoss.earthquakeobserver.server.Constant;
import com.unipi.lykourgoss.earthquakeobserver.server.tools.DatabaseHandler;
import com.unipi.lykourgoss.earthquakeobserver.server.R;
import com.unipi.lykourgoss.earthquakeobserver.server.activities.MainActivity;

/**
 * Created by LykourgosS <lpsarantidis@gmail.com>
 * on 03,September,2019.
 */

public class ServerService extends Service implements DatabaseHandler.OnEarthquakeListener {

    private static final String TAG = "ServerService";

    private DatabaseHandler databaseHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        Intent intentNotification = new Intent(this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNotification, 0);

        // todo only use foreground service on Oreo an higher -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        if (true) { // if API is v.26 and higher start a foreground service
            Notification notification = new NotificationCompat.Builder(this, Constant.OBSERVER_SERVICE_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_cast_connected_white_24dp)
                    .setContentTitle("Server title")
                    .setContentText("server text...")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .build();

            // when service started with:
            // 1. startService() -> without the following line system will kill the service after 1 min
            // 2. startForegroundService() -> if not called in 5 seconds max system will kill the service (on API v.26)
            startForeground(Constant.SERVER_SERVICE_ID, notification); // id must be greater than 0
        }

        databaseHandler = new DatabaseHandler(this);
        databaseHandler.addListener();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        databaseHandler.removeListener();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onEarthquakeAdded(boolean earthquakeAddedSuccessfully) {
        Log.d(TAG, "onEarthquakeAdded: added = " + earthquakeAddedSuccessfully);
        if (earthquakeAddedSuccessfully) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }
}

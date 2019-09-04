package com.unipi.lykourgoss.earthquakeobserver.server.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.unipi.lykourgoss.earthquakeobserver.server.R;
import com.unipi.lykourgoss.earthquakeobserver.server.models.Earthquake;
import com.unipi.lykourgoss.earthquakeobserver.server.services.ServerService;
import com.unipi.lykourgoss.earthquakeobserver.server.tools.DatabaseHandler;
import com.unipi.lykourgoss.earthquakeobserver.server.tools.Util;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DatabaseHandler.OnEarthquakeListener {

    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHandler = new DatabaseHandler(this);

        findViewById(R.id.button_start_server).setOnClickListener(this);
        findViewById(R.id.button_stop_server).setOnClickListener(this);
        findViewById(R.id.button_add_earthquake).setOnClickListener(this);
        findViewById(R.id.button_send_notification).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUi(Util.isServiceRunning(this, ServerService.class));
    }

    private void updateUi(boolean serverStarted) {
        findViewById(R.id.button_start_server).setEnabled(!serverStarted);
        findViewById(R.id.button_stop_server).setEnabled(serverStarted);
    }

    private void startServer() {
        Util.scheduleStartJob(this);
        updateUi(true);
    }

    private void stopServer() {
        stopService(new Intent(this, ServerService.class));
        updateUi(false);
    }

    private void addEarthquake() {
        databaseHandler.addEarthquake(new Earthquake(0, new Date().getTime()));
        findViewById(R.id.button_add_earthquake).setEnabled(false);
    }

    private void sendNotification() {
        // The topic name can be optionally prefixed with "/topics/".
        String topic = "highScores";

        // See documentation on defining a message payload.
        RemoteMessage message = new RemoteMessage.Builder("").build();

        // Send a message to the devices subscribed to the provided topic.
        FirebaseMessaging.getInstance().send(message);
        // Response is a message ID string.
//        System.out.println("Successfully sent message: " + response);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_server:
                startServer();
                break;
            case R.id.button_stop_server:
                stopServer();
                break;
            case R.id.button_add_earthquake:
                addEarthquake();
                break;
            case R.id.button_send_notification:
                sendNotification();
                break;
        }
    }

    @Override
    public void onEarthquakeAdded(boolean earthquakeAddedSuccessfully) {
        findViewById(R.id.button_add_earthquake).setEnabled(true);
        if (earthquakeAddedSuccessfully) {
            Toast.makeText(this, "Earthquake added successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error while adding earthquake", Toast.LENGTH_SHORT).show();
        }
    }
}

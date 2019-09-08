package com.unipi.lykourgoss.earthquakeobserver.server.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.unipi.lykourgoss.earthquakeobserver.server.R;
import com.unipi.lykourgoss.earthquakeobserver.server.models.Earthquake;
import com.unipi.lykourgoss.earthquakeobserver.server.services.ServerService;
import com.unipi.lykourgoss.earthquakeobserver.server.tools.DatabaseHandler;
import com.unipi.lykourgoss.earthquakeobserver.server.tools.Util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        findViewById(R.id.button_client_settings).setOnClickListener(this);
        findViewById(R.id.button_server_settings).setOnClickListener(this);
    }

    private void startServer() {
//        Util.scheduleStartJob(this);
        Util.restartServer(this);
    }

    private void stopServer() {
        stopService(new Intent(this, ServerService.class));
    }

    private void addEarthquake() {
        Map<String, Boolean> devices = new HashMap<>();
        devices.put("A", true);
        devices.put("nn", true);
        devices.put("a", true);
        devices.put("m", true);
        devices.put("o", true);
        devices.put("u", true);
        databaseHandler.addEarthquake(new Earthquake(devices, false, new Date().getTime()));
        findViewById(R.id.button_add_earthquake).setEnabled(false);
    }

    private void openClientSettings() {
        startActivity(new Intent(this, ClientSettingsActivity.class));
    }

    private void openServerSettings() {
        startActivity(new Intent(this, ServerSettingsActivity.class));
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
            case R.id.button_client_settings:
                openClientSettings();
                break;
            case R.id.button_server_settings:
                openServerSettings();
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

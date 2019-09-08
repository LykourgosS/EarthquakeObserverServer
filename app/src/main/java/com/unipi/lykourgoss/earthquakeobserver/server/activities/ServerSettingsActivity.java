package com.unipi.lykourgoss.earthquakeobserver.server.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.unipi.lykourgoss.earthquakeobserver.server.Constant;
import com.unipi.lykourgoss.earthquakeobserver.server.R;
import com.unipi.lykourgoss.earthquakeobserver.server.models.Settings;
import com.unipi.lykourgoss.earthquakeobserver.server.tools.SharedPrefManager;
import com.unipi.lykourgoss.earthquakeobserver.server.tools.Util;
import com.unipi.lykourgoss.earthquakeobserver.server.tools.dbhandlers.SettingsHandler;

import java.util.Date;

public class ServerSettingsActivity extends BaseActivity implements View.OnClickListener {

    private EditText editTextMinDeviceCount;

    private SettingsHandler settingsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);

        setTitle("Server Settings");

        editTextMinDeviceCount = findViewById(R.id.edit_text_min_device_count);

        findViewById(R.id.button_save_settings).setOnClickListener(this);

        updateUi();
    }

    private void saveSettings() {
        // Server settings
        int minDeviceCount;

        try {
            minDeviceCount = Integer.valueOf(editTextMinDeviceCount.getText().toString());
        } catch (NumberFormatException exception) {
            Toast.makeText(this, "All fields must have permissible value", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!(minDeviceCount >= 1 && minDeviceCount <= 100)) {
            // Min device count: Min=1 device and Max=100 devices
            editTextMinDeviceCount.setError("Min device count: Min=1 device and Max=100 devices");
            editTextMinDeviceCount.requestFocus();
            return;
        }

        findViewById(R.id.button_save_settings).setEnabled(false);

        // Save server settings locally in Shared Preferences
        SharedPrefManager.getInstance(this).write(Constant.MIN_DEVICE_COUNT, minDeviceCount);

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        Util.restartServer(this);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_save_settings) {
            saveSettings();
        }
    }

    private void updateUi() {
        // Server Settings
        int minDeviceCount = SharedPrefManager.getInstance(this).read(Constant.MIN_DEVICE_COUNT, 1);
        editTextMinDeviceCount.setText(String.valueOf(minDeviceCount));
    }
}

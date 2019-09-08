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

public class ClientSettingsActivity extends BaseActivity implements View.OnClickListener, SettingsHandler.OnSettingsListener {

    private EditText editTextSamplingPeriod;
    private EditText editTextSamplesBatchCount;
    private EditText editTextMinEventDuration;
    private EditText editTextDefaultBalanceSensorValue;
    private EditText editTextSensorValueThreshold;
    private EditText editTextConfigDeviceRejectThreshold;

    private SettingsHandler settingsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_settings);

        setTitle("Settings");

        editTextSamplingPeriod = findViewById(R.id.edit_text_sampling_period);
        editTextSamplesBatchCount = findViewById(R.id.edit_text_samples_batch_count);
        editTextMinEventDuration = findViewById(R.id.edit_text_min_event_duration);
        editTextDefaultBalanceSensorValue = findViewById(R.id.edit_text_default_balance_sensor_value);
        editTextSensorValueThreshold = findViewById(R.id.edit_text_sensor_value_threshold);
        editTextConfigDeviceRejectThreshold = findViewById(R.id.edit_text_config_device_reject_threshold);

        findViewById(R.id.button_save_settings).setOnClickListener(this);

        settingsHandler = new SettingsHandler(this);
        settingsHandler.fetchSettings();
        showProgressDialog();
    }

    private void saveSettings() {
        // Client settings
        int samplingPeriod;
        int samplesBatchCount;
        int minEventDuration;
        float defaultBalanceSensorValue;
        float sensorValueThreshold;
        float configDeviceRejectThreshold;

        try {
            samplingPeriod = Integer.valueOf(editTextSamplingPeriod.getText().toString());
            samplesBatchCount = Integer.valueOf(editTextSamplesBatchCount.getText().toString());
            minEventDuration = Integer.valueOf(editTextMinEventDuration.getText().toString());
            defaultBalanceSensorValue = Float.valueOf(editTextDefaultBalanceSensorValue.getText().toString());
            sensorValueThreshold = Float.valueOf(editTextSensorValueThreshold.getText().toString());
            configDeviceRejectThreshold = Float.valueOf(editTextConfigDeviceRejectThreshold.getText().toString());
        } catch (NumberFormatException exception) {
            Toast.makeText(this, "All fields must have permissible value", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!(samplingPeriod >= 20 * 1000 && samplingPeriod <= 1000 * 1000)) {
            // Sampling period: Min=0.02 seconds and Max=1 seconds
            editTextSamplingPeriod.setError("Sampling period: Min=0.02 seconds and Max=1 seconds");
            editTextSamplingPeriod.requestFocus();
            return;
        } else if (!(samplesBatchCount <= 100)) {
            // Samples batch count: Max=100 samples
            editTextSamplesBatchCount.setError("Samples batch count: Max=100 samples");
            editTextSamplesBatchCount.requestFocus();
            return;
        } else if (!(minEventDuration >= 2 * 1000 && minEventDuration <= 10 * 1000)) {
            // Min event duration: Min=2 seconds and Max=10 seconds
            editTextMinEventDuration.setError("Min event duration: Min=2 seconds and Max=10 seconds");
            editTextMinEventDuration.requestFocus();
            return;
        } else if (!(defaultBalanceSensorValue >= 9.4f && defaultBalanceSensorValue <= 10f)) {
            // Default balance sensor value: Min=9.4 m/s² and Max=10 m/s²
            editTextDefaultBalanceSensorValue.setError("Default balance sensor value: Min=9.4 m/s² and Max=10 m/s²");
            editTextDefaultBalanceSensorValue.requestFocus();
            return;
        } else if (!(sensorValueThreshold >= 0.05f && sensorValueThreshold <= 1.5f)) {
            // Sensor value threshold: Min=0.05 m/s² and Max=1.5 m/s²
            editTextSensorValueThreshold.setError("Sensor value threshold: Min=0.05 m/s² and Max=1.5 m/s²");
            editTextSensorValueThreshold.requestFocus();
            return;
        } else if (!(configDeviceRejectThreshold >= 0.3f && configDeviceRejectThreshold <= 1.0f)) {
            // Config device reject threshold: Min=0.3 m/s² and Max=1.0 m/s²
            editTextConfigDeviceRejectThreshold.setError("Config device reject threshold: Min=0.3 m/s² and Max=1.0 m/s²");
            editTextConfigDeviceRejectThreshold.requestFocus();
            return;
        }

        showProgressDialog();
        findViewById(R.id.button_save_settings).setEnabled(false);

        // Save client settings in Firebase
        Settings settings = new Settings(samplingPeriod, samplesBatchCount, minEventDuration, defaultBalanceSensorValue, sensorValueThreshold, configDeviceRejectThreshold, new Date().getTime());
        settingsHandler.updateSettings(settings);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_save_settings) {
            saveSettings();
        }
    }

    private void updateUi(Settings settings) {
        // Client Settings
        editTextSamplingPeriod.setText(String.valueOf(settings.getSamplingPeriod()));
        editTextSamplesBatchCount.setText(String.valueOf(settings.getSamplesBatchCount()));
        editTextMinEventDuration.setText(String.valueOf(settings.getMinEventDuration()));
        editTextDefaultBalanceSensorValue.setText(String.valueOf(settings.getDefaultBalanceSensorValue()));
        editTextSensorValueThreshold.setText(String.valueOf(settings.getSensorValueThreshold()));
        editTextConfigDeviceRejectThreshold.setText(String.valueOf(settings.getConfigDeviceRejectSampleThreshold()));
    }

    @Override
    public void onSettingsFetched(Settings settings) {
        hideProgressDialog();
        if (settings != null) {
            updateUi(settings);
        }
    }

    @Override
    public void onSettingsUpdated(boolean successfullyUpdated) {
        hideProgressDialog();
        if (successfullyUpdated) {
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            Util.restartServer(this);
            finish();
        } else {
            Toast.makeText(this, "Failed to save settings", Toast.LENGTH_SHORT).show();
            findViewById(R.id.button_save_settings).setEnabled(true);
        }
    }
}

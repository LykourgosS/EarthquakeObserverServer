package com.unipi.lykourgoss.earthquakeobserver.server.models;

/**
 * Created by LykourgosS <lpsarantidis@gmail.com>
 * on 06,September,2019.
 */

public class ClientSettings {

    /**
     * Used for registering listener in {@link com.unipi.lykourgoss.earthquakeobserver.client.services.EarthquakeManager},
     * using a Sensor manager object, for receiving SensorEvents objects. The same sampling period
     * is used in {@link com.unipi.lykourgoss.earthquakeobserver.client.activities.ConfigDeviceActivity}
     * (in microseconds, default value 10 samples/s => 1 sample in 0.1 s = 100 ms = 100000 μs)
     *
     * @see com.unipi.lykourgoss.earthquakeobserver.client.services.EarthquakeManager
     * */
    private int samplingPeriod;

    /**
     * How many samples (normalised values from SensorEvent values array {@link MinimalEarthquakeEvent})
     * should be used for generating, if needed an {@link EarthquakeEvent}, and save it to Firebase
     * Database (default value: 10 samples)
     *
     * @see com.unipi.lykourgoss.earthquakeobserver.client.services.EarthquakeManager
     * */
    private int samplesBatchCount;

    /**
     * Minimum duration in milliseconds for an {@link EarthquakeEvent} object to become a major one
     * from minor. Also used to check, while the event is terminated, if event should be saved
     * (means it is major) to Firebase Database (from active-events to saved-events)
     * (default value: 5 * 1000 millis = 5 seconds)
     *
     * @see com.unipi.lykourgoss.earthquakeobserver.client.services.EarthquakeManager
     * */
    private long minEventDuration ;

    /**
     * The default balance sensor value (calculated using the balanceValue from all devices that
     * have been stored to Firebase in path: /devices), it's being used for rejecting values too
     * big or too small during the configuration of a device (default value: 9.8f)
     * */
    private float defaultBalanceSensorValue;

    /**
     * Used for recognize if values from accelerometer are big enough for adding to Firebase,
     * reporting an event, at first as a minor one and then if the the event continues to be active
     * and overcomes it, then to be reported as a major one. (default value: 0.1f)
     *
     * @see com.unipi.lykourgoss.earthquakeobserver.client.services.EarthquakeManager
     * @see MinimalEarthquakeEvent
     * */
    private float sensorValueThreshold;

    /**
     * If the absolute difference of a sample's sensor value and the {@link #defaultBalanceSensorValue}
     * is greater the configuration will be restarted.
     *
     * @see com.unipi.lykourgoss.earthquakeobserver.client.activities.ConfigDeviceActivity
     * */
    private float configDeviceRejectSampleThreshold;

    public ClientSettings() {
    }

    public ClientSettings(int samplingPeriod, int samplesBatchCount, long minEventDuration, float defaultBalanceSensorValue, float sensorValueThreshold, float configDeviceRejectSampleThreshold) {
        this.samplingPeriod = samplingPeriod;
        this.samplesBatchCount = samplesBatchCount;
        this.minEventDuration = minEventDuration;
        this.defaultBalanceSensorValue = defaultBalanceSensorValue;
        this.sensorValueThreshold = sensorValueThreshold;
        this.configDeviceRejectSampleThreshold = configDeviceRejectSampleThreshold;
    }

    public int getSamplingPeriod() {
        return samplingPeriod;
    }

    public int getSamplesBatchCount() {
        return samplesBatchCount;
    }

    public long getMinEventDuration() {
        return minEventDuration;
    }

    public float getDefaultBalanceSensorValue() {
        return defaultBalanceSensorValue;
    }

    public float getSensorValueThreshold() {
        return sensorValueThreshold;
    }

    public float getConfigDeviceRejectSampleThreshold() {
        return configDeviceRejectSampleThreshold;
    }
}

package com.unipi.lykourgoss.earthquakeobserver.server.tools;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.lykourgoss.earthquakeobserver.server.models.Earthquake;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LykourgosS <lpsarantidis@gmail.com>
 * on 03,September,2019.
 */

public class DatabaseHandler implements ChildEventListener {

    private static final String TAG = "DatabaseHandler";

    // todo not only 1
    private static final int MIN_DEVICE_COUNT = 1;

    private static final String MAJOR_ACTIVE_EVENTS_REF = "major-active-events";
    private static final String ACTIVE_EARTHQUAKES_REF = "active-earthquakes";
    private static final String SAVED_EARTHQUAKES_REF = "saved-earthquakes";

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference activeEventsRef;
    private DatabaseReference activeEarthquakesRef;
    private DatabaseReference savedEarthquakesRef;

    private OnEarthquakeListener listener;

    private String lastEarthquakeId;
    private boolean earthquakeIsActive = false;
    private Map<String, Boolean> devices;

    public DatabaseHandler(OnEarthquakeListener listener) {
        activeEventsRef = databaseReference.child(MAJOR_ACTIVE_EVENTS_REF);
        activeEarthquakesRef = databaseReference.child(ACTIVE_EARTHQUAKES_REF);
        savedEarthquakesRef = databaseReference.child(SAVED_EARTHQUAKES_REF);
        this.listener = listener;
        this.devices = new HashMap<>();
    }

    public void addListener() {
        activeEventsRef.addChildEventListener(this);
    }

    public void removeListener() {
        activeEventsRef.removeEventListener(this);
    }

    public void addEarthquake(Earthquake earthquake) {
        lastEarthquakeId = activeEarthquakesRef.push().getKey();
        earthquake.setId(lastEarthquakeId);
        activeEarthquakesRef.child(lastEarthquakeId).setValue(earthquake).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: saved = " + task.isSuccessful());
                listener.onEarthquakeAdded(task.isSuccessful());
            }
        });
    }

    private void updateEarthquake(String deviceId) {
        // path: /earthquakes/{earthquakeId}/devices/{deviceId}. And the value: true
        activeEarthquakesRef.child(lastEarthquakeId).child(Earthquake.DEVICES).child(deviceId).setValue(true);
    }

    private void terminateEarthquake() {
        // save earthquake from active-earthquakes to saved-earthquakes
        activeEarthquakesRef.child(lastEarthquakeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Earthquake earthquake = dataSnapshot.getValue(Earthquake.class);
                // add the earthquake to saved-earthquakes
                savedEarthquakesRef.child(earthquake.getId()).setValue(earthquake);
                // remove the earthquake from active-earthquakes
                activeEarthquakesRef.child(earthquake.getId()).setValue(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists()) {
            // in path: /active-events, we store EarthquakeEvent objects using as keys the deviceId
            // field of each EarthquakeEvent objects (one active event by device!!!)
            String deviceId = dataSnapshot.getKey();
            devices.put(deviceId, true);
            if (devices.size() >= MIN_DEVICE_COUNT) {
                if (!earthquakeIsActive) {
                    addEarthquake(new Earthquake(devices, new Date().getTime()));
                    earthquakeIsActive = true;
                } else {
                    updateEarthquake(deviceId);
                }
            }
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            // in path: /active-events, we store EarthquakeEvent objects using as keys the deviceId
            // field of each EarthquakeEvent objects (one active event by device!!!)
            String deviceId = dataSnapshot.getKey();
            devices.remove(deviceId);
            if (devices.size() < MIN_DEVICE_COUNT) {
                if (earthquakeIsActive) {
                    terminateEarthquake();
                    earthquakeIsActive = false;
                    devices.clear();
                }
            }
        }
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
    }

    public interface OnEarthquakeListener {
        /**
         * Triggered when the {@link #addEarthquake(Earthquake)} is completed.
         *
         * @param earthquakeAddedSuccessfully shows if the earthquake added successfully or not.
         * */
        void onEarthquakeAdded(boolean earthquakeAddedSuccessfully);
    }
}

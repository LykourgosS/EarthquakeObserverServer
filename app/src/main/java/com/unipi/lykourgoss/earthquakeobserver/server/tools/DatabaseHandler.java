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

    /*// todo remove comments
    private static final int MIN_DEVICE_COUNT = 1;*/

    private static final String MAJOR_ACTIVE_EVENTS_REF = "major-active-events";
    private static final String EARTHQUAKES_REF = "earthquakes";
    private static final String SETTINGS_REF = "settings";

    /*private static final String ACTIVE_EARTHQUAKES_REF = "active-earthquakes";
    private static final String SAVED_EARTHQUAKES_REF = "saved-earthquakes";*/

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference activeEventsRef;
    private DatabaseReference earthquakesRef;
    private DatabaseReference settingsRef;
    /*private DatabaseReference activeEarthquakesRef;
    private DatabaseReference savedEarthquakesRef;*/

    private OnEarthquakeListener listener;

    private String lastEarthquakeId;
    private boolean earthquakeIsActive = false;
    private Map<String, Boolean> devices;

    private int minDeviceCount;

    public DatabaseHandler(OnEarthquakeListener listener) {
        activeEventsRef = databaseReference.child(MAJOR_ACTIVE_EVENTS_REF);
        earthquakesRef = databaseReference.child(EARTHQUAKES_REF);
        settingsRef = databaseReference.child(SETTINGS_REF);
        /*activeEarthquakesRef = databaseReference.child(ACTIVE_EARTHQUAKES_REF);
        savedEarthquakesRef = databaseReference.child(SAVED_EARTHQUAKES_REF);*/
        this.listener = listener;
        this.devices = new HashMap<>();
    }

    public void setMinDeviceCount(int minDeviceCount) {
        this.minDeviceCount = minDeviceCount;
    }

    public void addListener() {
        activeEventsRef.addChildEventListener(this);
    }

    public void removeListener() {
        activeEventsRef.removeEventListener(this);
    }

    public void addEarthquake(Earthquake earthquake) {
        String id = earthquakesRef.push().getKey();
        earthquake.setId(id);

        earthquakesRef.child(id).setValue(earthquake).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: saved = " + task.isSuccessful());
                listener.onEarthquakeAdded(task.isSuccessful());
            }
        });

        /*Map<String, Object> earthquakeAddition = new HashMap<>();

        String idPath = ACTIVE_EARTHQUAKES_REF + "/" + id;
        earthquakeAddition.put(idPath, true);

        String earthquakePath = SAVED_EARTHQUAKES_REF + "/" + id;
        earthquakeAddition.put(earthquakePath, earthquake);

        databaseReference.updateChildren(earthquakeAddition).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: saved = " + task.isSuccessful());
                listener.onEarthquakeAdded(task.isSuccessful());
            }
        });*/
        lastEarthquakeId = id;
    }

    private void updateEarthquake(String deviceId) {
        // path: /earthquakes/{earthquakeId}/devices/{deviceId}. And the value: true
        //savedEarthquakesRef.child(lastEarthquakeId).child(Earthquake.DEVICES).child(deviceId).setValue(true);

        earthquakesRef.child(lastEarthquakeId).child(Earthquake.DEVICES).child(deviceId).setValue(true);
    }

    private void terminateEarthquake() {
        // remove earthquake from active-earthquakes (no need for further action, it is already
        // saved in saved-earthquakes when it first added)
        //activeEarthquakesRef.child(lastEarthquakeId).setValue(null);

        // update isActive and endTime fields of the last Earthquake object (path: /earthquakes)
        Map<String, Object> earthquakeTermination = new HashMap<>();

        earthquakeTermination.put(Earthquake.IS_ACTIVE, false);

        earthquakeTermination.put(Earthquake.END_TIME, new Date().getTime());

        earthquakesRef.child(lastEarthquakeId).updateChildren(earthquakeTermination).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: Earthquake terminated = " + task.isSuccessful());
            }
        });

        lastEarthquakeId = null;
        earthquakeIsActive = false;
        devices.clear();

        /*databaseReference.updateChildren(earthquakeTermination).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: saved = " + task.isSuccessful());
                listener.onEarthquakeAdded(task.isSuccessful());
            }
        });*/

        /*// save earthquake from active-earthquakes to saved-earthquakes
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
        });*/
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists()) {
            // in path: /active-events, we store EarthquakeEvent objects using as keys the deviceId
            // field of each EarthquakeEvent objects (one active event by device!!!)
            String deviceId = dataSnapshot.getKey();
            devices.put(deviceId, true);
            if (devices.size() >= minDeviceCount) {
                if (!earthquakeIsActive) {
                    addEarthquake(new Earthquake(devices, true, new Date().getTime()));
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
            if (devices.size() < minDeviceCount) {
                if (earthquakeIsActive) {
                    terminateEarthquake();
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

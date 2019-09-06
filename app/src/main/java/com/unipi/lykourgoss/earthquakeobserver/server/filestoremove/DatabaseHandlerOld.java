package com.unipi.lykourgoss.earthquakeobserver.server.filestoremove;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.lykourgoss.earthquakeobserver.server.models.Earthquake;

import java.util.Date;

/**
 * Created by LykourgosS <lpsarantidis@gmail.com>
 * on 03,September,2019.
 */

public class DatabaseHandlerOld implements ValueEventListener {

    // todo not only 1
    private static final int LEAST_DEVICES = 1;

    private static final String MAJOR_ACTIVE_EVENTS_REF = "major-active-events";
    private static final String EARTHQUAKES_REF = "earthquakes";

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference activeEventsRef;
    private DatabaseReference earthquakesRef;

    private OnEarthquakeListener listener;

    public DatabaseHandlerOld(OnEarthquakeListener listener) {
        activeEventsRef = databaseReference.child(MAJOR_ACTIVE_EVENTS_REF);
        earthquakesRef = databaseReference.child(EARTHQUAKES_REF);
        this.listener = listener;
    }

    public void addListener() {
        activeEventsRef.addValueEventListener(this);
    }

    public void removeListener() {
        activeEventsRef.removeEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            long deviceCount = dataSnapshot.getChildrenCount();
            if (deviceCount >= LEAST_DEVICES) {
//                addEarthquake(new Earthquake(deviceCount, new Date().getTime()));
            }
        }
    }

    public void addEarthquake(Earthquake earthquake) {
        String id = earthquakesRef.push().getKey();
        earthquake.setId(id);
        earthquakesRef.child(id).setValue(earthquake).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onEarthquakeAdded(task.isSuccessful());
            }
        });
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

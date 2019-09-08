package com.unipi.lykourgoss.earthquakeobserver.server.tools;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by LykourgosS <lpsarantidis@gmail.com>
 * on 08,September,2019.
 */

public class EventHandler {

    private static final String MAJOR_ACTIVE_EVENTS_REF = "major-active-events";

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference activeEventsRef;
}

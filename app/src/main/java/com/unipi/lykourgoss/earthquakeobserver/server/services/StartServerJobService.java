package com.unipi.lykourgoss.earthquakeobserver.server.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.unipi.lykourgoss.earthquakeobserver.server.services.ServerService;

/**
 * Created by LykourgosS <lpsarantidis@gmail.com>
 * on 09,July,2019.
 */

public class StartServerJobService extends JobService { // JobService runs in the UI thread by default!!!

    private static final String TAG = "StartServerJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job started");

        startService();
        return false; // false if the task is short and can be executed here (means job is over), true if will be executed in a background thread
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) { // Called if the job was cancelled before being finished or when we manually call jobFinished()
        Log.d(TAG, "Job cancelled before completion");
        return true; // return boolean value -> means if we want to reschedule or not
    }

    private void startService() {
        Intent service = new Intent(this, ServerService.class);

        // to start the service while app is on the background call
        // startForegroundService(intentService), but after 5 seconds max should call
        // startForeground(...) within Service onStartCommand()!!!
        ContextCompat.startForegroundService(this, service);

        //startService(): If this service is not already running, it will be instantiated and
        // started (creating a process for it if needed); if it is running then it remains running
    }
}

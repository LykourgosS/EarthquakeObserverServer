package com.unipi.lykourgoss.earthquakeobserver.server.tools;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.unipi.lykourgoss.earthquakeobserver.server.Constant;
import com.unipi.lykourgoss.earthquakeobserver.server.services.ServerService;
import com.unipi.lykourgoss.earthquakeobserver.server.services.StartServerJobService;

import java.util.List;

/**
 * Created by LykourgosS <lpsarantidis@gmail.com>
 * on 14,July,2019.
 */

public class Util {

    public static final String TAG = "Util";

    public static void scheduleStartJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, StartServerJobService.class);
        // JobId must be unique, otherwise it will replace the previous scheduled job, by our
        // application, with the same jobId
        JobInfo jobInfo = new JobInfo.Builder(Constant.START_SERVICE_JOB_ID, serviceComponent)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                //.setRequiresDeviceIdle(true)
                //.setOverrideDeadline(1000) // The job will be run by this deadline even if other requirements are not met
                //.setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        int resultCode = jobScheduler.schedule(jobInfo);

        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
            Toast.makeText(context, "Job scheduled", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Job scheduling failed");
            Toast.makeText(context, "Job scheduling failed", Toast.LENGTH_SHORT).show();
        }

        // log all pending and started jobs
        List<JobInfo> jobs = jobScheduler.getAllPendingJobs();
        for (JobInfo job : jobs) {
            Log.d(TAG, "scheduleStartJob: " + job.toString());
        }
    }

    public static void restartServer(Context context) {
        Toast.makeText(context, "Server is restarting...", Toast.LENGTH_SHORT).show();
        context.stopService(new Intent(context, ServerService.class));
        Intent service = new Intent(context, ServerService.class);
        ContextCompat.startForegroundService(context, service);
    }

    /*public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }*/
}

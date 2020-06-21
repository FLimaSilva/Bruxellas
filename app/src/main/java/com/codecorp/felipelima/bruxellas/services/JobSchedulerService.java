package com.codecorp.felipelima.bruxellas.services;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.codecorp.felipelima.bruxellas.eventbus.MessageEB;
import com.codecorp.felipelima.bruxellas.view.MainActivity;

import org.greenrobot.eventbus.EventBus;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService{

    public static final String TAG = "Script";

    /*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG,"onStartJob("+ params.getExtras().getString("string") +")");
        new MyAsyncTask(this).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG,"onStopJob()");
        return true;
    }


    //INNER CLASS
    private static class MyAsyncTask extends AsyncTask<JobParameters, Void, String>{
        private JobSchedulerService jss;

        public MyAsyncTask(JobSchedulerService j) {
            this.jss = j;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(JobParameters... params) {
            Log.i(TAG,"doInBackground()");

            String answer = "TestTestando";

            jss.jobFinished(params[0],false);

            return answer;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i(TAG,"onPostExecute()");

            MessageEB m = new MessageEB();
            m.setText(s);

            EventBus.getDefault().post(m);
        }
    }*/

    // This method is called when the service instance
    // is created
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "myService created");
    }

    // This method is called when the service instance
    // is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "myService destroyed");
    }

    // This method is called when the scheduled job
    // is started
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "on start job");
        return true;
    }

    // This method is called when the scheduled job
    // is stopped
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "on stop job");
        return true;
    }

    MainActivity myMainActivity;

    public void setUICallback(MainActivity activity) {
        myMainActivity = activity;
    }


    // This method is called when the start command
    // is fired
    @Override
    public int onStartCommand(Intent intent, int flags,
                              int startId) {
        Messenger callback = intent.getParcelableExtra("messenger");
        Message m = Message.obtain();
        m.what = 2;
        m.obj = this;
        try {
            callback.send(m);
        } catch (RemoteException e) {
            Log.e(TAG, "Error passing service object " +
                    "back to activity.");
        }
        return START_NOT_STICKY;
    }

    // Method that schedules the job
    public void scheduleJob(JobInfo build) {
        Log.i(TAG,"Scheduling job");
        JobScheduler jobScheduler = (JobScheduler)getSystemService
                (Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(build);
    }
}

package com.example.polytech.androidservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by polytech on 17/09/18.
 *
 * The service itself, generating data and notifying listeners.
 */
public class MyService extends Service implements IBackgroundService {
    private Timer timer;
    private BackgroundServiceBinder binder;
    private final ArrayList<IBackgroundServiceListener> listeners = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        //Create variables
        timer = new Timer();
        binder = new BackgroundServiceBinder(this);
        Log.i("MyApp", "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Date parser
        final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String time = df.format(new Date());
                //Notify listeners
                fireDataChanged(time);
                Log.i("MyApp", time);
            }
        }, 0, 1000); //Run every second with no delay
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listeners.clear();
        timer.cancel();
        Log.i("MyApp", "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void addListener(IBackgroundServiceListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(IBackgroundServiceListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify listeners of data change.
     * @param o The new data.
     */
    private void fireDataChanged(Object o)
    {
        for(IBackgroundServiceListener listener : listeners)
        {
            listener.dataChanged(o); //Notify every listener
        }
    }
}

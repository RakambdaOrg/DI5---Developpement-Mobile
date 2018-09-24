package com.example.polytech.androidservices;

import android.os.Binder;

/**
 * Created by polytech on 17/09/18.
 */
public class BackgroundServiceBinder extends Binder{
    private IBackgroundService service = null;

    /**
     * Constructor.
     * @param service The service.
     */
    public BackgroundServiceBinder(IBackgroundService service) {
        super();
        this.service = service;
    }

    /**
     * Get the service.
     * @return The service.
     */
    public IBackgroundService getService()
    {
        return this.service;
    }
}

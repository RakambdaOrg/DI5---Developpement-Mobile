package com.example.polytech.androidservices;

/**
 * Created by polytech on 17/09/18.
 *
 * Used to declare a service that can notify some listeners.
 */
public interface IBackgroundService {
    /**
     * Add a listener.
     * @param listener The listener to add.
     */
    void addListener(IBackgroundServiceListener listener);

    /**
     * Remove a listener.
     * @param listener The listener to remove.
     */
    void removeListener(IBackgroundServiceListener listener);
}

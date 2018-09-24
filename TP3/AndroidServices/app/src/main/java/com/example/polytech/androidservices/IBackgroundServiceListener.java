package com.example.polytech.androidservices;

/**
 * Created by polytech on 17/09/18.
 *
 * A listener that listens for data sent by a service.
 */
public interface IBackgroundServiceListener {
    /**
     * Notify listeners of data change.
     * @param o The new data.
     */
    void dataChanged(Object o);
}

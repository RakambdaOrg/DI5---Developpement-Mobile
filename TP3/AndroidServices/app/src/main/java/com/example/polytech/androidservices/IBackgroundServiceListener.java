package com.example.polytech.androidservices;

/**
 * Created by polytech on 17/09/18.
 */
public interface IBackgroundServiceListener {
    /**
     * Notify listeners of data change.
     * @param o The new data.
     */
    void dataChanged(Object o);
}

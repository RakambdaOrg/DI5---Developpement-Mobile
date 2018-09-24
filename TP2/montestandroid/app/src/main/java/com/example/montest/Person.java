package com.example.montest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by polytech on 11/09/18.
 */
public class Person implements Parcelable {
    private final String firstName;
    private final String lastName;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{getFirstName(), getLastName()});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public Person createFromParcel(Parcel parcel) {
            String[] vals = new String[2];
            parcel.readStringArray(vals);
            return new Person(vals[0], vals[1]);
        }

        @Override
        public Person[] newArray(int i) {
            return new Person[i];
        }
    };
}

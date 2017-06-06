package com.komarekzm.onlyBet.project.models.objects;

import android.os.Parcel;
import android.os.Parcelable;


public class History implements Parcelable {
    private String date;

    private History(Parcel in) {
        date = in.readString();
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public History(String date) {
        this.date = date;
    }
}

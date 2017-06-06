package com.komarekzm.onlyBet.project.models.objects;

import android.os.Parcel;
import android.os.Parcelable;


public class Tip implements Parcelable {
    private String name;
    private String win;
    private String course;
    private String time;
    private String date;
    private String league;

    public Tip(String name, String win, String course, String time, String date, String league) {
        this.name = name;
        this.win = win;
        this.course = course;
        this.time = time;
        this.date = date;
        this.league = league;
    }

    protected Tip(Parcel in) {
        name = in.readString();
        win = in.readString();
        course = in.readString();
        time = in.readString();
        date = in.readString();
        league = in.readString();
    }

    public static final Creator<Tip> CREATOR = new Creator<Tip>() {
        @Override
        public Tip createFromParcel(Parcel in) {
            return new Tip(in);
        }

        @Override
        public Tip[] newArray(int size) {
            return new Tip[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(win);
        dest.writeString(course);
        dest.writeString(time);
        dest.writeString(date);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }
}

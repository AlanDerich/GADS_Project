package com.derich.gads2020project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Leaderboard implements Parcelable {

    private String name;
    private String badgeUrl;
    private String pointAndCountryConcat;
    private int badgePlaceholder;

    public Leaderboard(String name, String pointAndCountryConcat, String badgeUrl, int badgePlaceholder) {
        this.name = name;
        this.pointAndCountryConcat = pointAndCountryConcat;
        this.badgeUrl = badgeUrl;
        this.badgePlaceholder = badgePlaceholder;
    }

    public Leaderboard(Parcel source) {
        name = source.readString();
        badgeUrl = source.readString();
        pointAndCountryConcat = source.readString();
        badgePlaceholder = source.readInt();
    }

    public String getName() {
        return name;
    }
    public String getBadgeUrl() {
        return badgeUrl;
    }
    public int getBadgePlaceholder() { return badgePlaceholder; }
    public String getPointAndCountryConcat() {return pointAndCountryConcat; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(badgeUrl);
        dest.writeString(pointAndCountryConcat);
        dest.writeInt(badgePlaceholder);
    }

    public static final Creator<Leaderboard> CREATOR = new Creator<Leaderboard>() {
        @Override
        public Leaderboard createFromParcel(Parcel source) {
            return new Leaderboard(source);
        }

        @Override
        public Leaderboard[] newArray(int size) {
            return new Leaderboard[size];
        }
    };
}

package se.mah.af6589.assignment2;

import android.graphics.Color;

/**
 * Created by Gustaf Bohlin on 29/09/2017.
 */

public class Member {

    private String name;
    private String group;
    private float longitude, latitude;
    private boolean showOnMap;

    public Member(String name, String group) {
        this.name = name;
        this.group = group;
    }

    public Member(String name, String group, float longitude, float latitude) {
        this.name = name;
        this.group = group;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public boolean isShowOnMap() {
        return showOnMap;
    }

    public void setShowOnMap(boolean showOnMap) {
        this.showOnMap = showOnMap;
    }
}

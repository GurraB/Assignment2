package se.mah.af6589.assignment2;

/**
 * Created by Gustaf Bohlin on 04/10/2017.
 */

public class ImageMarker {

    private String member, message, lat, lng, imageId;

    public ImageMarker(String member, String message, String lat, String lng, String imageId) {
        this.member = member;
        this.message = message;
        this.lat = lat;
        this.lng = lng;
        this.imageId = imageId;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}

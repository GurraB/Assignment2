package se.mah.af6589.assignment2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gustaf Bohlin on 29/09/2017.
 */

public class ServerCommands {

    public static final String TYPE = "type";
    public static final String GROUP = "group";
    public static final String MEMBER = "member";
    public static final String ID = "id";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    public static final String REGISTER = "register";
    public static final String UNREGISTER = "unregister";
    public static final String MEMBERS = "members";
    public static final String GROUPS = "groups";
    public static final String LOCATION = "location";
    public static final String LOCATIONS = "locations";
    public static final String EXCEPTION = "exception";
    public static final String TEXTCHAT = "textchat";
    public static final String TEXT = "text";
    public static final String IMAGECHAT = "imagechat";
    public static final String UPLOAD = "upload";
    public static final String IMAGEID = "imageid";
    public static final String PORT = "port";


    public String register(String group, String name) {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, REGISTER);
            object.put(GROUP, group);
            object.put(MEMBER, name);
        } catch (JSONException e) {
            Log.e("REGISTER", "JSONEXCEPTION");
        }
        return object.toString();
    }

    public String deregister(String id) {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, UNREGISTER);
            object.put(ID, id);
        } catch (JSONException e) {
            Log.e("REGISTER", "JSONEXCEPTION");
        }
        return object.toString();
    }

    public String membersInGroup(String group) {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, MEMBERS);
            object.put(GROUP, group);
        } catch (JSONException e) {
            Log.e("REGISTER", "JSONEXCEPTION");
        }
        return object.toString();
    }

    public String currentGroups() {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, GROUPS);
        } catch (JSONException e) {
            Log.e("REGISTER", "JSONEXCEPTION");
        }
        return object.toString();
    }

    public String setPosition(String id, String longitude, String latitude) {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, LOCATION);
            object.put(ID, id);
            object.put(LONGITUDE, longitude);
            object.put(LATITUDE, latitude);
        } catch (JSONException e) {
            Log.e("REGISTER", "JSONEXCEPTION");
        }
        return object.toString();
    }

    public String positionsInGroup(String group) {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, LOCATIONS);
            object.put(GROUP, group);
        } catch (JSONException e) {
            Log.e("POSITIONSINGROUP", "JSONEXCEPTION");
        }
        return object.toString();
    }

    public String textChat(String userId, String text) {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, TEXTCHAT);
            object.put(ID, userId);
            object.put(TEXT, text);
        } catch (JSONException e) {
            Log.e("TEXTMESSAGE", "JSONEXCEPTION");
        }
        return object.toString();
    }

    public String imageChat(String userId, String text, String longitude, String latitude) {
        JSONObject object = new JSONObject();
        try {
            object.put(TYPE, IMAGECHAT);
            object.put(ID, userId);
            object.put(TEXT, text);
            object.put(LONGITUDE, longitude);
            object.put(LATITUDE, latitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}

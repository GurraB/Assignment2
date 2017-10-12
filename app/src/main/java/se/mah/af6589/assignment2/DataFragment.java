package se.mah.af6589.assignment2;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gustaf Bohlin on 29/09/2017.
 */

public class DataFragment extends Fragment {

    private boolean serviceExists = false;
    private boolean connected = false;
    private boolean bound = false;
    private String longitude = "0";
    private String latitude = "0";
    private ArrayList<String> currentGroups = new ArrayList<>();
    private ArrayList<String> userIds = new ArrayList<>();
    private String name;
    private boolean registerFragmentIsShowing = false;
    private ArrayList<String> groups = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();
    private Member selectedMember;
    private boolean groupDrawerOpen;
    private boolean locateUser = true;
    private ArrayList<Chat> chats = new ArrayList<>();
    private ArrayList<MarkerOptions> markers = new ArrayList<>();
    private boolean chatDrawerOpened;
    private Bitmap bitmapToSend;
    private HashMap<String, byte[]> downloadedBitmaps = new HashMap<>();
    private ArrayList<ImageMarker> imageMarkers = new ArrayList<>();
    private Bitmap bitmapToDisplay;
    private Uri pictureUri;
    private ArrayDeque<String> queuedCalls = new ArrayDeque<>();
    private String selectedChat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public boolean isServiceExists() {
        return serviceExists;
    }

    public void setServiceExists(boolean serviceExists) {
        this.serviceExists = serviceExists;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isBound() {
        return bound;
    }

    public void setBound(boolean bound) {
        this.bound = bound;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRegisterFragmentIsShowing() {
        return registerFragmentIsShowing;
    }

    public void setRegisterFragmentIsShowing(boolean registerFragmentIsShowing) {
        this.registerFragmentIsShowing = registerFragmentIsShowing;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public Member getSelectedMember() {
        return selectedMember;
    }

    public void setSelectedMember(Member selectedMember) {
        this.selectedMember = selectedMember;
    }

    public boolean isGroupDrawerOpen() {
        return groupDrawerOpen;
    }

    public void setGroupDrawerOpen(boolean groupDrawerOpen) {
        this.groupDrawerOpen = groupDrawerOpen;
    }

    public boolean isLocateUser() {
        return locateUser;
    }

    public void setLocateUser(boolean locateUser) {
        this.locateUser = locateUser;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    public Chat getChatForGroup(String groupName) {
        for (Chat chat: chats) {
            if (chat.getGroupName().equals(groupName))
                return chat;
        }
        return null;
    }

    public ArrayList<Member> getMembersInGroup(String group) {
        ArrayList<Member> membersInGroup = new ArrayList<>();
        for (Member m: members)
            if (m.getGroup().equals(group))
                membersInGroup.add(m);
        return membersInGroup;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    public boolean memberExists(Member receivedMember) {
        for (Member m : members)
            if (m.getName().equals(receivedMember.getName()) && m.getGroup().equals(receivedMember.getGroup()))
                return true;
        return false;
    }

    public boolean groupExists(String group) {
        for (String g : groups)
            if (g.equals(group))
                return true;
        return false;
    }

    public ArrayList<String> getCurrentGroups() {
        return currentGroups;
    }

    public void setCurrentGroups(ArrayList<String> currentGroups) {
        this.currentGroups = currentGroups;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public String getUserIdForGroup(String group) {
        for (String userId : userIds)
            if (userId.split(",")[0].equals(group))
                return userId;
        return null;
    }

    public boolean isInGroup(String group) {
        for (String g : currentGroups)
            if (g.equals(group))
                return true;
        return false;
    }

    public ArrayList<MarkerOptions> getMarkers() {
        return markers;
    }

    public void setMarkers(ArrayList<MarkerOptions> markers) {
        this.markers = markers;
    }

    public Member findMember(Member receivedMember) {
        for (Member m : members)
            if (m.getName().equals(receivedMember.getName()) && m.getGroup().equals(receivedMember.getGroup()))
                return m;
        return null;
    }

    public void setChatDrawerOpened(boolean chatDrawerOpened) {
        this.chatDrawerOpened = chatDrawerOpened;
    }

    public Bitmap getBitmapToSend() {
        return bitmapToSend;
    }

    public void setBitmapToSend(Bitmap bitmapToSend) {
        this.bitmapToSend = bitmapToSend;
    }

    public HashMap<String, byte[]> getDownloadedBitmaps() {
        return downloadedBitmaps;
    }

    public void setDownloadedBitmaps(HashMap<String, byte[]> downloadedBitmaps) {
        this.downloadedBitmaps = downloadedBitmaps;
    }

    public ArrayList<ImageMarker> getImageMarkers() {
        return imageMarkers;
    }

    public void setImageMarkers(ArrayList<ImageMarker> imageMarkers) {
        this.imageMarkers = imageMarkers;
    }

    public Bitmap getBitmapToDisplay() {
        return bitmapToDisplay;
    }

    public void setBitmapToDisplay(Bitmap bitmapToDisplay) {
        this.bitmapToDisplay = bitmapToDisplay;
    }

    public Uri getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(Uri pictureUri) {
        this.pictureUri = pictureUri;
    }

    public ArrayDeque<String> getQueuedCalls() {
        return queuedCalls;
    }

    public void setQueuedCalls(ArrayDeque<String> queuedCalls) {
        this.queuedCalls = queuedCalls;
    }

    public String getSelectedChat() {
        return selectedChat;
    }

    public void setSelectedChat(String selectedChat) {
        this.selectedChat = selectedChat;
    }
}

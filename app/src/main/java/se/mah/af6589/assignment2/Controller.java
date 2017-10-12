package se.mah.af6589.assignment2;

import android.Manifest;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gustaf Bohlin on 29/09/2017.
 */
public class Controller {

    private CommunicationService connection;
    private Listener listener;
    private MainActivity activity;
    private ServiceConn serviceConn;
    private DataFragment dataFragment;
    private ServerCommands serverCommands;
    private Timer timer;

    public Controller(MainActivity activity) {
        this.activity = activity;
        initDataFragment();
        initCommunication();
        openRegisterFragment();
        initChat();
        activity.updateUserInformation(dataFragment.getCurrentGroups().toString(), dataFragment.getName());
    }

    public String getToolbarTitle() {
        return activity.getResources().getString(R.string.app_name);
    }

    private void initChat() {
        if (isLoggedIn()) {
            activity.populateChatTabLayout(dataFragment.getCurrentGroups());
            if (dataFragment.getSelectedChat() != null)
                if (dataFragment.isInGroup(dataFragment.getSelectedChat()))
                    chatTabClicked(dataFragment.getSelectedChat());
                else
                    chatTabClicked(dataFragment.getCurrentGroups().get(0));
            else
                chatTabClicked(dataFragment.getCurrentGroups().get(0));
        }
    }

    private void initDataFragment() {
        FragmentManager manager = activity.getFragmentManager();
        dataFragment = (DataFragment) manager.findFragmentByTag("data");
        if (dataFragment == null) {
            dataFragment = new DataFragment();
            manager.beginTransaction().add(dataFragment, "data").commit();
        }
    }

    private void openRegisterFragment() {
        if (!dataFragment.isRegisterFragmentIsShowing() && !isLoggedIn()) {
            new RegisterFragment().show(activity.getFragmentManager(), "register");
            dataFragment.setRegisterFragmentIsShowing(true);
        }
    }

    private void initCommunication() {
        serverCommands = new ServerCommands();
        Intent intent = new Intent(activity, CommunicationService.class);
        if (!dataFragment.isServiceExists()) {
            activity.startService(intent);
            dataFragment.setServiceExists(true);
        }
        serviceConn = new ServiceConn();
        boolean boundsuccessful = activity.bindService(intent, serviceConn, 0);
        dataFragment.setBound(boundsuccessful);
        if (!boundsuccessful)
            Log.e("CONTROLLER", "SERVICE NOT BOUND");
        else
            Log.v("CONTROLLER", "SERVICE BOUND");
    }

    public void connect() {
        connection.connect();
        dataFragment.setConnected(true);
    }

    private void createMarkerForMember(Member member) {
        LatLng markerPos = new LatLng(member.getLatitude(), member.getLongitude());
        if (markerPos.latitude != 0.0 && markerPos.longitude != 0.0) {  //TODO check for NaN instead
            MarkerOptions marker = new MarkerOptions().position(markerPos).title(member.getName()).snippet(member.getGroup()).icon(getColoredMarkerIcon(Color.GREEN));
            activity.addMarker(marker);
            dataFragment.getMarkers().add(marker);
        }
    }
    
    private void createMarkerForImage(ImageMarker marker) {
        byte arr[] = dataFragment.getDownloadedBitmaps().get(marker.getImageId());
        if (arr != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            LatLng markerPos = new LatLng(Double.parseDouble(marker.getLat()), Double.parseDouble(marker.getLng()));
            MarkerOptions markerOptions = new MarkerOptions().position(markerPos).title("Image").snippet(marker.getImageId()).icon(BitmapDescriptorFactory.fromBitmap(bitmap));
            activity.addMarker(markerOptions);
            dataFragment.getMarkers().add(markerOptions);
        }
    }

    private BitmapDescriptor getColoredMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public void onPause() {
        if (activity.isFinishing())
            activity.getFragmentManager().beginTransaction().remove(dataFragment).commit();
    }

    public void onDestoy() {
        if (dataFragment.isBound()) {
            activity.unbindService(serviceConn);
            listener.stopListener();
            dataFragment.setBound(false);
            timer.cancel();
            timer.purge();
        }
        if (activity.isFinishing()) {
            connection.disconnect();
            activity.stopService(new Intent(activity, CommunicationService.class));
        }
    }

    public void logIn(String username) {
        register("login-group", username);
    }

    public void register(String group, String username) {
        String registerRequest = serverCommands.register(group, username);
        dataFragment.setName(username);
        connection.send(registerRequest);
    }

    private void getMembersInGroup(String group) {
        String membersRequest = serverCommands.membersInGroup(group);
        connection.send(membersRequest);
    }

    public void getGroups() {
        String groupsRequest = serverCommands.currentGroups();
        connection.send(groupsRequest);
    }

    public void setLocation() {
        if (isLoggedIn() && connection != null) {
            Log.v("MY LOCATION", "MY LOCATION SENT TO SERVER");
            for (String userId : dataFragment.getUserIds()) {
                String positionRequest = serverCommands.setPosition(userId, dataFragment.getLongitude(), dataFragment.getLatitude());
                connection.send(positionRequest);
            }
        }
    }

    public void leaveGroup(String group) {
        String unregister = serverCommands.deregister(dataFragment.getUserIdForGroup(group));
        connection.send(unregister);
        for (Member m : dataFragment.getMembersInGroup(group))
            m.setShowOnMap(false);
        for (String g : dataFragment.getCurrentGroups()) {
            if (g.equals(group)) {
                dataFragment.getCurrentGroups().remove(g);
                break;
            }
        }
    }

    public void changeUserName(String username) {   //TODO change username
//        String unregister = serverCommands.deregister(dataFragment.getUserId());
//        connection.send(unregister);
//        String register = serverCommands.register(dataFragment.getCurrentGroup(), username);
//        dataFragment.setName(username);
//        connection.send(register);
    }

    public void sendTextMessage(String text, String group) {
        String textRequest = serverCommands.textChat(dataFragment.getUserIdForGroup(group), text);
        connection.send(textRequest);
    }

    public boolean isLoggedIn() {
        if (dataFragment.getUserIds().isEmpty())
            return false;
        return true;
    }

    public DataFragment getDataFragment() {
        return dataFragment;
    }

    public void fabClicked() {
        startCamera(MainActivity.PICTURE);
    }

    public void groupsDrawerOpened() {
        activity.clearGroupsList();
        Log.v("GROUPSDRAWEROPENED", "CONNECTION IS null? " + String.valueOf(connection == null));
        if (connection != null)
            getGroups();
    }

    public void updateUserInformationFragmentContent(UserInformationFragment userInformationFragment) {
        Member selectedMember = dataFragment.getSelectedMember();
        if (selectedMember != null)
            userInformationFragment.updateContent(selectedMember.getName(), activity.getResources().getColor(R.color.colorPrimary, null), String.valueOf(selectedMember.getLatitude()) + "°", String.valueOf(selectedMember.getLongitude()) + "°");
    }

    public void addNewGroupClicked() {
        new AddGroupFragment().show(activity.getFragmentManager(), "addgroup");
    }

    public void markerClicked(Marker marker) {
        if (marker.getTitle().equals("Image")) {
            byte[] arr = dataFragment.getDownloadedBitmaps().get(marker.getSnippet());
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            dataFragment.setBitmapToDisplay(bitmap);
            new ImageFragment().show(activity.getFragmentManager(), "image");
        } else {
            dataFragment.setSelectedMember(dataFragment.findMember(new Member(marker.getTitle(), marker.getSnippet())));
            new UserInformationFragment().show(activity.getSupportFragmentManager(), "bottom");
        }
    }

    public void putOutSavedMarkers() {
        for (MarkerOptions m : dataFragment.getMarkers())
            activity.addMarker(m);
    }

    public void locationChanged(Location location) {
        try {
            dataFragment.setLatitude(String.valueOf(location.getLatitude()));
            dataFragment.setLongitude(String.valueOf(location.getLongitude()));
        } catch (Exception e) {
            Log.e("CONTROLLER", e.getMessage());
        }
    }

    public void locationProviderEnabled(String s) {
        Toast.makeText(activity, "Location has been enabled. phew! close one", Toast.LENGTH_LONG).show();
    }

    public void locationProviderDisabled(String s) {
        Toast.makeText(activity, "Location has been disabled, this app won't work now", Toast.LENGTH_LONG).show();
    }

    public void onResume() {
        timer = new Timer();
        timer.schedule(new SendLocationTask(), 0, 30000);
    }

    public void joinGroup(String group) {
        for (String g : dataFragment.getCurrentGroups()) {
            if (g.equals(group)) {
                Toast.makeText(activity, "You are already in this group", Toast.LENGTH_LONG);
                return;
            }
        }
        register(group, dataFragment.getName());
    }

    public void chatTabClicked(CharSequence text) {
        Chat chat = dataFragment.getChatForGroup(text.toString());
        if (chat != null)
            activity.updateGroupChat(chat.getMessages());
        else
            activity.updateGroupChat(new Chat(text.toString()).getMessages());
        dataFragment.setSelectedChat(text.toString());
        activity.setSelectedChatTab(text.toString());
    }

    public void send(String text) {
        String group = activity.getSelectedTab();
        activity.clearEtChat();
        sendTextMessage(text, group);
    }

    public void chatDrawerOpened() {
        initChat();
    }

    public void sendImageMessage(String group, String text) {
        String sendImageRequest = serverCommands.imageChat(dataFragment.getUserIdForGroup(group), text, dataFragment.getLongitude(), dataFragment.getLatitude());
        if (connection != null)
            connection.send(sendImageRequest);
        else {
            dataFragment.getQueuedCalls().add(sendImageRequest);
        }
    }

    public void refreshMarkers() {
        activity.clearMap();
        dataFragment.setMarkers(new ArrayList<MarkerOptions>());
        for (Member m : dataFragment.getMembers()) {
            if (m.isShowOnMap())
                createMarkerForMember(m);
        }
        for (ImageMarker marker : dataFragment.getImageMarkers()) {
            createMarkerForImage(marker);
        }
        for (MarkerOptions marker : dataFragment.getMarkers()) {
            activity.addMarker(marker);
        }
    }

    private class SendLocationTask extends TimerTask {
        @Override
        public void run() {
            setLocation();
        }
    }

    public void startCamera(int mode) {
        if (mode == MainActivity.THUMBNAIL) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(activity.getPackageManager()) != null)
                activity.startActivityForResult(intent, MainActivity.THUMBNAIL);
        }
        else if (mode == MainActivity.PICTURE) {
            if(Build.VERSION.SDK_INT>=24){  //extrem ghetto solution
                try{
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                dataFragment.setPictureUri(createPictureUri());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, dataFragment.getPictureUri());
                activity.startActivityForResult(intent, MainActivity.PICTURE);
            }
        }
    }

    private Uri createPictureUri() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "JPEG_" + timeStamp + ".jpg";
        File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return Uri.fromFile(new File(dir, filename));
    }

    public Bitmap compressBitmap(String pathToPicture, int maxByteCount, int compression) {
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
        bmpOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToPicture, bmpOptions);

        int photoW = bmpOptions.outWidth;
        int photoH = bmpOptions.outHeight;
        int scaleFactor = Math.min(photoW / compression, photoH / compression);
        bmpOptions.inJustDecodeBounds = false;
        bmpOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(pathToPicture, bmpOptions);
        if (bitmap.getByteCount() > maxByteCount)
            return compressBitmap(pathToPicture, maxByteCount, compression - 10);
        Log.v("BITMAP SIZE", String.valueOf(bitmap.getByteCount()));
        return bitmap;
    }

    public void imageFromCameraReceived(String text) {
        Bitmap bitmap = compressBitmap(dataFragment.getPictureUri().getPath(), 65536, 100);   //trial and error value
        dataFragment.setBitmapToSend(bitmap);
        sendImageMessage(activity.getSelectedTab(), text);
    }

    private class ServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CommunicationService.LocalService localService = (CommunicationService.LocalService) iBinder;
            connection = localService.getService();
            dataFragment.setBound(true);
            listener = new Listener();
            listener.start();
            if (!dataFragment.isConnected())
                connect();
            if (isLoggedIn())
                getGroups();
            while (!dataFragment.getQueuedCalls().isEmpty())
                connection.send(dataFragment.getQueuedCalls().pop());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            dataFragment.setBound(false);
        }
    }

    private class Listener extends Thread {

        private String json;

        public void stopListener() {
            interrupt();
            listener = null;
        }

        @Override
        public void run() {
            while (listener != null) {
                try {
                    json = connection.receive();
                    Log.v("LISTENER", "MESSAGE FROM SERVER: " + json);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recievedData(json);
                        }
                    });
                } catch (Exception e) {
                    listener = null;
                }
            }
        }
    }

    public void recievedData(String message) {
        try {
            JSONObject result = new JSONObject(message);
            String responseType = result.getString("type");
            switch (responseType) {
                case ServerCommands.REGISTER:
                    registerReceived(result);
                    break;
                case ServerCommands.UNREGISTER:
                    unregisterReceived(result);
                    break;
                case ServerCommands.MEMBERS:
                    membersReceived(result);
                    break;
                case ServerCommands.GROUPS:
                    groupsReceived(result);
                    break;
                case ServerCommands.LOCATION:
                    locationReceived(result);
                    break;
                case ServerCommands.LOCATIONS:
                    locationsReceived(result);
                    break;
                case ServerCommands.TEXTCHAT:
                    textChatReceived(result);
                    break;
                case ServerCommands.UPLOAD:
                    imageUploadReceived(result);
                    break;
                case ServerCommands.IMAGECHAT:
                    imageChatReceived(result);
                    break;
                case ServerCommands.EXCEPTION:
                    Log.e("RECIEVEDDATA", "EXCEPTION FROM SERVER");
                    Log.e("ACTUAL MESS FROM SERVER", result.toString());
                    Log.e("RECIEVEDDATA", result.getString("message"));
                    String errorMessage = result.getString("message");
                    /*if (errorMessage.contains("Bad argument"))
                        reLogin();
                    if (errorMessage.equals("Unable to add " + dataFragment.getName() + " to login-group")) {
                        RegisterFragment registerFragment = (RegisterFragment) activity.getFragmentManager().findFragmentByTag("register");
                        if (registerFragment != null)
                            ((RegisterFragment) activity.getFragmentManager().findFragmentByTag("register")).notifyDataReceived(activity, false);
                        else {
                            dataFragment.setUserId(null);
                            dataFragment.setCurrentGroup(null);
                            dataFragment.setName(null);
                            registerFragment = new RegisterFragment();
                            registerFragment.show(activity.getFragmentManager(), "register");
                            registerFragment.displayErrorMessage(activity, "An error occurred, please login again");
                        }
                    }*/
                    break;
            }
        } catch (JSONException e) {
            Log.e("RECIEVED DATA", "JSON EXCEPTION");
            Log.e("RECIEVED DATA", e.getMessage());
        } catch (UnknownHostException e) {
            Log.e("UNKNOWN HOST", "UNKNOWN HOST!!!!");
        }
    }

    private void registerReceived(JSONObject result) throws JSONException {
        String group = result.getString(ServerCommands.GROUP);
        String id = result.getString(ServerCommands.ID);
        dataFragment.getCurrentGroups().add(group);
        dataFragment.getUserIds().add(id);
        RegisterFragment registerFragment = (RegisterFragment) activity.getFragmentManager().findFragmentByTag("register");
        if (registerFragment != null)
            registerFragment.notifyDataReceived(activity, true);
        getGroups();
        activity.updateUserInformation(group, dataFragment.getName());
        activity.populateChatTabLayout(dataFragment.getCurrentGroups());
        Log.v("REGISTER RECIEVED", id);
    }

    private void unregisterReceived(JSONObject result) throws JSONException {
        String id = result.getString(ServerCommands.ID);
        for (int i = 0; i < dataFragment.getUserIds().size(); i++) {
            if (dataFragment.getUserIds().get(i).equals(id))
                dataFragment.getUserIds().remove(i);
        }
        if (dataFragment.getUserIds().isEmpty())
            new RegisterFragment().show(activity.getFragmentManager(), "register");
        getGroups();
    }

    private void membersReceived(JSONObject result) throws JSONException {
        String group = result.getString(ServerCommands.GROUP);
        JSONArray jsonMembers = result.getJSONArray(ServerCommands.MEMBERS);
        ArrayList<Member> newMembers = new ArrayList<>();

        for (int i = 0; i < jsonMembers.length(); i++) {
            JSONObject jsonMember = jsonMembers.getJSONObject(i);
            Member receivedMember = new Member(jsonMember.getString(ServerCommands.MEMBER), group);
            Member foundMember = dataFragment.findMember(receivedMember);
            if (foundMember != null)
                newMembers.add(foundMember);
            else
                newMembers.add(receivedMember);
        }
        for (Member m : dataFragment.getMembersInGroup(group))
            dataFragment.getMembers().remove(m);
        dataFragment.getMembers().addAll(newMembers);
        Log.v("--------", "--------------------------");
        for (Member m: dataFragment.getMembers()) {
            Log.v("CURRENT MEMBERS", "name: " + m.getName() + "\tgroup: " + m.getGroup());
        }
        Log.v("--------", "--------------------------");
        notifyMembersReceived();
    }

    private void groupsReceived(JSONObject result) throws JSONException {
        JSONArray jsonGroups = result.getJSONArray(ServerCommands.GROUPS);
        dataFragment.setGroups(new ArrayList<String>());
        for (int i = 0; i < jsonGroups.length(); i++) {
            JSONObject jsonGroup = jsonGroups.getJSONObject(i);
            String group = jsonGroup.getString(ServerCommands.GROUP);
            dataFragment.getGroups().add(group);
        }
        notifyGroupsReceived();
    }

    private void locationReceived(JSONObject result) throws JSONException {
        String id = result.getString(ServerCommands.ID);
        String longitude = result.getString(ServerCommands.LONGITUDE);
        String latitude = result.getString(ServerCommands.LATITUDE);
        Log.v("RECIEVED", "LOCATION UPDATED ON SERVER FOR ID: " + id + ", longitude: " + longitude + " , latitude: " + latitude);
    }

    private void locationsReceived(JSONObject result) throws JSONException {
        String group = result.getString(ServerCommands.GROUP);
        JSONArray array = result.getJSONArray(ServerCommands.LOCATION);
        ArrayList<Member> newMembers = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonMember = array.getJSONObject(i);
            String memberName = jsonMember.getString(ServerCommands.MEMBER);
            float longitude, latitude;
            try {
                longitude = Float.valueOf(jsonMember.getString(ServerCommands.LONGITUDE));
                latitude = Float.valueOf(jsonMember.getString(ServerCommands.LATITUDE));
            } catch (Exception e) {
                longitude = 12.999910f; //Malmö C
                latitude = 55.609068f;
            }

            Member receivedMember = new Member(jsonMember.getString(ServerCommands.MEMBER), group);
            Member foundMember = dataFragment.findMember(receivedMember);
            if (foundMember != null) {
                foundMember.setLatitude(latitude);
                foundMember.setLongitude(longitude);
                newMembers.add(foundMember);
            }
            else {
                receivedMember.setLatitude(latitude);
                receivedMember.setLongitude(longitude);
                newMembers.add(receivedMember);
            }
            Log.v("LOCATIONSRECEIVED", "MEMBER: " + memberName + " (" + String.valueOf(latitude) + " , " + String.valueOf(longitude) + ")");
        }
        for (Member m : dataFragment.getMembersInGroup(group))
            dataFragment.getMembers().remove(m);
        dataFragment.getMembers().addAll(newMembers);

        activity.clearMap();
        dataFragment.setMarkers(new ArrayList<MarkerOptions>());
        showMembersOnMap();
    }

    private void showMembersOnMap() {
        for (Member m : dataFragment.getMembers())
            if (m.isShowOnMap())
                createMarkerForMember(m);
        for (int i = 0; i < dataFragment.getImageMarkers().size(); i++) {
            createMarkerForImage(dataFragment.getImageMarkers().get(i));
        }
    }

    private void textChatReceived(JSONObject result) throws JSONException {
        try {
            String group = result.getString(ServerCommands.GROUP);
            String member = result.getString(ServerCommands.MEMBER);
            String text = result.getString(ServerCommands.TEXT);
            addMessageToGroupChat(group, member, text, null, 0);
            Log.v("TEXTMESSAGE", "[" +group + "]\t" + member + ":" + text);
            if (!member.equals(dataFragment.getName()))
                notifyMessageReceived(group);
        } catch (JSONException e) {
            if (!e.getMessage().equals("No value for group"))
                throw new JSONException(e.getMessage());
        }
    }

    private void imageUploadReceived(JSONObject result) throws JSONException, UnknownHostException {
        String imageId = result.getString(ServerCommands.IMAGEID);
        String port = result.getString(ServerCommands.PORT);
        InetAddress address = InetAddress.getByName(CommunicationService.ip);
        SendImage sendImage = new SendImage(address, Integer.parseInt(port));
        sendImage.execute(new BitmapPackage(imageId, dataFragment.getBitmapToSend()));
        dataFragment.setBitmapToSend(null);
    }

    private void imageChatReceived(JSONObject result) throws JSONException {
        String group = result.getString(ServerCommands.GROUP);
        String member = result.getString(ServerCommands.MEMBER);
        String text = result.getString(ServerCommands.TEXT);
        String longitude = result.getString(ServerCommands.LONGITUDE);
        String latitude = result.getString(ServerCommands.LATITUDE);
        String imageId = result.getString(ServerCommands.IMAGEID);
        String port = result.getString(ServerCommands.PORT);
        addMessageToGroupChat(group, member, text, imageId, Integer.parseInt(port));
        ImageMarker marker = new ImageMarker(member, text, latitude, longitude, imageId);
        if (!member.equals(dataFragment.getName()))
            notifyMessageReceived(group);
        dataFragment.getImageMarkers().add(marker);
        activity.clearMap();
        showMembersOnMap();
    }

    private void notifyGroupsReceived() {
        ArrayList<String> groups = dataFragment.getGroups();
        for (int i = 0; i < groups.size(); i++) {
            getMembersInGroup(groups.get(i));
        }
        activity.updateActiveGroups(dataFragment.getCurrentGroups().toString());
    }

    private void notifyMembersReceived() {
        activity.updateGroupsList(dataFragment.getGroups());
    }

    private void notifyMessageReceived(String group) {
        Snackbar.make(activity.getParentLayout(), "New message in " + group, Snackbar.LENGTH_SHORT).setAction("Read", null).show();
    }

    private void addMessageToGroupChat(String group, String member, String text, String imageId, int port) {
        Chat chat = dataFragment.getChatForGroup(group);
        if (chat == null) {
            chat = new Chat(group);
            chat.addMessage(member, text, imageId, port);
            ArrayList<Chat> chats = dataFragment.getChats();
            chats.add(chat);
        } else
            chat.addMessage(member, text, imageId, port);
        activity.updateGroupChat(chat.getMessages());
    }

    public void changeLanguage(String language) {
        language = (language.equals("EN") ? "sv" : "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getApplicationContext().getResources().updateConfiguration(config, null);
        activity.recreate();
    }
}
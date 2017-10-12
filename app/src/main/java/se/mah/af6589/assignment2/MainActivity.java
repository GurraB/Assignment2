package se.mah.af6589.assignment2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static int THUMBNAIL = 1, PICTURE = 2;

    private Controller controller;
    private Listener listener = new Listener();
    private DrawerLayout drawerLayout;
    private NavigationView groupsView, chatView;
    private Toolbar toolbar;
    private SupportMapFragment map;
    private Button btnNewAddGroup;
    private GoogleMap googleMap;

    private RecyclerView rvGroups;
    private SwipeRefreshLayout swipeRefreshGroups;
    private TabLayout tabLayoutChat;
    private RecyclerView rvChat;
    private EditText etChat;
    private ImageView ibChat, ibCamera;
    private TextView tvUsername, tvGroups;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        attachListeners();
        controller = new Controller(this);
        getSupportActionBar().setTitle(controller.getToolbarTitle());
    }

    private void initComponents() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        swipeRefreshGroups = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_groups);
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        btnNewAddGroup = (Button) findViewById(R.id.btn_add_group);
        initToolbar();
        initRecyclerViews();

        groupsView = (NavigationView) findViewById(R.id.nav_view_left);
        chatView = (NavigationView) findViewById(R.id.nav_view_right);
        tabLayoutChat = (TabLayout) findViewById(R.id.tablayout_chat);
        etChat = (EditText) findViewById(R.id.et_chat);
        ibChat = (ImageView) findViewById(R.id.ib_send_chat);
        ibCamera = (ImageView) findViewById(R.id.iv_send_camera);
        tvUsername = (TextView) groupsView.getHeaderView(0).findViewById(R.id.tv_username);
        tvGroups = (TextView) groupsView.getHeaderView(0).findViewById(R.id.tv_user_groups);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void attachListeners() {
        drawerLayout.addDrawerListener(listener);
        map.getMapAsync(this);
        swipeRefreshGroups.setOnRefreshListener(listener);
        btnNewAddGroup.setOnClickListener(listener);
        tabLayoutChat.addOnTabSelectedListener(listener);
        ibChat.setOnClickListener(listener);
        ibCamera.setOnClickListener(listener);
    }

    private void initRecyclerViews() {
        rvGroups = (RecyclerView) findViewById(R.id.rv_groups);
        rvGroups.setLayoutManager(new LinearLayoutManager(this));
        rvGroups.addItemDecoration(new SpacingItemDecoration(10));

        rvChat = (RecyclerView) findViewById(R.id.rv_chat);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START) || drawerLayout.isDrawerOpen(GravityCompat.END)) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_item_chat) {
            if (!drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.openDrawer(GravityCompat.END);
            else if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            return true;
        }
        if (id == android.R.id.home) {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START);
            else if (drawerLayout.isDrawerOpen(GravityCompat.START))    //unnecessary?
                drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if (id == R.id.menu_item_language) {
            Log.v("MAINACTIVITY", "CHANGE LANGUAGE");
            controller.changeLanguage(item.getTitle().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0); //Ask permission
        }
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setOnMarkerClickListener(listener);
        controller.putOutSavedMarkers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == THUMBNAIL && resultCode == Activity.RESULT_OK) {
            Bitmap thumbnail = data.getParcelableExtra("data");
        }
        if (requestCode == PICTURE && resultCode == Activity.RESULT_OK) {
            controller.imageFromCameraReceived(etChat.getText().toString());
            etChat.setText("");
        }
    }

    public Controller getController() {
        return controller;
    }

    public void setSwipeRefreshGroupsSpinning(boolean spinning) {
        swipeRefreshGroups.setRefreshing(spinning);
    }

    public void updateGroupsList(ArrayList<String> groups) {
        setSwipeRefreshGroupsSpinning(false);
        rvGroups.setAdapter(new GroupRecyclerViewAdapter(this, groups));
    }

    public void clearGroupsList() {
        setSwipeRefreshGroupsSpinning(true);
        rvGroups.setAdapter(new GroupRecyclerViewAdapter(this, new ArrayList<String>()));
    }

    public void addMarker(MarkerOptions marker) {
        try {
            googleMap.addMarker(marker);
        } catch (Exception e) {
            Log.e("MAINACTIVITY", "EXCEPTION");
            Log.e("MAINACTIVITY", e.getMessage());
        }
    }

    public void clearMap() {
        googleMap.clear();
    }

    public View getParentLayout() {
        return drawerLayout;
    }

    public void populateChatTabLayout(ArrayList<String> tabTitles) {
        tabLayoutChat.removeAllTabs();
        for (String title : tabTitles)
            tabLayoutChat.addTab(tabLayoutChat.newTab().setText(title));
        if (tabLayoutChat.getTabAt(0) != null)
            tabLayoutChat.getTabAt(0).select();
    }

    public void setSelectedChatTab(String title) {
        for (int i = 0; i < tabLayoutChat.getTabCount(); i++) {
            Tab tab = tabLayoutChat.getTabAt(i);
            if (title.equals(tab.getText().toString())) {
                tab.select();
                break;
            }
        }
    }

    public void updateGroupChat(ArrayList<Chat.Message> messages) {
        rvChat.setAdapter(new ChatRecyclerViewAdapter(messages, this));
    }

    public void updateUserInformation(String group, String name) {
        if (name == null)
            tvUsername.setText("");
        else
            tvUsername.setText(name);
    }

    public String getSelectedTab() {
        return tabLayoutChat.getTabAt(tabLayoutChat.getSelectedTabPosition()).getText().toString();
    }

    public void clearEtChat() {
        etChat.setText("");
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public void updateActiveGroups(String groups) {
        tvGroups.setText(groups);
    }

    private class Listener implements View.OnClickListener, DrawerLayout.DrawerListener, SwipeRefreshLayout.OnRefreshListener, GoogleMap.OnMarkerClickListener, LocationListener, TabLayout.OnTabSelectedListener {

        @Override
        public void onClick(View view) {
            if (view == btnNewAddGroup)
                controller.addNewGroupClicked();
            if (view == ibChat) {
                String textMessage = etChat.getText().toString();
                controller.send(textMessage);
                hideKeyboard();
            }
            if (view == ibCamera) {
                hideKeyboard();
                controller.startCamera(PICTURE);
            }
        }

        /**
         * Drawers handler
         * @param drawerView
         * @param slideOffset
         */
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                controller.groupsDrawerOpened();
                controller.getDataFragment().setGroupDrawerOpen(true);
            }
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                controller.chatDrawerOpened();
                controller.getDataFragment().setChatDrawerOpened(true);
            }
            Log.v("DRAWER", "DRAWER OPENED");
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            setSwipeRefreshGroupsSpinning(true);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                controller.getDataFragment().setGroupDrawerOpen(false);
            }
            Log.v("DRAWER", "DRAWER CLOSED");
        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }


        /**
         * Refresh groups handler
         */
        @Override
        public void onRefresh() {
            clearGroupsList();
            Log.v("MAINACTIVITY", "REFRESHED");
            controller.getGroups();
        }

        /**
         * Marker handler
         * @param marker
         * @return
         */
        @Override
        public boolean onMarkerClick(Marker marker) {
            controller.markerClicked(marker);
            return true;
        }


        /**
         * Location handlers
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            Log.v("LOCATIONCHANGED", "lat: " + String.valueOf(location.getLatitude()) + "\tlng: " + String.valueOf(location.getLongitude()));
            controller.locationChanged(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}

        @Override
        public void onProviderEnabled(String s) {
            controller.locationProviderEnabled(s);
        }

        @Override
        public void onProviderDisabled(String s) {
            controller.locationProviderDisabled(s);
        }

        /**
         * Tablayout handlers
         * @param tab
         */
        @Override
        public void onTabSelected(Tab tab) {
            if (controller != null)
                controller.chatTabClicked(tab.getText());
        }

        @Override
        public void onTabUnselected(Tab tab) {

        }

        @Override
        public void onTabReselected(Tab tab) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0); //Ask permission
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, listener);
        controller.locationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        controller.locationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        controller.onResume();
    }

    @Override
    protected void onPause() {
        controller.onPause();
        locationManager.removeUpdates(listener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        controller.onDestoy();
        super.onDestroy();
    }
}

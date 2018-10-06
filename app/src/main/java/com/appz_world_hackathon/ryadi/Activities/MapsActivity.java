package com.appz_world_hackathon.ryadi.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.appz_world_hackathon.ryadi.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // TODO enhancing map as other professional apps

    private static final int MY_PERMISSION_FOR_ACCESS_LOCATION = 1;

    private GoogleMap mMap;


    private LatLng southwest;
    private LatLng northeast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getPermissionToAccessLocation();



        if (checkGooglePlayServices()) {
            Handler UI_HANDLER = new Handler();
            UI_HANDLER.postDelayed(UI_UPDATE_RUNNABLE, 500);
        }

    }


    private boolean checkGooglePlayServices() {
        final int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.e("Google Play Services", GoogleApiAvailability.getInstance().getErrorString(status));

            // ask user to update google play services.
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, status, 1);
            dialog.show();
            return false;
        } else {
            Log.i("Google Play Services", GoogleApiAvailability.getInstance().getErrorString(status));
            // google play services is updated.
            //your code goes here...
            return true;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // mMap.setTrafficEnabled(true);
       // mMap.setIndoorEnabled(true);
       // mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        //myMap(googleMap);
    }

    Runnable UI_UPDATE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            myMap(mMap);
        }
    };

    private void myMap(GoogleMap googleMap){

            GoogleMapOptions options = new GoogleMapOptions().liteMode(false);
            options.getMapToolbarEnabled();
            options.zoomControlsEnabled(true);
            googleMap.setMapType(options.getMapType());

            // *** Focus & Zoom
            Double Latitude = 21.6214211;
            Double Longitude = 39.152502;
            LatLng coordinate = new LatLng(Latitude, Longitude);

            // Gets screen size
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            southwest = new LatLng(21.6848645, 39.1169809);
            northeast = new LatLng(21.7288889, 39.1956396);
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(southwest)
                    .include(northeast)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height,0));
            //        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 17));
            // CameraPosition cameraPosition = new CameraPosition.Builder().target(coordinate).zoom(cameraZoom).build();
            //   mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.addMarker(new MarkerOptions().position(coordinate).title(getString(R.string.bolling_club))).showInfoWindow();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(coordinate).zoom(12).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        options.zoomControlsEnabled(true);

    }





    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == MY_PERMISSION_FOR_ACCESS_LOCATION) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read LOCATION permission denied", Toast.LENGTH_SHORT).show();
            } else {
                // showRationale = false if user clicks Never Ask Again, otherwise true
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);


                if (showRationale) {
                    // do something here to handle degraded mode
                } else {
                    Toast.makeText(this, "Read LOCATION permission granted", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        recreate();
    }


    @TargetApi(23)
    public void getPermissionToAccessLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION);


            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, MY_PERMISSION_FOR_ACCESS_LOCATION
            );

        }
    }

}

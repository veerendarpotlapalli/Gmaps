package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
//import android.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.gmaps.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnCameraMoveListener,GoogleMap.OnCameraMoveStartedListener,GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;
    final int ACCESS_LOCATION_REQUEST_CODE = 1002;
    Geocoder geocoder;
    SupportMapFragment mapFragment; // for search view
    SearchView searchView;
    EditText edit,edit2;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // search view
        edit = findViewById(R.id.edit);
        edit2 = findViewById(R.id.edit2);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag);

        try {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
            mapFragment.getMapAsync(this);
            geocoder = new Geocoder(this);

        } catch (Exception e) {
            e.printStackTrace();
        }// catch

        edit.setInputType(InputType.TYPE_NULL);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        edit.setCursorVisible(false);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this,pick_add.class);
                startActivity(intent);
            } //onclick
        });//onclicklistner
    } //on create

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

        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            currentlocation();
            zoomtouserlocation();
        } //check self permission (if)
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // we can say why this permission is needed.....
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
        } // else

        try {
            String location = getIntent().getStringExtra("loc").toString().trim();
            if (location != null || !location.equals("")) {
                newlocate(location);
            }//if
        }catch (Exception e){
            e.printStackTrace();
        }//catch

    } //on map ready

    @SuppressLint("MissingPermission")
    private void currentlocation() {
        mMap.setMyLocationEnabled(true);
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, MapsActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }//catch
    } // currentlocation

    private void zoomtouserlocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
            } // onsuccess
        }); //addOnsuccessListner

    } // zoomtouserlocation


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ACCESS_LOCATION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            currentlocation();
            zoomtouserlocation();
        } else {
            // we can say why this permission is needed.......
            if(! ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){ // this is a block if completely denied permission

                new AlertDialog.Builder(MapsActivity.this)
                        .setMessage("you have permanently deneid the permission to allow permisson go to settings ")
                        .setPositiveButton("Go to setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                gotosettings();
                            } // onclick
                        }) //positive button
                        .setNegativeButton("cancel",null)
                        .setCancelable(false)
                        .show();
            } //if

        }
    } //onRequestPermissionsResult

    private void gotosettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
        Uri uri = Uri.fromParts("package",this.getPackageName(),null);
        intent.setData(uri);
        startActivity(intent);
    } // gotosettings

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude,mMap.getCameraPosition().target.longitude,1);
            String address = addresses.get(0).getAddressLine(0);
            edit.setText(address);
        }catch (Exception e){
            e.getMessage();
        }//catch
    }//onlocationchange

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onCameraIdle() {

        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
            String addr = addresses.get(0).getAddressLine(0);

            if (addresses != null) {
                edit.setText(addr);
            }//if

        }catch (Exception e) {
            e.printStackTrace();
        }//catch

    }//oncameraidle

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {
    }

    public void newlocate(String location){

    try{
        List<Address> addressList;
    Geocoder geocoder = new Geocoder(MapsActivity.this,Locale.getDefault());
    addressList = geocoder.getFromLocationName(location, 1);
    Address address = addressList.get(0);
    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
    mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
        @Override
        public void onCameraIdle() {
            //Geocoder geocoder1 = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocationName(location,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String addressoo = addresses.get(0).getAddressLine(0);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            ImageView imageView = findViewById(R.id.pin);
            imageView.setVisibility(View.GONE);
            edit.setText(addressoo);
        }//onCameraidle
    });//setOnCameraIdleListener
            } catch (Exception e) {
                    e.printStackTrace();
                    }//catch
    }//newlocate

} // main

package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.gmaps.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLEngineResult;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;
    final int ACCESS_LOCATION_REQUEST_CODE = 1002;
    Geocoder geocoder;
    SupportMapFragment mapFragment; // for search view
    SearchView searchView; // for search view


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // search view
        searchView = findViewById(R.id.search);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag);

        //to autofill places api
       /* Places.initialize(getApplicationContext(),"AIzaSyCALb4qP-Dcjg5mChzcjLCY2u7PhCRV79k");
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // initialising place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                                                        ,Place.Field.LAT_LNG,Place.Field.NAME);
                //create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList)
                                                    .build(MapsActivity.this);
                // start activiy result
                startActivityForResult(intent,100);

            } //on click
        }); // setonclick */

        //after text entered
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("MissingPermission")
            @Override
            public boolean onQueryTextSubmit(String query) {
                        // initialise location
                        String location = searchView.getQuery().toString();
                        if(location != null){
                            Geocoder geocoder = new Geocoder(MapsActivity.this);
                            try {
                            //initialise addresslist
                                List<Address> addressList = geocoder.getFromLocationName(location,1);
                                Address address = addressList.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                                String city = hereLocation(address.getLatitude(),address.getLongitude());

                                //String a = city;
                                String b = "khammam";
                                if(city.equalsIgnoreCase(b)) {
                                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                } else {
                                    Toast.makeText(MapsActivity.this,"Sorry....",Toast.LENGTH_SHORT).show();
                                }//else
                            } catch (IOException e) {
                                e.printStackTrace();
                            } //catch

                        }else{
                            Toast.makeText(MapsActivity.this,"Noo....",Toast.LENGTH_SHORT).show();
                        } //else

                return false;
            } // onQueryTextSubmit

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        }); // setOnQueryTextListener


        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frag);
            mapFragment.getMapAsync(this);
            geocoder = new Geocoder(this);

        } catch (Exception e) {
            e.printStackTrace();
        }// catch

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
        // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        /* LatLng latLng = new LatLng(17.25, 80.15);
        // marker options
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(" KHAMMAM ").snippet(" STAMBADRI ");
        // TO ADD THIS MARKER
        mMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,10);
        mMap.animateCamera(cameraUpdate); */

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

    } //on map ready

    private void currentlocation() {

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
        mMap.setMyLocationEnabled(true);
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


   /* @Override  // place api
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK) {
            // when success
            //intialize places
            Place place = Autocomplete.getPlaceFromIntent(data);
            editText.setText(place.getAddress());

        } //if
        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(MapsActivity.this,status.getStatusMessage(),Toast.LENGTH_SHORT).show();
        } //else if
    } //onActivityResult */


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

    // for getting city name
    private String hereLocation(double lat, double lng){
        String cityname = "";
        Geocoder geocoder = new Geocoder(MapsActivity.this,Locale.getDefault());
        List<Address> addresses;
        try{
            addresses = geocoder.getFromLocation(lat,lng,10);
            if(addresses.size()>0){
                for(Address adr : addresses){
                    if(adr.getLocality()!=null && adr.getLocality().length()>0){
                        cityname = adr.getLocality();
                        break;
                    }//if
                }//for
            }//if
        }catch(Exception e){
            e.printStackTrace();
        } // catch
        return cityname;
    } //hereLocation

} // main

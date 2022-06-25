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
import android.app.FragmentManager;
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
import android.widget.Button;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FusedLocationProviderClient fusedLocationProviderClient;
    final int ACCESS_LOCATION_REQUEST_CODE = 1002;
    Geocoder geocoder;
    SupportMapFragment mapFragment; // for search view
    SearchView searchView;
    EditText edit;
    LocationManager locationManager;
    EditText editpick,editdest;
    Button button, button2;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editpick = (findViewById(R.id.editpick));
        editdest = (findViewById(R.id.editdest));

        editpick.setVisibility(View.GONE);
        editdest.setVisibility(View.GONE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // search view
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        edit = findViewById(R.id.edit);
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
                Intent intent = new Intent(MapsActivity.this, pick_add.class);
                startActivity(intent);
            } //onclick
        });//onclicklistner

        try {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    String location = edit.getText().toString();
                    if(location != null) {
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                        //initialise addresslist
                        List<Address> addressList = null;
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        Address address = addressList.get(0);
                        //LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        String city = hereLocation(address.getLatitude(), address.getLongitude());

                        //String a = city;
                        String b = "khammam";
                        if (city.equalsIgnoreCase(b)) {

                            if (edit.getText().toString() != null || !edit.getText().toString().equals("")) {
                                String picksadd = edit.getText().toString();
                                Intent intent = new Intent(MapsActivity.this, pick_dest.class);
                                intent.putExtra("picks", picksadd);
                                startActivity(intent);
                            }//if

                        }else{
                            Toast.makeText(MapsActivity.this,"sorry......",Toast.LENGTH_SHORT).show();
                        }//else
                    }//location != null
                }//onclick
            });//onclicklistner
        } catch (Exception e) {
            e.printStackTrace();
        }//catch

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
            String location = getIntent().getStringExtra("loc");
            if (location != null || !location.equals("")) {
                picklocate(location);
            }else{
                edit.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                Toast.makeText(MapsActivity.this,"please enter a correct place",Toast.LENGTH_SHORT).show();
            }//else
        } catch (Exception e) {
            e.printStackTrace();
        }//catch

        next();

    } //on map ready

    @SuppressLint("MissingPermission")
    private void currentlocation() {
        mMap.setMyLocationEnabled(true);
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, MapsActivity.this);
        } catch (Exception e) {
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
                LatLng latLng3 = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng3, 16));
            } // onsuccess
        }); //addOnsuccessListner

    } // zoomtouserlocation


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) ;
            currentlocation();
            zoomtouserlocation();
        } else {
            // we can say why this permission is needed.......
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) { // this is a block if completely denied permission

                new AlertDialog.Builder(MapsActivity.this)
                        .setMessage("you have permanently deneid the permission to allow permisson go to settings ")
                        .setPositiveButton("Go to setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                gotosettings();
                            } // onclick
                        }) //positive button
                        .setNegativeButton("cancel", null)
                        .setCancelable(false)
                        .show();
            } //if

        }
    } //onRequestPermissionsResult

    private void gotosettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    } // gotosettings

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1);
             String address = addresses.get(0).getAddressLine(0);

                    edit.setText(address);

        } catch (Exception e) {
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

            edit.setText(addr);

        } catch (Exception e) {
            e.printStackTrace();
        }//catch

    }//oncameraidle

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {
    }

    public void picklocate(String location) {

        try {
            List<Address> addressList;
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            addressList = geocoder.getFromLocationName(location, 1);
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            String city = hereLocation(address.getLatitude(),address.getLongitude());

            //String a = city;
            String b = "khammam";
            if(city.equalsIgnoreCase(b)) {

                mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        //Geocoder geocoder1 = new Geocoder(MapsActivity.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocationName(location, 1);
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
            }else {
                Toast.makeText(MapsActivity.this,"sorry...",Toast.LENGTH_SHORT).show();
            }//else

        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//picklocate

    public void next() {

        ImageView imageView = findViewById(R.id.pin);

        try {
            String pick = getIntent().getStringExtra("vamshi");
            String dest = getIntent().getStringExtra("sai");
            //EditText editpick = (findViewById(R.id.editpick));
            //EditText editdest = (findViewById(R.id.editdest));
            editpick.setText(pick);
            editdest.setText(dest);


            String location = editdest.getText().toString(); // here we are saving texted_location(query) in location
            String location2 = editpick.getText().toString();

            List<Address> addressList = null;
            List<Address> addressList2 = null;

            if ((location != null || !location.equals("")) && (location2 != null || !location2.equals(""))) {

                Geocoder geocoder = new Geocoder(MapsActivity.this);
                Geocoder geocoder2 = new Geocoder(MapsActivity.this);

                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                    addressList2 = geocoder2.getFromLocationName(location2, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                } // catch
                Address address = addressList.get(0);
                Address address2 = addressList2.get(0);

                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                LatLng latLng2 = new LatLng(address2.getLatitude(), address2.getLongitude());

                imageView.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);

                Geocoder geocoderoo = new Geocoder(MapsActivity.this);
                List<Address> addressListoo = geocoderoo.getFromLocationName(location,1);
                Address addressoo = addressListoo.get(0);
                //LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                String city = hereLocation(addressoo.getLatitude(),addressoo.getLongitude());

                //String a = city;
                String b = "khammam";
                if(city.equalsIgnoreCase(b)) {

                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.addMarker(new MarkerOptions().position(latLng2).title(location2));

                    mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {

                        private Geocoder geocoderr;

                        @Override
                        public void onCameraIdle() {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 16));
                        }//oncameraidle
                    });//setOnCameraIdleListener

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MapsActivity.this,"okay.....",Toast.LENGTH_SHORT).show();
                }//onclick
            });//onclicklistner

                }else{
                    imageView.setVisibility(View.VISIBLE);
                    button2.setVisibility(View.GONE);
                    Toast.makeText(MapsActivity.this,"sorry....",Toast.LENGTH_SHORT).show();
                }//else

        }else{
                edit.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                Toast.makeText(MapsActivity.this,"please enter a correct place",Toast.LENGTH_SHORT).show();
            }//else
        } catch (Exception e) {
            e.printStackTrace();
        } // catch
    }//next


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

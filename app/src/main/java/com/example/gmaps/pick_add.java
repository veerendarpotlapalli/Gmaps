package com.example.gmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;


public class pick_add extends AppCompatActivity {
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pick_add);

        searchView = findViewById(R.id.search);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    getInput(s);
                    return false;
                }//onQueryTextSubmit

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }//onQueryTextChange
            });//setOnQueryTextListener
    }//oncreate

    public void getInput(String location){
        //String location = searchView.getQuery().toString().trim(); // here we are saving texted_location(query) in location
        Intent intent = new Intent(pick_add.this,MapsActivity.class);
        intent.putExtra("loc",location);
        startActivity(intent);
    }//getInput

}//main
package com.example.gmaps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class pick_dest extends AppCompatActivity {

    SearchView searchView;
    EditText edit2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_dest);

        try {
           // edit2.setCursorVisible(false);
            edit2 = findViewById(R.id.edit2);
            String pickadd = getIntent().getStringExtra("picks");
            edit2.setText(pickadd);
        }catch (Exception e){
            e.printStackTrace();
        }//catch

        searchView = findViewById(R.id.destination);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String dest = searchView.getQuery().toString();
                String pick = edit2.getText().toString();

                Intent intent = new Intent(pick_dest.this,MapsActivity.class);
                intent.putExtra("sai",dest);
                intent.putExtra("vamshi",pick);
                startActivity(intent);
                return false;
            }//onQueryTextSubmit

            @Override
            public boolean onQueryTextChange(String sv) {
                return false;
            }//onQueryTextChange
        });//setOnQueryTextListener
    }//oncreate
}//main
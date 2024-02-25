package com.example.strawfallescapebobadash.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.strawfallescapebobadash.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class fragment_google_map extends Fragment {

    private GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_map, container, false);
        SupportMapFragment supportMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        supportMapFragment.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
        });

        return view;
    }

    public void setMapLocation(double lat, double lon, String name){
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lon)).title(name));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lon))      // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }


}
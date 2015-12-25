package com.example.yzhuo.jogtrip;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    LatLngBounds.Builder builder;
    LatLngBounds bounds;
    LatLng currentLatLng;
    LatLng first;
    Location myLocation;
    LocationManager locationManager;
    LocationListener locationListener;
    Polyline line;
    LatLng startLatLng;
    LatLng endLatLng;
    int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        MenuInflater menuInflater = getMenuInflater();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMyLocationEnabled(true);
        builder = new LatLngBounds.Builder();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            return;
        }
        String bestProvider = locationManager.getBestProvider(new Criteria(), true);
        Location myLocation = locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);
        //Location myLocation = mMap.getMyLocation();
        // Add a marker in Sydney and move the camera
        LatLng currentLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        //mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18.0f));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (start == 0) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Start"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
                    builder.include(latLng);
                    startLatLng = latLng;
                    first = latLng;
                    start = 1;
                } else {
                    start = 0;
                    mMap.addMarker(new MarkerOptions().position(latLng).title("End"));
                    builder.include(latLng).build();
                    bounds = builder.build();

                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));
                    line = googleMap.addPolyline(new PolylineOptions()
                            .add(startLatLng, latLng)
                            .width(5)
                            .color(Color.BLUE));

                    startLatLng = null;
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (startLatLng != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
                    builder.include(latLng).build();
                    line = googleMap.addPolyline(new PolylineOptions()
                            .add(startLatLng, latLng)
                            .width(5)
                            .color(Color.BLUE));
                    startLatLng = latLng;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clearTrip) {
            mMap.clear();
            builder = new LatLngBounds.Builder();
            startLatLng = null;
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}

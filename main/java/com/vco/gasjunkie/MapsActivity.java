package com.vco.gasjunkie;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.webkit.PermissionRequest;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    static CardView card;
    private static ArrayList<DataModel> data;

    static double longitude = 151.1957362;
    static double latitude =  -33.8670522;
    private int PROXIMITY_RADIUS = 20000; //feet
    private String key = "AIzaSyDJA3hw4fZtNQo9hF_GjF-ZWGYS5rtCAvc";
    String keyword = "gas";
    String type = "gas_station";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        card = findViewById(R.id.card_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        data = new ArrayList<DataModel>();
//        for (int i = 0; i < myData.nameArray.length; i++){
//            data.add(new DataModel(
//                    mtData.nameArray[i],
//                    myData.versionArray[i],
//                    myData.is_[i]
//                    //myData.drawableArray[i]
//            ));
//        }
//
//        adapter = new CustomAdapter(data);
//        card.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            if(!mMap.isMyLocationEnabled())
                mMap.setMyLocationEnabled(true);
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location mylocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(mylocation == null){
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                mylocation = lm.getLastKnownLocation(provider);
            }

            if(mylocation != null){
                longitude = mylocation.getLongitude();
                latitude = mylocation.getLatitude();
            }
        }

        mapOptions();

        LatLng currentLocation = new LatLng(latitude, longitude);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_user_location);

        mMap.clear();
        String url = getUrl(latitude, longitude, type);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        GetNearbyPlacesData getnearbyPlacesData = new GetNearbyPlacesData();
        getnearbyPlacesData.execute(DataTransfer);
        Toast.makeText(MapsActivity.this, "Near By Gas Station", Toast.LENGTH_LONG).show();

        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Lat: " + latitude + " Long: " + longitude).icon(icon));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f), 1500, null);
    }

    public void mapOptions(){
       // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + key);
       // Toast.makeText(MapsActivity.this, "URL: " + googlePlacesUrl.toString(), Toast.LENGTH_LONG).show();
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
}

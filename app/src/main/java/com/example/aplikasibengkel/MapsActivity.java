package com.example.aplikasibengkel;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.aplikasibengkel.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int Request_Code = 101;
    private LocationServices LocationService;
    private double lat,lng;
    ImageButton Motor , Mobil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Mobil =findViewById(R.id.mobil);
        Motor =findViewById(R.id.motor);

        mFusedLocationClient =
                LocationService.getFusedLocationProviderClient(this.getApplicationContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Mobil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                StringBuilder stringBuilder= new StringBuilder
                        ("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location" + lat + "."+lng);
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=bengkel mobil");
                stringBuilder.append("&sensor=true");
                stringBuilder.append("keys="+getResources().getString(R.string.google_maps_key));

                String url = stringBuilder.toString();
                Object dataFetch[]=new Object[2];
                dataFetch[0]=mMap;
                dataFetch[1]=url;

                GetNearbyPlace getNearbyPlace=new GetNearbyPlace();
                getNearbyPlace.execute(dataFetch);

            }
        });
        Motor.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder= new StringBuilder
                        ("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                stringBuilder.append("location" + lat + "."+lng);
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=bengkel motor");
                stringBuilder.append("&sensor=true");
                stringBuilder.append("keys="+getResources().getString(R.string.google_maps_key));

                String url = stringBuilder.toString();
                Object dataFetch[]=new Object[2];
                dataFetch[0]=mMap;
                dataFetch[1]=url;

                GetNearbyPlace getNearbyPlace=new GetNearbyPlace();
                getNearbyPlace.execute(dataFetch);
            }
        });
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

        // Add a marker in Sydney and move the camera
       getCurrentLocation();
    }
    //get current location
    private void getCurrentLocation(){
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions
                    (this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_Code);
            return;


        }
        LocationRequest locationRequest= LocationRequest.create();
        locationRequest.setInterval(6000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(5000);
        //receive notif
        LocationCallback locationCallback= new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Toast.makeText(getApplicationContext(),"location Result is="+locationResult
                        ,Toast.LENGTH_LONG).show();
                if(locationResult==null){
                    Toast.makeText(getApplicationContext(),"location Result is NULL="+locationResult
                            ,Toast.LENGTH_LONG).show();

                    return;

                }
                for(Location location:locationResult.getLocations()){

                    if(location!=null){
                        Toast.makeText(getApplicationContext(),"Current location is ="+locationResult
                                ,Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates
                (locationRequest,locationCallback,null);

        Task<Location> task =mFusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {


                if(location!=null){
                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    LatLng latLng = new LatLng(lat , lng);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("current location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

                }
            }
        });

    }
    //permission buat akses lokasi
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (Request_Code){
            case Request_Code:
                if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }
        }
    }
}
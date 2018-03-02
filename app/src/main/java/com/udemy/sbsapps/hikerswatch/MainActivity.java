package com.udemy.sbsapps.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    final static String LAT = "Latitiude: ";
    final static String LONG = "Longitude: ";
    final static String ACC = "Accuracy: ";
    final static String ALT = "Altitude: ";

    TextView latitudeTextView, longitudeTextView, accuarcyTextView, altitudeTextView, addressTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeTextView = findViewById(R.id.latTextView);
        longitudeTextView = findViewById(R.id.longTextView);
        accuarcyTextView = findViewById(R.id.accTextView);
        altitudeTextView = findViewById(R.id.altTextView);
        addressTextView = findViewById(R.id.addressTextView);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation != null) {
                updateLocation(lastKnownLocation);
            }
        }
    }

    public void startListening() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    public void updateLocation(Location location) {
        Log.i("Location",location.toString());
        String message = LAT + Double.toString(location.getLatitude());
        latitudeTextView.setText(message);
        message = LONG + Double.toString(location.getLongitude());
        longitudeTextView.setText(message);
        message = ACC + Float.toString(location.getAccuracy());
        accuarcyTextView.setText(message);
        message = ALT + Double.toString(location.getAltitude());
        altitudeTextView.setText(message);

        String address = "Could not find an address :(";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if(listAddress !=null && listAddress.size() > 0) {
                address = "Address:\n";
                if(listAddress.get(0).getThoroughfare() !=null){
                    address += listAddress.get(0).getThoroughfare() + "\n";
                }if(listAddress.get(0).getLocality() !=null){
                    address += listAddress.get(0).getLocality() + " ";
                }if(listAddress.get(0).getPostalCode() !=null){
                    address += listAddress.get(0).getPostalCode() + " ";
                }if(listAddress.get(0).getAdminArea() !=null){
                    address += listAddress.get(0).getAdminArea();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addressTextView.setText(address);
    }
}

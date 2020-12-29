package com.animixer.weatherly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.snackbar.Snackbar;

public class SplashActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private ConstraintLayout mSplashLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mSplashLayout = findViewById(R.id.splash_layout);

        if (ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        }
        else {
            checkGPSStatus();
        }


    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(SplashActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        checkGPSStatus();
                    }
                }else{
                    Snackbar sbar = Snackbar.make(mSplashLayout,"Permission denied", Snackbar.LENGTH_INDEFINITE).setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });

                    sbar.setActionTextColor(ContextCompat.getColor(SplashActivity.this, R.color.DayStart));

                    sbar.show();
                }
                return;
            }
        }
    }


    private void checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        int num = 0;
        boolean network_enabled = false;
        if ( locationManager == null ) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex){}
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex){}

        if ( !gps_enabled && !network_enabled ){

            Snackbar sbar = Snackbar.make(mSplashLayout,"Please turn on location", Snackbar.LENGTH_INDEFINITE).setAction("TURN ON", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            sbar.setActionTextColor(ContextCompat.getColor(SplashActivity.this, R.color.DayStart));

            sbar.show();
        }
        else if (gps_enabled && !network_enabled){
            Snackbar sbar = Snackbar.make(mSplashLayout,"Please turn on internet", Snackbar.LENGTH_INDEFINITE).setAction("TURN ON", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
            });

            sbar.setActionTextColor(ContextCompat.getColor(SplashActivity.this, R.color.DayStart));

            sbar.show();
        }
        else if (!gps_enabled && network_enabled){
            Snackbar sbar = Snackbar.make(mSplashLayout,"Please turn on location", Snackbar.LENGTH_INDEFINITE).setAction("TURN ON", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            sbar.setActionTextColor(ContextCompat.getColor(SplashActivity.this, R.color.DayStart));

            sbar.show();
        }
        else {

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                Intent MainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(MainIntent);
                finish();
            }
            else {
                Snackbar sbar = Snackbar.make(mSplashLayout,"Please turn on internet", Snackbar.LENGTH_INDEFINITE).setAction("TURN ON", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                });

                sbar.setActionTextColor(ContextCompat.getColor(SplashActivity.this, R.color.DayStart));

                sbar.show();
            }


        }
    }
}
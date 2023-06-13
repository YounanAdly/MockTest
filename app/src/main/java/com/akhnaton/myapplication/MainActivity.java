package com.akhnaton.myapplication;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.inforoeste.mocklocationdetector.MockLocationDetector;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView mLastUpdateTimeTextView;
    private TextView mIsMockTextView;
    private TextView mAreMockLocationAppsPresentTextView;
    private TextView mIsMockLocationsOnTextView;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLatitudeTextView = (TextView) findViewById(R.id.txt_location_latitude);
        mLongitudeTextView = (TextView) findViewById(R.id.txt_location_longitude);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.txt_location_last_update_time);
        mIsMockTextView = (TextView) findViewById(R.id.txt_is_mock_text);
        mAreMockLocationAppsPresentTextView = (TextView) findViewById(R.id.txt_are_mock_location_apps_present);
        mIsMockLocationsOnTextView = (TextView) findViewById(R.id.txt_is_allow_mock_locations_on);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText(mLastUpdateTime);
        boolean isMock = MockLocationDetector.isLocationFromMockProvider(this, mCurrentLocation);
        mIsMockTextView.setText(String.valueOf(isMock));
        if (isMock) {
            mIsMockTextView.setTextColor(ContextCompat.getColor(this, R.color.purple_200));
        } else {
            mIsMockTextView.setTextColor(ContextCompat.getColor(this, R.color.purple_700));
        }
        boolean mockLocationAppsPresent = MockLocationDetector.checkForAllowMockLocationsApps(this);
        mAreMockLocationAppsPresentTextView.setText(String.valueOf(mockLocationAppsPresent));
        if (mockLocationAppsPresent) {
            mAreMockLocationAppsPresentTextView.setTextColor(ContextCompat.getColor(this, R.color.purple_200));
        } else {
            mAreMockLocationAppsPresentTextView.setTextColor(ContextCompat.getColor(this, R.color.purple_700));
        }
        boolean isAllowMockLocationsON = MockLocationDetector.isAllowMockLocationsOn(this);
        mIsMockLocationsOnTextView.setText(String.valueOf(isAllowMockLocationsON));
        if (isAllowMockLocationsON) {
            mIsMockLocationsOnTextView.setTextColor(ContextCompat.getColor(this, R.color.purple_200));
        } else {
            mIsMockLocationsOnTextView.setTextColor(ContextCompat.getColor(this, R.color.purple_700));
        }
    }
}

package com.example.helper1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private GoogleMap map;
    private SensorManager mSensorManager;
    private boolean mCompassEnabled;
    public static Double map_x;
    public static Double map_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        startLocationService();
    }


    @Override
    public void onResume(){
        super.onResume();

        //내 위치 자동 표시 enable
        map.setMyLocationEnabled(true);
        if(mCompassEnabled){
            mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        map.setMyLocationEnabled(false);
        if(mCompassEnabled){
            mSensorManager.unregisterListener(mListener);
        }
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

    private void startLocationService(){
        //위치 관리자 객체 참조
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //리스너 객체 생성
        GPSListener gpsListener = new GPSListener();
        long minTime=10000;
        float minDistance=0;

        manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                gpsListener
        );

    }

    private void showCurrentLocation(Double latitude, Double longitude){
        LatLng curPoint = new LatLng(latitude, longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
    private final SensorEventListener mListener = new SensorEventListener() {
        private int iOrientation = -1;
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(iOrientation<0){
                iOrientation = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    //위치 정보에 필요한 GPSListener
    private class GPSListener implements LocationListener {
        //위치 정보가 확인되었을때 호출되는 메소드
        public void onLocationChanged(Location location){
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            map_x = latitude;
            map_y = longitude;

            String msg="Latitude: " +latitude + "\nLongitude:"+longitude;
            Log.i("GPSLocationService", msg);

            //현재 위치의 지도를 보여주기 위해 정의한 메소드 호출
            showCurrentLocation(latitude, longitude);
        }
        public void onProviderDisabled(String provider){

        }
        public void onProviderEnabled(String provider){

        }
        public void onStatusChanged(String provider, int status, Bundle extras){

        }
    }
}

package bennett.dave.autocollect;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by David on 7/31/2016.
 */
public class Scanner extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private int location_time_interval = 2000; //2 seconds
    private int displacement = 5;
    private Controller controller;
    public static boolean running = false;
    private int NOTIFICATION_ID = 876;
    private NotificationManager notificationManager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(location_time_interval);
        mLocationRequest.setFastestInterval(5000); //5 second
        mLocationRequest.setSmallestDisplacement(displacement);
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        controller = new Controller(getApplicationContext());
        Intent notificationIntent = new Intent(this, Home.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Auto Loot");
        builder.setContentText("Running in Background");
        builder.setOngoing(true);
        builder.setContentIntent(contentIntent);
        notificationManager = (NotificationManager) this.getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        googleApiClient.connect();
        running = true;
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        running = false;
        notificationManager.cancel(NOTIFICATION_ID);
        controller.stop();
        googleApiClient.disconnect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
       // location.setLatitude(39.947117);
       // location.setLongitude(-75.292792);
        Log.d("bacoonia","Location changed");
        PokeController.setLocation(location);
        controller.addLocationToQueue(location);
    }
}

package com.murrays.aiderv1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class SensorForegroundService extends Service implements SensorEventListener {

    // Sensor variables
    private SensorManager sensorManager;
    private Sensor mGravity;
    private boolean isGravitySensorPresent;
    private AudioManager aManager;

    @Override
    public final void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        aManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            isGravitySensorPresent = true;

        } else {
            isGravitySensorPresent = false;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        // Insert sensor code here

                        while (true) {

                            logSensorStatus();

                        }


                    }
                }
        ).start();

        final String CHANNEL_ID = "Foreground Service ID";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_ID,
                    NotificationManager.IMPORTANCE_LOW
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        Notification.Builder notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText("Foreground service is still running...")
                    .setContentTitle("Service is enabled")
                    .setSmallIcon(R.drawable.ic_launcher_foreground);
        }

        startForeground(1001, notification.build());

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Sensor methods for change and accuracy
    @Override
    public final void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values[0] > 8.2 || sensorEvent.values[2] < -9.1) {
            aManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            Log.e("Service", "USER HAS FALLEN DOWN...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            aManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void logSensorStatus() {


        Log.e("Service", "Foreground Sensor Service is running...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
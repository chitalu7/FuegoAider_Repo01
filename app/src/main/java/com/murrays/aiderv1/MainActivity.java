package com.murrays.aiderv1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.murrays.aiderv1.Location.LocationService;

//Sensors
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


//Sensors
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;

//NEW LOCATION IMPORTS!
import android.Manifest;
import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;






public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private FirebaseAuth mFirebaseAuth;
    private String userFname;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;
    TextView txtAcceleration;  //, txtCurrent, txtPrev;
    //private String mFamilyID;

    DatabaseReference mDatabase;



    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent)
        {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            accelerationCurrentValue = Math.sqrt((x * x + y * y + z * z));
            double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;

            //txtCurrent.setText("Current = " + /*(int)*/accelerationCurrentValue);
            //txtPrev.setText(("Prev = " + /*(int)*/accelerationPreviousValue));

            //txtAcceleration.setText("Acceleration change = " + /*(int)*/changeInAcceleration);

            if(changeInAcceleration > 12) {
                //txtAcceleration.setBackgroundColor(Color.RED);
                sendNotificationToDB();

            }
            else if (changeInAcceleration > 5) {
                //txtAcceleration.setBackgroundColor(Color.BLUE);
            }
            else if(changeInAcceleration > 2) {
                //txtAcceleration.setBackgroundColor(Color.GREEN);
            }

           /* txtAcceleration.setText("Acceleration change = " + *//*(int)*//*changeInAcceleration);

            if(changeInAcceleration > 12) {
                txtAcceleration.setBackgroundColor(Color.RED);
            }
            else if (changeInAcceleration > 5) {
                txtAcceleration.setBackgroundColor(Color.BLUE);
            }
            else if(changeInAcceleration > 2) {
                txtAcceleration.setBackgroundColor(Color.GREEN);
            }*/
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //txtAcceleration = findViewById(R.id.txtAcceleration);
        //txtCurrent = findViewById(R.id.txtCurrent);
        //txtPrev = findViewById(R.id.txtPrev);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        startLocationService();
//        startLocationService();
       /* findViewById(R.id.buttonStartLocationUpdates).setOnClickListener(new View.OnClickListener() {
            //start
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                } else {
                    startLocationService();
                }
            }
        });

            findViewById(R.id.buttonStopLocationUpdates).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    stopLocationService();
                }
            });
*/
        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        } else {
            String mUserId = mFirebaseUser.getUid();

            mDatabase.child("users").child(mUserId).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userFname = (String)dataSnapshot.child("FirstName").getValue();
                    TextView welcome = (TextView) findViewById(R.id.welcome);
                    String welcomeMsg = String.format("Welcome %1$s", userFname);
                    welcome.setText(welcomeMsg);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Failed to read value
                    Log.w("Error", "Failed to read value.", error.toException());
                }
            });

          /*  final ListView listView = (ListView) findViewById(R.id.calendar_recent);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listview_layout, R.id.text1);
            listView.setAdapter(adapter);

            mDatabase.child("family").child("familyID").child("55").child("CalendarItems").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String dateKey = child.getKey();

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm", Locale.US);
                        Date thisEventDate = new Date();
                        if(dateKey != null){
                            try {
                                thisEventDate = simpleDateFormat.parse(dateKey);
                            }catch(Exception e){
                                Log.i("TAG", e.getMessage());
                            }
                        }
                        String niceDate = String.valueOf(thisEventDate);
                        String addme = "Date: "+niceDate + "  Event: "+ child.child("Description").getValue(String.class);
                        adapter.add(addme);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });*/

            final ListView listView2 = (ListView) findViewById(R.id.notifications_recent);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listview_layout, R.id.text1);
            listView2.setAdapter(adapter);

            mDatabase.child("family").child("familyID").child("55").child("Notifications").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int num = 0;
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        // Get only to 5 records from notifications node
                        if(num <6) {
                            String addme = child.child("NotificationText").getValue(String.class);
                            adapter.add(addme);
                            num++;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null) {
            for(ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if(com.murrays.aiderv1.Location.LocationService.class.getName().equals(service.service.getClassName())) {
                    if(service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if(!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplication(), com.murrays.aiderv1.Location.LocationService.class);
            intent.setAction(com.murrays.aiderv1.Location.Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service ", Toast.LENGTH_SHORT).show();
        }
    }
//    private void stopLocationService() {
//        if(isLocationServiceRunning()) {
//            Intent intent = new Intent(getApplicationContext(), com.murrays.aiderv1.Location.LocationService.class);
//            intent.setAction(com.murrays.aiderv1.Location.Constants.ACTION_STOP_LOCATION_SERVICE);
//            startService(intent);
//            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
//        }
//    }
    //move into activities
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void loadCalendarView() {
        startActivity(new Intent(MainActivity.this, Calendar.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_logout) {
//            mFirebaseAuth.signOut();
//            stopLocationService();
//            loadLogInView();
//        }

        if (id == R.id.action_calendar) {
            loadCalendarView();
        }
        return super.onOptionsItemSelected(item);
    }
    //2 methods to add -> onResume, onPause
    protected void onResume() {
        super.onResume();
        //the sensor manager will be using this function called sensorEventListener
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

    public void sendNotificationToDB() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");

        java.util.Calendar calendar = java.util.Calendar.getInstance();

        String logData = "";
        String notificationText = "A family member may have fallen. Please check in to see if they are okay";
        String calendaritemDate = simpleDateFormat.format(calendar.getTime());

        logData = LocationService.getlatitudeAndLongitude();

        String notID = mDatabase.child("family").child("familyID").child("55").child("Notifications").push().getKey();
        mDatabase.child("family").child("familyID").child("55").child("Notifications").child(notID).child("Time").setValue(calendaritemDate);
        mDatabase.child("family").child("familyID").child("55").child("Notifications").child(notID).child("NotificationText").setValue(notificationText);
        mDatabase.child("family").child("familyID").child("55").child("Notifications").child(notID).child("LogData").setValue(logData);

        listenForDBChanges();

    }

    private void listenForDBChanges(){
        mDatabase.child("family").child("familyID").child("55").child("Notifications").addChildEventListener(new ChildEventListener()
                //mDatabase.child("family").child("familyID").child("55").child("Notifications").
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                pushNotification(previousChildName);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //pushNotification();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void pushNotification(String notID){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setContentText("Fuego Aider")
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(false)
                .setContentText("ALERT!!! One of your family members may have fallen!");

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }





}
package com.murrays.aiderv1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
    private static boolean activityVisible = true;



    DatabaseReference mDatabase;


    // Initiatialize  sensor event listener and then lsiten for changes
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

        // This code is commneted because tis was not trigerring with consistency. it does work sometimes.
            if(changeInAcceleration >12) {
               // sendNotificationToDB();

            }


        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };


    // TRigger all the code we want to run in the onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton sensorTrigger = (ImageButton)  findViewById(R.id.sensorButton);



        // Initialize Firebase Auth and Database Reference, as well as sensormanager
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);






        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        } else {
            // Check if permission has been requested to share location
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
            // use a database listener to get user's name
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
            // Add a listener to the sensor simulation  trigger button
            sensorTrigger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runSensorTriggerEvent();

                }
            });
            // Go and populate the view with notifications
            populateListView();

            // Only run the listener if we are on this activity currently

        }
    }

    private void populateListView(){

            // initialize listview object on main view

            final ListView listView2 = (ListView) findViewById(R.id.notifications_recent);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listview_layout, R.id.text1);


            listView2.setAdapter(adapter);

            // Use a database listener to get the data
            mDatabase.child("family").child("familyID").child("55").child("Notifications").limitToLast(3).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        String addme = child.child("NotificationText").getValue(String.class);
                        adapter.add(addme);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            // check if Main acitivity is in the foreground before executing this code, I dont want it to trigger when someone adds a clendar item

            if(isActivityVisible()){

                // Add an onclick listener to the listview
                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       // create a database listener to populate the click link with the coordinates from the fall notification to go to google amps
                        mDatabase.child("family").child("familyID").child("55").child("Notifications").
                                limitToLast(3).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                for (DataSnapshot child2 : dataSnapshot2.getChildren()) {
                                    if(child2.child("NotificationText").getValue(String.class).equals(listView2.getItemAtPosition(position)))
                                    {
                                        String locdata = child2.child("LogData").getValue(String.class);
                                        try {
                                            String[] coordinates = locdata.split("[,]", 0);
                                            String longitude = coordinates[0];
                                            String latitude = coordinates[1];
                                            Uri gmmIntentUri = Uri.parse("geo:" + longitude + "," + latitude);
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");
                                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(mapIntent);
                                            }
                                        }catch (Exception e){
                                            // This code will catch it if it's not a fall notification. This would be implemented differently in future iterations.
                                            Log.i("Error","This is not a fall notification, not going to maps");
                                        }
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        }

    /// Check if this is the current activity we are on

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    // Check if permisson has been given for location sharing
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
        // Check if the location service is  running

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
        private void runSensorTriggerEvent(){
        sendNotificationToDB();
    }

    // Start the location service
    private void startLocationService() {
        if(!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplication(), com.murrays.aiderv1.Location.LocationService.class);
            intent.setAction(com.murrays.aiderv1.Location.Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service ", Toast.LENGTH_SHORT).show();
        }
    }

    // Stop the location service

    private void stopLocationService() {
        if(isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), com.murrays.aiderv1.Location.LocationService.class);
            intent.setAction(com.murrays.aiderv1.Location.Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_SHORT).show();
        }
    }
    //Create menu inflater
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    // Menu method to go to login view
    private void loadLogInView() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    // Menu method to go calendar
    private void loadCalendarView() {
        startActivity(new Intent(MainActivity.this, Calendar.class));
    }
  // Code to tell the menu what to do
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
         //   stopLocationService();
            loadLogInView();
        }

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
        // Method to send a notification to DB
    public void sendNotificationToDB() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        String mUserId = mFirebaseUser.getUid();

        userFname = "Stephanie";


                String logData = "";
                String notificationText = "Family member "+userFname+" may have fallen. Please click here to see their location";
                String calendaritemDate = simpleDateFormat.format(calendar.getTime());

                logData = LocationService.getlatitudeAndLongitude();
                // Add the notificaton to DB
                String notID = mDatabase.child("family").child("familyID").child("55").child("Notifications").push().getKey();
                mDatabase.child("family").child("familyID").child("55").child("Notifications").child(notID).child("Time").setValue(calendaritemDate);
                mDatabase.child("family").child("familyID").child("55").child("Notifications").child(notID).child("NotificationText").setValue(notificationText);
                mDatabase.child("family").child("familyID").child("55").child("Notifications").child(notID).child("LogData").setValue(logData);

        Toast.makeText(this, "ALERT!!! One of your family members may have fallen!", Toast.LENGTH_SHORT).show();

        // this code is commented out as it itsn't working with all the other functions combined
      // listenForDBChanges();

       // Reset and make the listview again so that it doesn't duplicate
        final ListView listView3 = (ListView) findViewById(R.id.notifications_recent);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.listview_layout, R.id.text1);
        listView3.setAdapter(adapter2);

        mDatabase.child("family").child("familyID").child("55").child("Notifications").limitToLast(3).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //We MUST clear the data adapter before we use it again
                adapter2.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    String addme = child.child("NotificationText").getValue(String.class);
                    adapter2.add(addme);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });






    }

    // Method to listen for notification changes and send a push notification. Not being used right now

    private void listenForDBChanges(){
        mDatabase.child("family").child("familyID").child("55").child("Notifications").addChildEventListener(new ChildEventListener()
                //mDatabase.child("family").child("familyID").child("55").child("Notifications").
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                pushNotification(previousChildName);
               // finish();
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

    // Method to send a push notification
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void pushNotification(String notID){

      NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "n")
                .setContentText("Fuego Aider")
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(false)
                .setContentText("ALERT!!! One of your family members may have fallen!");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("n", "n", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }



}
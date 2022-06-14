package com.murrays.aiderv1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private String userFname;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;
    TextView txtAcceleration, txtCurrent, txtPrev;
    //private String mFamilyID;


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

            txtCurrent.setText("Current = " + /*(int)*/accelerationCurrentValue);
            txtPrev.setText(("Prev = " + /*(int)*/accelerationPreviousValue));
            txtAcceleration.setText("Acceleration change = " + /*(int)*/changeInAcceleration);

            if(changeInAcceleration > 14) {
                txtAcceleration.setBackgroundColor(Color.RED);
            }
            else if (changeInAcceleration > 5) {
                txtAcceleration.setBackgroundColor(Color.BLUE);
            }
            else if(changeInAcceleration > 2) {
                txtAcceleration.setBackgroundColor(Color.GREEN);
            }
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
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        txtAcceleration = findViewById(R.id.txtAcceleration);
        txtCurrent = findViewById(R.id.txtCurrent);
        txtPrev = findViewById(R.id.txtPrev);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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

            final ListView listView = (ListView) findViewById(R.id.calendar_recent);
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
            });

            final ListView listView2 = (ListView) findViewById(R.id.notifications_recent);
            final ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.listview_layout, R.id.text1);
            listView2.setAdapter(adapter2);

            mDatabase.child("family").child("familyID").child("55").child("Notifications").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String addme = child.child("NotificationText").getValue(String.class);
                        adapter2.add(addme);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

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
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
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

}
package com.murrays.aiderv1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Calendar extends AppCompatActivity{
    CalendarView calendar;
    private AppBarConfiguration appBarConfiguration;
   // TextView date_view;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private String userFname;
    private String mFamilyID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView addme = findViewById(R.id.addevent);
        addme.setText("Click a Day Below to Add an Event to that Day");
        // By ID we can use each component
        // which id is assign in xml file
        // use findViewById() to get the
        // CalendarView and TextView
        calendar = (CalendarView)
                findViewById(R.id.calendar);
      //  date_view = (TextView)
     //           findViewById(R.id.date_view);
      //  Date currDate = new Date();
       // date_view.setText(currDate.getDate() +"-"+(currDate.getMonth()+1)+"-"+currDate.getYear());
        // Add Listener in calendar
        calendar
                .setOnDateChangeListener(
                        new CalendarView
                                .OnDateChangeListener() {
                            @Override

                            // In this Listener have one method
                            // and in this method we will
                            // get the value of DAYS, MONTH, YEARS
                            public void onSelectedDayChange(
                                    @NonNull CalendarView view,
                                    int year,
                                    int month,
                                    int dayOfMonth)
                            {

                                // Store the value of date with
                                // format in String type Variable
                                // Add 1 in month because month
                                // index is start with 0
                                String Date
                                        = dayOfMonth + "-"
                                        + (month + 1) + "-" + year;

                                // set this date in TextView for Display
                              //  date_view.setText(Date);
                                String dateToPass = ""+dayOfMonth+(month+1)+year;
                                gotoNextPage(dateToPass);



                            }
                        });


        // Show user Latest Calendar items
        final ListView listView = (ListView) findViewById(R.id.calendar_recent_calview);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listview_layout, R.id.text1);
        listView.setAdapter(adapter);

        //   Log.i("Familyid: ", mFamilyID);

        mDatabase.child("family").child("familyID").child("55").child("CalendarItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String dateKey = child.getKey();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
                    Date thisEventDate = new Date();

                    try {
                        thisEventDate = simpleDateFormat.parse(dateKey);
                    }catch(Exception e){

                    }

                    String niceDate = String.valueOf(thisEventDate);
                    String addme = niceDate + "  Event: "+ child.child("Description").getValue(String.class);
                    adapter.add(addme);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    private void gotoNextPage(String date){
        Intent intent = new Intent(Calendar.this,CalendarActivity.class);
        intent.putExtra("date", date);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void loadHomeView(){
        startActivity(new Intent(Calendar.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }
        if (id == R.id.action_home) {
            loadHomeView();
        }



        return super.onOptionsItemSelected(item);
    }


}

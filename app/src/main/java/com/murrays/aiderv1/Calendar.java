package com.murrays.aiderv1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Calendar extends Activity {
    CalendarView calendar;
    TextView date_view;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private String userFname;
    private String mFamilyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // By ID we can use each component
        // which id is assign in xml file
        // use findViewById() to get the
        // CalendarView and TextView
        calendar = (CalendarView)
                findViewById(R.id.calendar);
        date_view = (TextView)
                findViewById(R.id.date_view);
        Date currDate = new Date();
        date_view.setText(currDate.getDate() + "-" + (currDate.getMonth() + 1) + "-" + currDate.getYear());
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
                                    int dayOfMonth) {

                                // Store the value of date with
                                // format in String type Variable
                                // Add 1 in month because month
                                // index is start with 0
                                String Date
                                        = dayOfMonth + "-"
                                        + (month + 1) + "-" + year;

                                // set this date in TextView for Display
                                date_view.setText(Date);
                                String day_WZeros = "";
                                String month_WZeros = "";
                                month++;
                                //add zero

                                if(month < 10 ){

                                    month_WZeros = "0"+month;
                                }
                                else{
                                    month_WZeros = month+"";
                                }

                                if(dayOfMonth<10){
                                    day_WZeros = "0"+dayOfMonth;

                                }
                                else{
                                    day_WZeros = dayOfMonth+"";
                                }
                                String dateToPass = ""+(day_WZeros)+(month_WZeros)+year;

                                gotoNextPage(dateToPass);



                            }
                        });


        // Show user Latest Calendar items
        final ListView listView = (ListView) findViewById(R.id.activity_calender_eventview);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(adapter);

        //   Log.i("Familyid: ", mFamilyID);

        mDatabase.child("family").child("familyID").child("55").child("CalendarItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String dateKey = child.getKey();
//                    String rDate= dateKey.substring(0,7);
//                    Date realDate = new Date(rDate);
                    String addme = "Date: "+dateKey + "  Event: "+ child.child("Description").getValue(String.class);
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

}
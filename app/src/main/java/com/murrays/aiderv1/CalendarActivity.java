package com.murrays.aiderv1;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.util.List;


public class CalendarActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    TextView date_from_Cal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_events_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //display the date
        date_from_Cal = (TextView)findViewById(R.id.day_view);

        // create the get Intent object
        Intent intent = getIntent();

        // receive the value by getStringExtra() method
        // and key must be same which is send by first activity
        //String current_date = intent.getStringExtra("date");


        //This code does not work
     /*   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy");
        try {
            Date thisEventDate = simpleDateFormat.parse(current_date);
            String str = thisEventDate.toString();
            date_from_Cal.setText(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        //SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");

        String current_date = intent.getStringExtra("date");
        Log.i("datePrint", current_date);
        //14062022
        //SimpleDateFormat formatterOut = new SimpleDateFormat("dd MM yyyy");

        //Date dd = new Date(current_date);
        //date_from_Cal.setText(current_date);
        //date_from_Cal.setText(dd.getDate() + "-" + (dd.getMonth() + 1) + "-" + dd.getYear());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        //Date thisEventDate = new Date();

        //SimpleDateFormat can be used for parsing and formatting. You just need two formats, one that parses the string and the other that returns the desired print out:

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");


        SimpleDateFormat fort = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        SimpleDateFormat formatout = new SimpleDateFormat("ddMMyyyy");


//        try{
//            //String date = simpleDateFormat.parse(current_date).toString();
//            String date = fort.parse(current_date).toString();
//            String final_date = formatout.parse(date).toString();
//            date_from_Cal.setText(final_date);
////            date_from_Cal.setText(formatterOut.format(date));
////            date_from_Cal.setText(thisEventDate);
//        }catch (ParseException e) {
//            e.printStackTrace();
//        }

//        try {
//            //thisEventDate = simpleDateFormat.parse(current_date);
//            Date datet = fmtOut.parse(current_date);
//            String dateyy = datet.toString();
//            //String date_final = fmtOut.format(datet).toString();
//              date_from_Cal.setText(dateyy);
//            } catch (ParseException e) {
//                e.printStackTrace();
//           }

        try{
            String date = simpleDateFormat.parse(current_date).toString();
            date_from_Cal.setText(date);
////            date_from_Cal.setText(formatterOut.format(date));
////            date_from_Cal.setText(thisEventDate);
        }catch (ParseException e) {
            e.printStackTrace();
        }




        //date_from_Cal.setText(current_date);

        // Show user Latest Calendar items
        final ListView listView = (ListView) findViewById(R.id.select_dayview); //change later when you mergre
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);//change when you merge with step
        listView.setAdapter(adapter);





        /*String[] finalCurrent_dateA = current_dateA;
        String finalcurrentDate = String.join(",",finalCurrent_dateA);*/
        mDatabase.child("family").child("familyID").child("55").child("CalendarItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String dateKey = child.getKey();
                    //Only get the first 5-6 characters b/c they are the date

                    String rDate= dateKey.substring(0,8);
                    Log.i("dateKey",rDate);
                    if(rDate.equals(current_date)){
                        //display the events for the day
                        String addme = "Date: " + current_date + "  Event: " + child.child("Description").getValue(String.class);
                        adapter.add(addme);
                    }

//                   Date realDate = new Date(rDate);
                    //you can get day and get month to compare, it may be easier than comparing the whole day
                    //real.getDate()
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
//                    Date thisEventDate = new Date();
//                    try {
//                        thisEventDate = simpleDateFormat.parse(child.getKey());
//                        String addme = "Date: " + thisEventDate + "  Event: " + child.child("Description").getValue(String.class);
//                        adapter.add(addme);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }



                    // display the string into textView
//                    if (realDate == dd) {
//                        String addme = "Date: " + realDate + "  Event: " + child.child("Description").getValue(String.class);
//                        adapter.add(addme);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gotoNextPage((String)listView.getItemAtPosition(position),current_date);
            }
        });



        ImageButton imageButton= findViewById(R.id.button2);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldIntent = getIntent();
                String current_date = intent.getStringExtra("date");
                Intent intent = new Intent(CalendarActivity.this, CalendarAddActivity.class);
                intent.putExtra("Date",current_date);
                startActivity(intent);

            }
        });


    }

    private void gotoNextPage(String item, String dt){
        Intent intent = new Intent(CalendarActivity.this,CalendarEditActivity.class);
        intent.putExtra("txt", item);
        intent.putExtra("date",dt);
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
        startActivity(new Intent(CalendarActivity.this, MainActivity.class));
    }
    private void loadCalendarView() {
        startActivity(new Intent(CalendarActivity.this, Calendar.class));
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
        if (id == R.id.action_calendar) {
            loadCalendarView();
        }


        return super.onOptionsItemSelected(item);
    }

}

//if the first digit is more than 1 add a zero at the begining
//if the second number is more than 1 add a
//String[] current_dateA = current_date.split("");

/*
if (current_dateA[0] != "1" && current_dateA[1] != "0"){
    //new array with one more length
    String[] fix = new String[current_dateA.length + 2];
//copy the old in the new array
    System.arraycopy(current_dateA, 0, fix, 0, current_dateA.length);
//add element to new array
    fix[0] = "0";
    fix[2] = "0"; //change to 1 if in wrong spot
    System.out.println(fix);
//optional: set old array to new array
    current_dateA = fix;
    System.out.println(current_dateA);
    //Only get the first 5 characters b/c they are the date

}
else if(current_dateA[0] != "1"){

    String[] fix = new String[current_dateA.length + 1];
    System.arraycopy(current_dateA, 0, fix, 0, current_dateA.length);
    fix[0] = "0";
    current_dateA = fix;

}
else if (current_dateA[1] != "1"){
    String[] fix = new String[current_dateA.length + 1];
    System.arraycopy(current_dateA, 0, fix, 0, current_dateA.length);
    fix[1] = "0";
    current_dateA = fix;

}*/

package com.murrays.aiderv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.murrays.aiderv1.Location.LocationService;

import java.util.Random;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CalendarAddActivity extends AppCompatActivity {
    protected EditText eventDate;
    protected EditText eventTime;
    protected EditText descriptionEvent;
    protected EditText notesEvent;
    protected Button addEventButton;
    private String mFamilyID;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_add_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventDate = (EditText) findViewById(R.id.event_date);
        eventTime = (EditText) findViewById(R.id.event_time);
        descriptionEvent = (EditText) findViewById(R.id.descript_textbox);
        notesEvent = (EditText) findViewById(R.id.notes_textbox);
        ImageButton addEventButton = (ImageButton) findViewById(R.id.add_event_button);

        //Get date passed from previous page and populates the form
        Intent intent = getIntent();
        String current_date = intent.getStringExtra("Date");
        eventDate.setText(current_date.substring(0,8));


        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = eventDate.getText().toString();
                String time = eventTime.getText().toString();
                String description = descriptionEvent.getText().toString();
                String notes = notesEvent.getText().toString();

                //adds a random time to the date to give it a unique key in db
                // does not prevent duplicates
                //maybe add  an if statement to compare previous times

                final Random random = new Random();
                final int millisInDay = 24 * 60 * 60 * 1000;
                Time ranTime = new Time(random.nextInt(millisInDay));
                String TimeString = ranTime.toString();
                String newTime = TimeString.replace(":", "");

                // add time to the end
                String add_dt = date + newTime;
                String newTime_dbKey = add_dt.substring(0, 12);
                Log.i("checkDateTime", newTime_dbKey);




                String mUserId = mFirebaseUser.getUid();
                mDatabase.child("users").child(mUserId).child("profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFamilyID = dataSnapshot.child("FamilyID").getValue(String.class);


                        Log.i("Familyid", mFamilyID);
                        mDatabase.child("family").child("familyID").child(mFamilyID).child("CalendarItems").child(newTime_dbKey).child("Description").setValue(description);
                        mDatabase.child("family").child("familyID").child(mFamilyID).child("CalendarItems").child(newTime_dbKey).child("Notes").setValue(notes);

                        addNotificationCalendar(newTime_dbKey,description);


                        // Create the object of
                        // AlertDialog Builder class
                        AlertDialog.Builder builder
                                = new AlertDialog
                                .Builder(CalendarAddActivity.this);

                        // Set the message show for the Alert time
                        builder.setMessage(" Date:" + "  " + newTime_dbKey + " Description:" + "  " + description + " Notes:" + "  " + notes);

                        // Set Alert Title
                        builder.setTitle("Event Added");

                        // Set Cancelable false
                        // for when the user clicks on the outside
                        // the Dialog Box then it will remain show
                        //builder.setCancelable(false);

                        // Set the positive button with yes name
                        // OnClickListener method is use of
                        // DialogInterface interface.

                        builder
                                .setPositiveButton(
                                        "Ok",
                                        new DialogInterface
                                                .OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {

                                                // When the user click yes button
                                                // then app will close
                                                dialog.cancel();
                                            }
                                        });


                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();

                        // Show the Alert Dialog box
                        alertDialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    };

        // Add a notifcation to DB when calendat event is added
    private void addNotificationCalendar(String date, String descr){



        String mUserId = mFirebaseUser.getUid();
        String logData = "Event added by user: "+mUserId;

        mDatabase.child("users").child(mUserId).child("profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userFname = "";
                userFname = (String) dataSnapshot.child("FirstName").getValue();
                String notificationText = "Family member "+userFname+" has added '" + descr +"' to the calendar on "+date ;

                String notID = mDatabase.child("family").child("familyID").child("55").child("Notifications").push().getKey();
                mDatabase.child("family").child("familyID").child("55").child("Notifications").child(notID).child("Time").setValue(date);
                mDatabase.child("family").child("familyID").child("55").child("Notifications").child(notID).child("NotificationText").setValue(notificationText);
                mDatabase.child("family").child("familyID").child("55").child("Notifications").child(notID).child("LogData").setValue(logData);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    // menu method to go home
    private void loadHomeView(){
        startActivity(new Intent(CalendarAddActivity.this, MainActivity.class));
    }
    // Menu method to go calendar
    private void loadCalendarView() {
        startActivity(new Intent(CalendarAddActivity.this, Calendar.class));
    }
    // Code to tell the menu what to do
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


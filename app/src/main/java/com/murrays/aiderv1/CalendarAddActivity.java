package com.murrays.aiderv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        setContentView(R.layout.activity_calendar_add);
          mFirebaseAuth = FirebaseAuth.getInstance();
          mFirebaseUser = mFirebaseAuth.getCurrentUser();


        eventDate = (EditText) findViewById(R.id.event_date);
        eventTime = (EditText) findViewById(R.id.event_time);
        descriptionEvent = (EditText) findViewById(R.id.descript_textbox);
        notesEvent = (EditText) findViewById(R.id.notes_textbox);
        addEventButton = (Button) findViewById(R.id.add_event_button);

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
                //112520220619
                //062020220123
                //061820222232
                //Log.i("checkdescrp", description);
                //Log.i("checknotes", notes);


                String mUserId = mFirebaseUser.getUid();
                mDatabase.child("users").child(mUserId).child("profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFamilyID = dataSnapshot.child("FamilyID").getValue(String.class);

                           Log.i("Familyid", mFamilyID);
                        //mDatabase.child("family").child("familyID").child(mFamilyID).child("CalendarItems").child(newTime_dbKey).child("Description").setValue(description);
                        //mDatabase.child("family").child("familyID").child(mFamilyID).child("CalendarItems").child(newTime_dbKey).child("Notes").setValue(notes);

                        Log.i("checkFamilyid", mFamilyID);


                        //                try {
//                    Log.i("convert",convertTo24HoursFormat("12:00AM")); // 00:00
//                } catch (ParseException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            // Replace with KK:mma if you want 0-11 interval
//            private  final DateFormat TWELVE_TF = new SimpleDateFormat("hh:mma");
//            // Replace with kk:mm if you want 1-24 interval
//            private  final DateFormat TWENTY_FOUR_TF = new SimpleDateFormat("HH:mm");
//
//            public  String convertTo24HoursFormat(String twelveHourTime)
//                    throws ParseException {
//                return TWENTY_FOUR_TF.format(
//                        TWELVE_TF.parse(twelveHourTime));
//            }

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
}

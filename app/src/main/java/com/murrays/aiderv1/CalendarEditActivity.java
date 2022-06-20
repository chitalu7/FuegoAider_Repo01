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

import java.util.Random;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CalendarEditActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_edit_main);
        showEvent();


    }
        public void showEvent(){

            EditText eventDate = (EditText) findViewById(R.id.event_date) ;
            EditText eventTime = (EditText) findViewById(R.id.event_time) ;
            EditText eventDesc = (EditText) findViewById(R.id.descript_textbox) ;
            EditText eventNoes = (EditText) findViewById(R.id.notes_textbox) ;
            Button editEvent = (Button) findViewById(R.id.add_event_button);

            mFirebaseAuth = FirebaseAuth.getInstance();
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            Intent intent = getIntent();

            String current_date = intent.getStringExtra("date");
            String txt= intent.getStringExtra("txt");
            String rTxt= txt.substring(23,txt.length());



            mDatabase.child("family").child("familyID").child("55").child("CalendarItems").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String dateKey = child.getKey();
                        //Only get the first 5-6 characters b/c they are the date

                        String rDate = dateKey.substring(0, 8);
                        Log.i("MydateKey", rDate);
                        if (rDate.equals(current_date)) {

                            //display the events for the day
                            //  Log.i("Child node",(String)child.child("Description").getValue());
                            //  String addme = "Date: " + current_date + "  Event: " + child.child("Description").getValue(String.class);
                            if (((String) child.child("Description").getValue()).equals(rTxt)) {
                                String dd = dateKey.substring(0, 8);
                                String tt = dateKey.substring(8, 12);
                                eventDate.setText(dd);
                                eventTime.setText(tt);
                                eventDesc.setText((String) child.child("Description").getValue());
                                eventNoes.setText((String) child.child("Notes").getValue());
                                // eventDate.setTag(1,dateKey);

                                //    Log.i("Foundit","Nice.");


                            }
                        }
                        editEvent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditEvent(dateKey);
                            }

                        });

                    }

                    //  String key = (String)eventDate.getTag(1);

                    //  Log.i("Keykey",key);

                }



                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        public void EditEvent(String key){
            EditText eventDate = (EditText) findViewById(R.id.event_date) ;
            EditText eventTime = (EditText) findViewById(R.id.event_time) ;
            EditText eventDesc = (EditText) findViewById(R.id.descript_textbox) ;
            EditText eventNoes = (EditText) findViewById(R.id.notes_textbox) ;


            String theeventDate = eventDate.getText().toString();
            String theeventTime = eventTime.getText().toString();
            String theeventDesc = eventDesc.getText().toString();
            String theeventNotes = eventNoes.getText().toString();
            Log.i("key",key);
            Log.i("theeventdec", theeventDesc);

          // String newKey = theeventDate+theeventTime;


            mDatabase.child("family").child("familyID").child("55").child("CalendarItems").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                //   mDatabase.child("family").child("familyID").child("55").child("CalendarItems").child(key).setValue(newKey);
                    mDatabase.child("family").child("familyID").child("55").child("CalendarItems").child(key).child("Description").setValue(theeventDesc);
                    mDatabase.child("family").child("familyID").child("55").child("CalendarItems").child(key).child("Notes").setValue(theeventNotes);
                    AlertDialog.Builder builder
                            = new AlertDialog
                            .Builder(CalendarEditActivity.this);

                    // Set the message show for the Alert time
                    builder.setMessage("Date:" + "  " + key + " Description:" + "  " + theeventDesc + " Notes:" + "  " + theeventNotes);

                    // Set Alert Title
                    builder.setTitle("Event Edited");

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
                public void onCancelled(DatabaseError databaseError) {
                        Log.i("Database error", String.valueOf(databaseError));
                }
            });
        //    showEvent();

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
        startActivity(new Intent(CalendarEditActivity.this, MainActivity.class));
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

package com.murrays.aiderv1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
//import com.murrays.aiderv1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
   // private ActivityMainBinding binding;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private String userFname;
    private String mFamilyID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*
        Button calendarButton = (Button) findViewById(R.id.calendarButton);
        Button meetupButton = (Button) findViewById(R.id.meetupButton);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent);
            }
        });
        meetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MeetupActivity.class);
                startActivity(intent);
            }
        });
*/
        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();




            //after the database is connected to the app and the reference is
        // established the app retrieves the current user that is loged in

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        } else {
            mUserId = mFirebaseUser.getUid();

            // the current mUserId is then used to access the specific node within
            // the firebase database to retrieve a snapshot of the data
            // we can specify what data we want, since this is the welcome page
            //we want the persons first name to welcome them, their info
            //and eventually  their calendar and notifications that will be clickable
            // in later development

            mDatabase.child("users").child(mUserId).child("profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    userFname = (String)dataSnapshot.child("FirstName").getValue();
                    TextView welcome= (TextView) findViewById(R.id.welcome);
                    welcome.setText("Welcome " + userFname);
                    mFamilyID =  dataSnapshot.child("FamilyID").getValue(String.class);

                  //  TextView userinfo = (TextView) findViewById(R.id.userinfo);

                  //  userinfo.setText( getString(R.string.new_line)+ "Here is some of the data we currently have for you: "+ getString(R.string.new_line)+"Name: " + userFname + " "+ (String)dataSnapshot.child("LastName").getValue() + getString(R.string.new_line)+"Address: " +(String)dataSnapshot.child("Address1").getValue()+", "+ (String)dataSnapshot.child("City").getValue());

                    //the code below is meant for seeding dummy data to the database before we have those fragments working
                    //



                    //Seed some data for other nodes

                    // Family Data Seeding - only done once for prototype purposes

                  //  mDatabase.child("family").child("familyID").setValue(mFamilyID);

                    // Calender Data Seeding - each family has calendar - Datetime acts as key. must be unique - only done once for prototype purposes

                  //Calendar calendar = Calendar.getInstance();
                //   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
                  //  String calendaritemDate = simpleDateFormat.format(calendar.getTime()).toString();
                  //  String calendaritemDate = "290520221700";


                 //   String calendaritemDescr = "Daughter Coming Over to Grandma's House";
                 //   String calendaritemNotes = "I will bring cake";


                  //  Log.d("Date", calendaritemDate);
                  //  mDatabase.child("family").child("familyID").child(mFamilyID).child("CalendarItems").child(calendaritemDate).child("Description").setValue(calendaritemDescr);
                 //  mDatabase.child("family").child("familyID").child(mFamilyID).child("CalendarItems").child(calendaritemDate).child("Notes").setValue(calendaritemNotes);

                //Family Notifications- Seed some data for notifications, which will come under the banner of family groups - only done once for prototype purposes

                /*   Calendar calendar = Calendar.getInstance();
                       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmm");
                      String calendaritemDate = simpleDateFormat.format(calendar.getTime()).toString();
                      String logdata = "We can put any data coming from sensor relating to event that we want to log here.";


                  String not1 ="Family member Emily has fallen. Please check in to see if they are ok";
                  String not2 ="Family member Richard has added something to the family calendar";
                    String notID = mDatabase.child("family").child("familyID").child(mFamilyID).child("Notifications").push().getKey();
                    mDatabase.child("family").child("familyID").child(mFamilyID).child("Notifications").child(notID).child("Time").setValue(calendaritemDate);
                    mDatabase.child("family").child("familyID").child(mFamilyID).child("Notifications").child(notID).child("NotificationText").setValue(not2);
                    mDatabase.child("family").child("familyID").child(mFamilyID).child("Notifications").child(notID).child("LogData").setValue(logdata);


*/

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("Error", "Failed to read value.", error.toException());
                }
            });



            // Show user Latest Calendar items
            final ListView listView = (ListView) findViewById(R.id.calendar_recent);
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
                        String addme = "Date: "+niceDate + "  Event: "+ child.child("Description").getValue(String.class);
                        adapter.add(addme);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        // Show user the notification events

            final ListView listView2 = (ListView) findViewById(R.id.notifications_recent);
            final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.listview_layout, R.id.text1);
            listView2.setAdapter(adapter2);

            mDatabase.child("family").child("familyID").child("55").child("Notifications").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        String addme = child.child("NotificationText").getValue(String.class);
                        adapter2.add(addme);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        }

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
    private void loadCalendarView() {
        startActivity(new Intent(MainActivity.this, Calendar.class));
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

        if (id == R.id.action_calendar) {
            loadCalendarView();
        }

        return super.onOptionsItemSelected(item);
    }

  //  @Override
  //  public boolean onSupportNavigateUp() {
      //  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
    //   return NavigationUI.navigateUp(navController, appBarConfiguration)
     //           || super.onSupportNavigateUp();
   // }
}
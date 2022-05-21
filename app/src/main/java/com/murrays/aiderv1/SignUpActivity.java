package com.murrays.aiderv1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    protected EditText passwordEditText;
    protected EditText emailEditText;
    protected Button signUpButton;
    private FirebaseAuth mFirebaseAuth;

    protected EditText firstName;
    protected EditText lastName;
    protected EditText phoneEditText;
    protected EditText address1EditText;
    protected EditText address2EditText;
    protected EditText cityEditText;
    protected EditText stateEditText;
    protected EditText zipEditText;
    protected String family_id = "55";

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        firstName = (EditText)findViewById(R.id.firstName);
        lastName = (EditText)findViewById(R.id.lastName);
        phoneEditText = (EditText)findViewById(R.id.phone);
        address1EditText = (EditText)findViewById(R.id.address1);
        address2EditText = (EditText)findViewById(R.id.address2);
        cityEditText = (EditText)findViewById(R.id.city);
        stateEditText = (EditText)findViewById(R.id.state);
        zipEditText = (EditText)findViewById(R.id.zip);

        passwordEditText = (EditText)findViewById(R.id.passwordField);
        emailEditText = (EditText)findViewById(R.id.emailField);
        signUpButton = (Button)findViewById(R.id.signupButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String fname = firstName.getText().toString();
                String lname = lastName.getText().toString();
                String phone = phoneEditText.getText().toString();
                String address1 = address1EditText.getText().toString();
                String address2 = address2EditText.getText().toString();
                String city = cityEditText.getText().toString();
                String zip = zipEditText.getText().toString();
                String state = stateEditText.getText().toString();
                String[] UserData = {"FirstName", "LastName", "Phone","Address1","Address2","State","City","Zip", "FamilyID"};


                mDatabase = FirebaseDatabase.getInstance().getReference();

                password = password.trim();
                email = email.trim();

                if (fname.isEmpty() || lname.isEmpty()|| password.isEmpty() || email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseUser user = task.getResult().getUser();
                                        String mUserId = user.getUid();
                                        DatabaseReference mProfile = mDatabase.child("users").child(mUserId).child("profile");
                                        String[] userDataValues = {fname, lname, phone, address1, address2, state, city, zip, family_id};
                                        for(int i = 0; i< userDataValues.length;i++) {
                                            mProfile.child(UserData[i]).setValue(userDataValues[i]);
                                            Log.d(UserData[i]+" ",userDataValues[i]);
                                        }





                                        // User newuser = new User(fname,lname);
                                   //     User newuser = new User("Steph","Murray");
                                  //      Log.d("Test","hi");
                                    //    Log.d("Hello", newuser.FirstName);
                                      //  mDatabase.child("users").child(mUserId).child("profile").push().child("uname").setValue(newuser);


                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setTitle(R.string.login_error_title)
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });


                }




            }
        });
    }

}
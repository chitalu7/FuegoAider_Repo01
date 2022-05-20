package com.murrays.aiderv1;

import android.content.Intent;
import android.os.Bundle;
//import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
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
    private DatabaseReference mDatabase;
    /*
    To Do: Update the layout to include relevant data that we need.
    I used for loops here that will correspond to the text fields. The arrays
    "UserData" and "userDataValues" should be the same length and should include
    1) the field name in UserData and 2) the initialized value in userDataValues
    This way, when we update the layout to contain the fields that we need, when a
    new user signs up, their information will be stored in the nosql database.
    */
    String[] UserData = {"FirstName", "LastName", "Phone"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phoneEditText = findViewById(R.id.phoneField);

        passwordEditText = findViewById(R.id.passwordField);
        emailEditText = findViewById(R.id.emailField);
        signUpButton = findViewById(R.id.signupButton);

        signUpButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String fname = firstName.getText().toString();
            String lname = lastName.getText().toString();
            String uphone = phoneEditText.getText().toString();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            password = password.trim();
            email = email.trim();

           /* if (fname.isEmpty() || lname.isEmpty()|| password.isEmpty() || email.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(R.string.signup_error_message)
                        .setTitle(R.string.signup_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {*/
                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, task -> {
                            if (task.isSuccessful()) {

                                FirebaseUser user = task.getResult().getUser();
                                String mUserId = user.getUid();

                                DatabaseReference mProfile = mDatabase.child("users").child(mUserId).child("profile");
                                String[] userDataValues = {fname, lname, uphone};
                                for(int i = 0; i< userDataValues.length;i++) {
                                    mProfile.child(UserData[i]).setValue(userDataValues[i]);
                                }

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
                        });
            });
    }
}

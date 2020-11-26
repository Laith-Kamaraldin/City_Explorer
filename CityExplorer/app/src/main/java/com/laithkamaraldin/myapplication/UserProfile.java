package com.laithkamaraldin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
//A tutorial was followed and modified when making this class on youtube more details
//found in the readme
public class UserProfile extends AppCompatActivity {
    TextInputLayout fullName, email, phoneNo, password;
    TextView fullNameLabel, usernameLabel;
    String _USERNAME, _NAME, _EMAIL, _PHONENO, _PASSWORD;
    DatabaseReference reference;
    //setting up variables


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        reference = FirebaseDatabase.getInstance().getReference("users");
        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_no_profile);
        password = findViewById(R.id.password_profile);
        fullNameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username_field);
       //hooking XML elements into the page

        //ShowAllData
        showAllUserData();
    }

    private void showAllUserData() {

        Intent applicationIntent = getIntent();
        String username = applicationIntent.getStringExtra("username");
        //setting up intent to get data when activity is started
        Query checkUser = reference.orderByChild("username").equalTo(username);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    _NAME = dataSnapshot.child(username).child("name").getValue(String.class);
                    _USERNAME = dataSnapshot.child(username).child("username").getValue(String.class);
                    _PHONENO = dataSnapshot.child(username).child("phoneNo").getValue(String.class);
                    _EMAIL = dataSnapshot.child(username).child("email").getValue(String.class);
                    //updating variables to data found in database
                    fullNameLabel.setText(_NAME);
                    usernameLabel.setText(_USERNAME);
                    fullName.getEditText().setText(_NAME);
                    email.getEditText().setText(_EMAIL);
                    phoneNo.getEditText().setText(_PHONENO);
                    password.getEditText().setText(_PASSWORD);
                    //displaying user data inside the the XML text fields
                    } else {
                        password.setError("Wrong Password");
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    public void update(View view) {

        if (isNameChanged() || isPasswordChanged()) {
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(this, "Data is same and can not be updated", Toast.LENGTH_LONG).show();

    }

    private boolean isPasswordChanged() {
        if (!_PASSWORD.equals(password.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("password").setValue(password.getEditText().getText().toString());
            _PASSWORD = password.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isNameChanged() {

        if (!_NAME.equals(fullName.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("name").setValue(fullName.getEditText().getText().toString());
            _NAME = fullName.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }

    }

}

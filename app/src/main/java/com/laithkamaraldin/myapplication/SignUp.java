package com.laithkamaraldin.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//part of this code has been adopted from online sources more details in the readme
public class SignUp extends AppCompatActivity {


    //Variables
    TextInputLayout registeredName, registeredUsername, registeredEmail, registeredPhone, registeredPassword;
    Button regBtn, regToLoginBtn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This Line will hide the status bar from the screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        //Firebase Instantiation
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        //Hooks to all xml elements in activity_sign_up.xml
        registeredName = findViewById(R.id.reg_name);
        registeredUsername = findViewById(R.id.reg_username);
        registeredEmail = findViewById(R.id.reg_email);
        registeredPhone = findViewById(R.id.reg_phoneNo);
        registeredPassword = findViewById(R.id.reg_password);

    }

    private Boolean validateName() {
        String val = registeredName.getEditText().getText().toString();

        if (val.isEmpty()) {
            registeredName.setError("Field cannot be empty");
            return false;
        } else {
            registeredName.setError(null);
            registeredName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUsername() {
        String val = registeredUsername.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if (val.isEmpty()) {
            registeredUsername.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 15) {
            registeredUsername.setError("Username too long");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            registeredUsername.setError("White Spaces are not allowed");
            return false;
        } else {
            registeredUsername.setError(null);
            registeredUsername.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = registeredEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            registeredEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            registeredEmail.setError("Invalid email address");
            return false;
        } else {
            registeredEmail.setError(null);
            registeredEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhoneNo() {
        String val = registeredPhone.getEditText().getText().toString();

        if (val.isEmpty()) {
            registeredPhone.setError("Field cannot be empty");
            return false;
        } else {
            registeredPhone.setError(null);
            registeredPhone.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        //password validation function
        String val = registeredPassword.getEditText().getText().toString();
        //Getting the registered password value
        String passwordVal = "^" +
                "(?=.*[a-zA-Z])" +
                //can contain any letter in the alphabet
                "(?=.*[@#$%^&+=])" +
                // must at least contain one special type of character
                "(?=\\S+$)" +
                //Entered password can contain no white spaces
                ".{4,}" +
                //the minimum length for the password is four characters
                "$";

        if (val.isEmpty()) {
            registeredPassword.setError("Password Field cannot be left empty");
            return false;
            //checks if no data was entered and provides error
        } else if (!val.matches(passwordVal)) {
            registeredPassword.setError("Password entered is too weak. Try Again!");
            return false;
            //checks if password matches required syntax
        } else {
            registeredPassword.setError(null);
            registeredPassword.setErrorEnabled(false);
            //else if password meets requirements submit
            return true;
        }
    }

    //This function will execute when user click on Register Button
    public void registerUser(View view) {

        //Performing Validation by calling validation functions
        /*if(!validateName() | !validatePassword() | !validatePhoneNo() | !validateEmail() | !validateUsername()){
            return;
        }*/

        //Get all the values in String
        String name = registeredName.getEditText().getText().toString();
        String username = registeredUsername.getEditText().getText().toString();
        String email = registeredEmail.getEditText().getText().toString();
        String phoneNo = registeredPhone.getEditText().getText().toString();
        String password = registeredPassword.getEditText().getText().toString();

        Intent intent = new Intent(getApplicationContext(),VerifyPhoneNo.class);
        intent.putExtra("name",name);
        intent.putExtra("username",username);
        intent.putExtra("email",email);
        intent.putExtra("phoneNo",phoneNo);
        intent.putExtra("password",password);
        startActivity(intent);


    }
}

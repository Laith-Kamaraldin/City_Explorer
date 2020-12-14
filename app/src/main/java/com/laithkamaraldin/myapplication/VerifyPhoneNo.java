package com.laithkamaraldin.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneNo extends AppCompatActivity {

    //Variables for design
    String verificationCodeBySystem;
    Button verify_btn;
    EditText phoneNoEnteredByTheUser;
    ProgressBar progressBar;

    String name, username, email, phoneNo, password;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_no);

        verify_btn = findViewById(R.id.verify_btn);
        phoneNoEnteredByTheUser = findViewById(R.id.verification_code_entered_by_user);
        progressBar = findViewById(R.id.progress_bar);

        //Firebase Instantiation
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");

        progressBar.setVisibility(View.GONE);

        name = getIntent().getStringExtra("name");
        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");
        phoneNo = getIntent().getStringExtra("phoneNo");
        password = getIntent().getStringExtra("password");

        sendVerificationCodeToUser(phoneNo);

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String code = phoneNoEnteredByTheUser.getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    phoneNoEnteredByTheUser.setError("Wrong OTP...");
                    phoneNoEnteredByTheUser.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        });

    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+44" + phoneNo,        // formatting phone number for UK
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of time
                TaskExecutors.MAIN_THREAD,  // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneNo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);
        //calling the sign in function with the correct credentials
    }
    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        //getting the firebase verification instance
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneNo.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if verification is successful create the account
                        if (task.isSuccessful()) {
                            //Code below calls the helperClass and submits the code to the database under the child username.
                            UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneNo, password );
                            reference.child(username).setValue(helperClass);
                            Toast.makeText(VerifyPhoneNo.this, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show();
                            //Perform action to let user sign In passing details using intent
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(VerifyPhoneNo.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //else display error message
                        }

                    }
                });

    }
}

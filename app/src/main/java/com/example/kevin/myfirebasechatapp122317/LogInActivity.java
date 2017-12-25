package com.example.kevin.myfirebasechatapp122317;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by kevin on 12/23/2017.
 */

public class LogInActivity extends AppCompatActivity {
    private TextInputLayout mLoginEmail, mLoginPassword;
    private Button mBtnLogIn;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;
    private Toolbar mToolbar;

    private DatabaseReference mUserDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = findViewById(R.id.toolbar_login_page);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        mLoginEmail = findViewById(R.id.edittext_login_email);
        mLoginPassword = findViewById(R.id.edittext_login_password);

        mBtnLogIn = findViewById(R.id.btn_login_loginpage);

        mProgressDialog = new ProgressDialog(this);

        mBtnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_email = mLoginEmail.getEditText().getText().toString();
                String login_password = mLoginPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(login_email) || !TextUtils.isEmpty(login_password)){

                    mProgressDialog.setTitle("Logging In");
                    mProgressDialog.setMessage("Please Wait While We Verify Your Credentials");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    login_user(login_email, login_password);
                }
            }
        });
    }

    private void login_user(String login_email, String login_password) {
        mAuth.signInWithEmailAndPassword(login_email, login_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    mProgressDialog.dismiss();

                    String current_user_id = mCurrentUser.getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent mainIntent = new Intent(LogInActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                    });
                } else {
                    mProgressDialog.hide();
                    String task_result = task.getException().getMessage().toString();
                    Toast.makeText(LogInActivity.this, "Cannot Sign In. Please verify the credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

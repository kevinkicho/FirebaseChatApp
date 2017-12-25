package com.example.kevin.myfirebasechatapp122317;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by kevin on 12/23/2017.
 */

public class StartActivity extends AppCompatActivity {
    Button mBtnRegister, mBtnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mBtnRegister = findViewById(R.id.btn_start_to_register_activity);
        mBtnLogIn = findViewById(R.id.btn_sign_in);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });
        mBtnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartActivity.this, LogInActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }
}

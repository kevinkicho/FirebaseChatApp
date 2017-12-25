package com.example.kevin.myfirebasechatapp122317;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin on 12/24/2017.
 */

public class ProfileActivity extends AppCompatActivity {
    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mBtnProfileSendReq, mBtnProfileDeclineReq;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendsReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private ProgressDialog mProgressDialog;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    private String mCurrent_state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendsReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friends_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");


        mProfileImage = findViewById(R.id.imageView_profile_image);
        mProfileName = findViewById(R.id.textview_profile_displayname);
        mProfileStatus = findViewById(R.id.textView_profile_status);
        mProfileFriendsCount = findViewById(R.id.textView_profile_friends_total);
        mBtnProfileSendReq = findViewById(R.id.btn_send_friends_req);
        mBtnProfileDeclineReq = findViewById(R.id.btn_decline_friends_req);

        mCurrent_state = "not_friends";

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);

                // -----------------------FRIENDS LIST / REQUEST FEATURE -------------------

                mFriendsReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type")
                                    .getValue().toString();

                            if(req_type.equals("received")){
                                mCurrent_state = "req_received";
                                mBtnProfileSendReq.setText("ACCEPT FRIEND REQUEST");

                                mBtnProfileDeclineReq.setVisibility(View.VISIBLE);
                                mBtnProfileDeclineReq.setEnabled(false);
                            } else if(req_type.equals("sent")){
                                mCurrent_state = "req_sent";
                                mBtnProfileSendReq.setText("CANCEL FRIEND REQUEST");

                                mBtnProfileDeclineReq.setVisibility(View.INVISIBLE);
                                mBtnProfileDeclineReq.setEnabled(false);
                            }
                        } else {

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)){
                                        mCurrent_state = "friends";
                                        mBtnProfileSendReq.setText("UNFRIEND THIS PERSON");

                                        mBtnProfileDeclineReq.setVisibility(View.INVISIBLE);
                                        mBtnProfileDeclineReq.setEnabled(false);
                                    }
                                    mProgressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBtnProfileSendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBtnProfileSendReq.setEnabled(false);

                // - ---------------------- NOT FRIENDS STATE ---------------------------

                if (mCurrent_state.equals("not_friends")){

                    DatabaseReference newNotificationRef = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationRef.getKey();

                    final HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrentUser.getUid());
                    notificationData.put("type", "request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friends_req/" + mCurrentUser.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friends_req/" + user_id + "/" + mCurrentUser.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                Toast.makeText(ProfileActivity.this, "There was an Error in sending request", Toast.LENGTH_SHORT).show();
                            }
                            mBtnProfileSendReq.setEnabled(true);
                            mCurrent_state = "req_sent";
                            mBtnProfileSendReq.setText("CANCEL FRIEND REQUEST");
                        }
                    });
                }

                // - ----------------------- CANCEL REQUEST STATE ----------------------------

                if(mCurrent_state.equals("req_sent")){
                    mFriendsReqDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendsReqDatabase.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            mBtnProfileSendReq.setEnabled(true);
                                            mCurrent_state = "not_friends";
                                            mBtnProfileSendReq.setText("SEND FRIEND REQUEST");

                                            mBtnProfileDeclineReq.setVisibility(View.INVISIBLE);
                                            mBtnProfileDeclineReq.setEnabled(false);
                                        }
                                    });
                                }
                            });

                }

                // ------------------------- REQ RECEIVED STATE -------------------------------

                if(mCurrent_state.equals("req_received")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid() + "/date", currentDate);

                    friendsMap.put("Friends_req/" + mCurrentUser.getUid() + "/" + user_id, null);
                    friendsMap.put("Friends_req/" + user_id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null){
                                mBtnProfileSendReq.setEnabled(true);
                                mCurrent_state = "friends";
                                mBtnProfileSendReq.setText("UNFRIEND THIS PERSON");

                                mBtnProfileDeclineReq.setVisibility(View.INVISIBLE);
                                mBtnProfileDeclineReq.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                // ------------------------------------ UNFRIENDS --------------------------------

                if (mCurrent_state.equals("friends")){
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(), null);
                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null){

                                mCurrent_state = "not_friends";
                                mBtnProfileSendReq.setText("SEND FRIEND REQUEST");
                                mBtnProfileSendReq.setVisibility(View.VISIBLE);

                                mBtnProfileDeclineReq.setVisibility(View.INVISIBLE);
                                mBtnProfileDeclineReq.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}

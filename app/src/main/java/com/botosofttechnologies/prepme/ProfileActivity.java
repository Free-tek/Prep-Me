package com.botosofttechnologies.prepme;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView stars, name, email, subject1, subject2, subject3, subject4, subject_text;
    Button subscription;
    String $subscriptionStatus, $coins, $email, $name, $subject1, $subject2, $subject3, $subject4, $remainingTests;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference users = database.getReference().child("users");

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser User = mAuth.getCurrentUser();
    static final String userId = User.getUid();

    private Typeface header, subheading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUi();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initUi() {

        progressBar = (ProgressBar)findViewById(R.id.progress);
        Wave mWave = new Wave();
        mWave.setBounds(0,0,100,100);
        mWave.setColor(R.color.colorPrimaryDark);
        progressBar.setIndeterminateDrawable(mWave);


        stars = (TextView) findViewById(R.id.stars);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        subject_text = (TextView) findViewById(R.id.subject_text);
        subject1 = (TextView) findViewById(R.id.subject1);
        subject2 = (TextView) findViewById(R.id.subject2);
        subject3 = (TextView) findViewById(R.id.subject3);
        subject4 = (TextView) findViewById(R.id.subject4);

        subscription = (Button) findViewById(R.id.subscription);
        subscription.setVisibility(View.INVISIBLE);

        header = Typeface.createFromAsset(getAssets(), "fonts/heading.ttf");
        subheading = Typeface.createFromAsset(getAssets(), "fonts/subheading1.ttf");

        name.setTypeface(header);
        email.setTypeface(subheading);
        subject_text.setTypeface(subheading);
        stars.setTypeface(subheading);
        subject1.setTypeface(subheading);
        subject2.setTypeface(subheading);
        subject3.setTypeface(subheading);
        subject4.setTypeface(subheading);



        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                $subscriptionStatus = String.valueOf(dataSnapshot.child(userId).child("subscription").getValue());
                $remainingTests = String.valueOf(dataSnapshot.child(userId).child("remaining_subscription").getValue());
                $coins = String.valueOf(dataSnapshot.child(userId).child("coins").getValue());
                $email = String.valueOf(dataSnapshot.child(userId).child("email").getValue());
                $name = String.valueOf(dataSnapshot.child(userId).child("name").getValue());
                $subject1 = String.valueOf(dataSnapshot.child(userId).child("subjects").child("0").getValue());
                $subject2 = String.valueOf(dataSnapshot.child(userId).child("subjects").child("1").getValue());
                $subject3 = String.valueOf(dataSnapshot.child(userId).child("subjects").child("2").getValue());
                $subject4 = String.valueOf(dataSnapshot.child(userId).child("subjects").child("3").getValue());



                if($subscriptionStatus.equals("true")){
                    subscription.setText($remainingTests + " Tests Remaining");
                    subscription.setVisibility(View.VISIBLE);
                    //subscription.setText("Subscripiton Active");

                }else{
                    subscription.setVisibility(View.VISIBLE);
                    subscription.setText("Subscripiton Inactive");

                }

                stars.setText($coins);
                email.setText($email);
                name.setText($name);
                subject1.setText($subject1);
                subject2.setText($subject2);
                subject3.setText($subject3);
                subject4.setText($subject4);
                progressBar.setVisibility(View.INVISIBLE);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subscription.getText().equals("Subscripiton Active")){
                    Toast.makeText(ProfileActivity.this, "You have an active subscription plan", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
                    alertDialog.setMessage("Do you want to subscribe now?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        Intent intent =  new Intent(ProfileActivity.this, PaymentActivity.class);
                                        startActivity(intent);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Back",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }

                            });


                    alertDialog.show();

                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this, NavigationActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(ProfileActivity.this, NavigationActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.botosofttechnologies.prepme;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ProgressBar progressBar;
    TextView title, question, instruction;
    RadioGroup radioGroup;
    RadioButton optionA, optionB, optionC, optionD;
    Button submit;

    RelativeLayout layout;
    ImageView reward, logo;
    TextView rewardText, notice, name;
    Button ok, takeDemo, subscribe;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference users = database.getReference().child("users");
    final DatabaseReference leaderboard = database.getReference().child("leader_board");

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser User = mAuth.getCurrentUser();
    static final String userId = User.getUid();

    String subject, $answer, lastQuizDate, qSubject, $subscritpionCheck;
    int coins, selectRadomSubject,  $remainingTests;


    private Typeface header, subheading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initUi();

        $subscritpionCheck = "false";

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                final String date = dateFormat.format(currentDate);

                String subscriptionDate = String.valueOf(dataSnapshot.child(userId).child("subscription_date").getValue());

                try {
                    Date date1=new SimpleDateFormat("dd-MM-yyyy").parse(subscriptionDate);
                    Date date2=new SimpleDateFormat("dd-MM-yyyy").parse(date);

                    Calendar day1 = Calendar.getInstance();
                    Calendar day2 = Calendar.getInstance();
                    day1.setTime(date1);
                    day2.setTime(date2);

                    int daysBetween = day2.get(Calendar.DAY_OF_YEAR) - day1.get(Calendar.DAY_OF_YEAR);

                    if(daysBetween >= 360){
                        users.child(userId).child("subscription").setValue(false);
                    }else if(daysBetween >= 30){
                        users.child(userId).child("subject_change_count").setValue(0);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }


    private void initUi() {
        progressBar = (ProgressBar) findViewById(R.id.progress);
        Wave mWave = new Wave();
        mWave.setBounds(0,0,100,100);
        mWave.setColor(R.color.colorPrimaryDark);
        progressBar.setIndeterminateDrawable(mWave);


        title = (TextView) findViewById(R.id.title);
        question = (TextView) findViewById(R.id.question);
        instruction = (TextView) findViewById(R.id.instruction);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        optionA = (RadioButton) findViewById(R.id.optionA);
        optionB = (RadioButton) findViewById(R.id.optionB);
        optionC = (RadioButton) findViewById(R.id.optionC);
        optionD = (RadioButton) findViewById(R.id.optionD);

        submit = (Button) findViewById(R.id.submit);

        title.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.INVISIBLE);
        optionA.setVisibility(View.INVISIBLE);
        optionB.setVisibility(View.INVISIBLE);
        optionC.setVisibility(View.INVISIBLE);
        optionD.setVisibility(View.INVISIBLE);





        layout = (RelativeLayout) findViewById(R.id.layout);
        reward = (ImageView) findViewById(R.id.reward);
        rewardText = (TextView) findViewById(R.id.rewardText);
        ok = (Button) findViewById(R.id.ok);

        takeDemo = (Button) findViewById(R.id.take_demo);
        subscribe = (Button) findViewById(R.id.subscribe);
        logo = (ImageView) findViewById(R.id.logo);

        notice = (TextView) findViewById(R.id.notice);
        notice.setVisibility(View.INVISIBLE);

        layout.setVisibility(View.INVISIBLE);
        reward.setVisibility(View.INVISIBLE);
        rewardText.setVisibility(View.INVISIBLE);
        ok.setVisibility(View.INVISIBLE);



        header = Typeface.createFromAsset(getAssets(), "fonts/subheading.ttf");
        subheading = Typeface.createFromAsset(getAssets(), "fonts/subheading1.ttf");
        title.setTypeface(header);
        question.setTypeface(subheading);
        instruction.setTypeface(subheading);
        optionA.setTypeface(subheading);
        optionB.setTypeface(subheading);
        optionC.setTypeface(subheading);
        optionD.setTypeface(subheading);
        rewardText.setTypeface(subheading);

        optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionA.setBackground(getResources().getDrawable(R.drawable.button));
                optionA.setTextColor(getResources().getColor(R.color.white));
                optionB.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionC.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionD.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionB.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                optionC.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                optionD.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


            }
        });

        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionB.setBackground(getResources().getDrawable(R.drawable.button));
                optionB.setTextColor(getResources().getColor(R.color.white));
                optionA.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionC.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionD.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionA.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                optionC.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                optionD.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


            }
        });

        optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionC.setBackground(getResources().getDrawable(R.drawable.button));
                optionC.setTextColor(getResources().getColor(R.color.white));
                optionA.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionB.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionD.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionA.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                optionB.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                optionD.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


            }
        });

        optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionD.setBackground(getResources().getDrawable(R.drawable.button));
                optionD.setTextColor(getResources().getColor(R.color.white));
                optionA.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionC.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionB.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                optionA.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                optionB.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                optionC.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


            }
        });




        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastDate = String.valueOf(dataSnapshot.child(userId).child("lastQuizDate").getValue());
                String lastQuizSubject = String.valueOf(dataSnapshot.child(userId).child("lastQuizSubject").getValue());
                String lastQuizQuestionNo = String.valueOf(dataSnapshot.child(userId).child("lastQuizQuestionNo").getValue());
                String subsriptionValue = String.valueOf(dataSnapshot.child(userId).child("subscription").getValue());
                $remainingTests = Integer.parseInt(String.valueOf(dataSnapshot.child(userId).child("remaining_subscription").getValue()));

                $subscritpionCheck = subsriptionValue;


                Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                final String date = dateFormat.format(currentDate);


                if(lastDate.equals(date)){
                    //user has taken the daily quiz already

                    submit.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    title.setVisibility(View.INVISIBLE);
                    //getOldQuestions(lastQuizSubject, lastQuizQuestionNo);

                    if(subsriptionValue.equals("false") || $remainingTests == 0){
                        subscribe.setVisibility(View.VISIBLE);
                        takeDemo.setVisibility(View.VISIBLE);
                        logo.setVisibility(View.VISIBLE);
                    }else{
                        subscribe.setVisibility(View.VISIBLE);
                        if($remainingTests == 1){
                            subscribe.setText($remainingTests + " Test Remaining");
                        }else{
                            subscribe.setText($remainingTests + " Tests Remaining");
                        }

                        subscribe.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                        subscribe.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        subscribe.setEnabled(false);
                        takeDemo.setText("Take Test");
                        takeDemo.setVisibility(View.VISIBLE);
                        logo.setVisibility(View.VISIBLE);
                    }

                }else{
                    setNewQuestion();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        takeDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if($subscritpionCheck.equals("true")){
                    Intent intent = new Intent(NavigationActivity.this, TakeExamActivity.class);
                    intent.putExtra("demo",  "false");
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(NavigationActivity.this, TakeExamActivity.class);
                    intent.putExtra("demo",  "true");
                    startActivity(intent);
                }


            }
        });


        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NavigationActivity.this, PaymentActivity.class);
                startActivity(intent);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedAnswer = getOptionSelected();

                Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = dateFormat.format(currentDate);

                if(selectedAnswer == null){

                    AlertDialog alertDialog = new AlertDialog.Builder(NavigationActivity.this).create();
                    alertDialog.setTitle("OOps...");
                    alertDialog.setMessage("Please select a valid option" );
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            });
                    alertDialog.show();

                }else if(selectedAnswer.equalsIgnoreCase($answer)){
                    layout.setVisibility(View.VISIBLE);
                    reward.setVisibility(View.VISIBLE);
                    rewardText.setVisibility(View.VISIBLE);
                    ok.setVisibility(View.VISIBLE);

                    title.setAlpha((float) 0.2);
                    question.setAlpha((float) 0.2);
                    instruction.setAlpha((float) 0.2);
                    radioGroup.setAlpha((float) 0.2);
                    optionA.setAlpha((float) 0.2);
                    optionB.setAlpha((float) 0.2);
                    optionC.setAlpha((float) 0.2);
                    optionD.setAlpha((float) 0.2);
                    submit.setVisibility(View.INVISIBLE);


                    //users.child(userId).child("coins").setValue(coins + 2);
                    users.child(userId).child("lastQuizDate").setValue(date);
                    users.child(userId).child("lastQuizSubject").setValue(qSubject);
                    users.child(userId).child("lastQuizQuestionNo").setValue("01");

                    /*users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String lbcount = String.valueOf(dataSnapshot.child(userId).child("lbKey").getValue());
                            leaderboard.child(lbcount).child("star").setValue(String.valueOf(coins+2));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/


                    //TODO: Set last quiz question number to be its original value
                    //getOldQuestions(qSubject, "01");



                }else{
                    layout.setVisibility(View.VISIBLE);
                    reward.setVisibility(View.VISIBLE);
                    rewardText.setVisibility(View.VISIBLE);
                    ok.setVisibility(View.VISIBLE);

                    reward.setImageDrawable(getResources().getDrawable(R.drawable.sad));
                    rewardText.setText(R.string.lost);

                    title.setAlpha((float) 0.2);
                    question.setAlpha((float) 0.2);
                    instruction.setAlpha((float) 0.2);
                    radioGroup.setAlpha((float) 0.2);
                    optionA.setAlpha((float) 0.2);
                    optionB.setAlpha((float) 0.2);
                    optionC.setAlpha((float) 0.2);
                    optionD.setAlpha((float) 0.2);
                    submit.setVisibility(View.INVISIBLE);


                    users.child(userId).child("lastQuizDate").setValue(date);
                    users.child(userId).child("lastQuizSubject").setValue(qSubject);
                    users.child(userId).child("lastQuizQuestionNo").setValue("01");

                    //getOldQuestions(qSubject, "01");

                }
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeDemo.setVisibility(View.VISIBLE);
                subscribe.setVisibility(View.VISIBLE);
                logo.setVisibility(View.VISIBLE);

                title.setVisibility(View.INVISIBLE);
                question.setVisibility(View.INVISIBLE);
                instruction.setVisibility(View.INVISIBLE);
                radioGroup.setVisibility(View.INVISIBLE);
                optionA.setVisibility(View.INVISIBLE);
                optionB.setVisibility(View.INVISIBLE);
                optionC.setVisibility(View.INVISIBLE);
                optionD.setVisibility(View.INVISIBLE);


                layout.setVisibility(View.INVISIBLE);
                reward.setVisibility(View.INVISIBLE);
                rewardText.setVisibility(View.INVISIBLE);
                ok.setVisibility(View.INVISIBLE);

                /*title.setAlpha((float) 1);
                question.setAlpha((float) 1);
                instruction.setAlpha((float) 1);
                radioGroup.setAlpha((float) 1);
                optionA.setAlpha((float) 1);
                optionB.setAlpha((float) 1);
                optionC.setAlpha((float) 1);
                optionD.setAlpha((float) 1);*/
            }
        });


    }

    private void setNewQuestion() {

        selectRadomSubject = (int) (Math.random() * 3);


        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subject = String.valueOf(dataSnapshot.child(userId).child("subjects").child(String.valueOf(selectRadomSubject)).getValue());
                coins = Integer.parseInt(String.valueOf(dataSnapshot.child(userId).child("coins").getValue()));
                lastQuizDate = String.valueOf(dataSnapshot.child(userId).child("lastQuizDate").getValue());
                qSubject = "m"+subject;



                final int questionNo = (int) (Math.random() * 100) ;


                //TODO:questionNo should be used to replace "1" below and lastQuizNo value saved to Firebase;
                //TODO:change reference database to database for question of the day questions


                final DatabaseReference questionSubject = database.getReference().child(qSubject);
                questionSubject.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        $answer = String.valueOf(dataSnapshot.child("1").child("answer").getValue()).toLowerCase();
                        String $optionA = String.valueOf(dataSnapshot.child("1").child("optionA").getValue());
                        String $optionB = String.valueOf(dataSnapshot.child("1").child("optionB").getValue());
                        String $optionC = String.valueOf(dataSnapshot.child("1").child("optionC").getValue());
                        String $optionD = String.valueOf(dataSnapshot.child("1").child("optionD").getValue());
                        String $question = String.valueOf(dataSnapshot.child("1").child("question").getValue());
                        String $instruction = String.valueOf(dataSnapshot.child("1").child("instruction").getValue());

                        question.setText($question);
                        instruction.setText($instruction);


                        title.setVisibility(View.VISIBLE);
                        submit.setVisibility(View.VISIBLE);
                        optionA.setVisibility(View.VISIBLE);
                        optionB.setVisibility(View.VISIBLE);
                        optionC.setVisibility(View.VISIBLE);
                        optionD.setVisibility(View.VISIBLE);

                        optionA.setText($optionA);
                        optionB.setText($optionB);
                        optionC.setText($optionC);
                        optionD.setText($optionD);

                        if($instruction.equals("")){
                            instruction.setVisibility(View.GONE);
                        }else{
                            instruction.setVisibility(View.VISIBLE);
                        }


                        progressBar.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }

    private void getOldQuestions(String lastQuizSubject, final String lastQuizQuestionNo) {

        final DatabaseReference questionSubject = database.getReference().child(lastQuizSubject);
        questionSubject.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                $answer = String.valueOf(dataSnapshot.child(lastQuizQuestionNo).child("answer").getValue()).toLowerCase();
                String $optionA = String.valueOf(dataSnapshot.child(lastQuizQuestionNo).child("optionA").getValue());
                String $optionB = String.valueOf(dataSnapshot.child(lastQuizQuestionNo).child("optionB").getValue());
                String $optionC = String.valueOf(dataSnapshot.child(lastQuizQuestionNo).child("optionC").getValue());
                String $optionD = String.valueOf(dataSnapshot.child(lastQuizQuestionNo).child("optionD").getValue());
                String $question = String.valueOf(dataSnapshot.child(lastQuizQuestionNo).child("question").getValue());
                String $instruction = String.valueOf(dataSnapshot.child(lastQuizQuestionNo).child("instruction").getValue());

                question.setText($question);
                instruction.setText($instruction);
                optionA.setText($optionA);
                optionB.setText($optionB);
                optionC.setText($optionC);
                optionD.setText($optionD);
                submit.setVisibility(View.INVISIBLE);
                notice.setVisibility(View.VISIBLE);

                optionA.setVisibility(View.VISIBLE);
                optionB.setVisibility(View.VISIBLE);
                optionC.setVisibility(View.VISIBLE);
                optionD.setVisibility(View.VISIBLE);

                if($instruction.equals("")){
                    instruction.setVisibility(View.GONE);
                }else{
                    instruction.setVisibility(View.VISIBLE);
                }

                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private String getOptionSelected() {
        String answer = null;
        if(optionA.isChecked()){
            answer = "a";
        }else if(optionB.isChecked()){
            answer = "b";
        }else if(optionC.isChecked()){
            answer = "c";
        }else if(optionD.isChecked()){
            answer = "d";
        }

        return answer;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog alertDialog = new AlertDialog.Builder(NavigationActivity.this).create();
            alertDialog.setTitle("Proceed?");
            alertDialog.setMessage("Do you want to sign out" );
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent signOut = new Intent (NavigationActivity.this, LoginActivity.class);
                            FirebaseAuth.getInstance().signOut();
                            startActivity(signOut);
                            finish();                                     }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.take_exam) {
            if($subscritpionCheck.equals("true")){
                Intent intent = new Intent(NavigationActivity.this, TakeExamActivity.class);
                intent.putExtra("demo",  "false");
                startActivity(intent);
            }else{
                Intent intent = new Intent(NavigationActivity.this, TakeExamActivity.class);
                intent.putExtra("demo",  "false");
                startActivity(intent);
            }
        } else if (id == R.id.leaderboard) {
            Intent intent = new Intent(NavigationActivity.this, LeaderBoardActivity.class);
            startActivity(intent);
        } /*else if (id == R.id.questions) {

        } */else if (id == R.id.profile) {
            Intent intent  = new Intent(NavigationActivity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.subscription) {
            Intent intent = new Intent(NavigationActivity.this, PaymentActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.change_subject) {
            Intent intent = new Intent(NavigationActivity.this, SubjectChangeActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

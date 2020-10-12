package com.botosofttechnologies.prepme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.squareup.picasso.Picasso;


import java.util.stream.*;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class ExamActivity extends AppCompatActivity {

    //TODO: chage subscription and remaining_test afterwards
    //TODO: Handle network switch off
    TextView number, time, question, instruction, demo, comprehension, minute;
    RadioGroup radioGroup;
    RadioButton optionA, optionB, optionC, optionD;
    Button submit;
    int paper1Count, paper2Count, paper3Count, paper4Count, totalComprehesionBatchSize;
    int currentPaper1Count, currentPaper2Count, currentPaper3Count, currentPaper4Count, currentPaper;
    String paper1, paper2, paper3, paper4, answer, examNo, studentName, _demo;
    RelativeLayout more;
    ImageView questionImage;

    ScrollView scroll;
    int comprehensionQuestionCount, comprehensionType;

    ArrayList<Integer> englishQ;
    ArrayList<Integer> _paper2;
    ArrayList<Integer> _paper3;
    ArrayList<Integer> _paper4;

    //int[] englishQ = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    //int[] _paper2 = { 1, 2, 3, 4, 5};
    //int[] _paper3 = { 1, 2, 3, 4, 5};
    //int[] _paper4 = { 1, 2, 3, 4, 5};




    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference users = database.getReference().child("users");
    final DatabaseReference leaderboard = database.getReference().child("leader_board");

    DatabaseReference paper1db;
    DatabaseReference paper1dbComprehesion;
    DatabaseReference paper2db;
    DatabaseReference paper3db;
    DatabaseReference paper4db;


    int[] paper1QuestionsArray = {};
    int[] paper2QuestionsArray = {};
    int[] paper3QuestionsArray = {};
    int[] paper4QuestionsArray = {};

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser User = mAuth.getCurrentUser();
    static final String userId = User.getUid();

    private int scorePaper1,  scorePaper2, scorePaper3, scorePaper4;

    CountDownTimer timer;
    long milliLeft;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //TODO:Number of  questions per paper
        /*if(_demo.equals("true")){
            // a demo test
            paper1Count = 7; //60
            paper2Count = 3; //40
            paper3Count = 3; //40
            paper4Count = 3; //40

            getQuestionNo();
            //time for demo 10 minutes
            timerStart(600000);
        }else{
            //not a demo test
            paper1Count = 25;
            paper2Count = 15;
            paper3Count = 15;
            paper4Count = 15;

            getQuestionNo();
            //time for exam 60 minutes
            timerStart(3600000);

        }


        englishQ = getRandomNonRepeatingIntegers(paper1Count, 1, 250);
        _paper2 = getRandomNonRepeatingIntegers(paper2Count, 1, 250);
        _paper3 = getRandomNonRepeatingIntegers(paper3Count, 1, 250);
        _paper4 = getRandomNonRepeatingIntegers(paper4Count, 1, 250);

        */

        //TODO: Change this  to the total number of comprehesion passages you have
        totalComprehesionBatchSize = 1;

        //TODO: remove test
        paper1Count = 7;
        paper2Count = 3;
        paper3Count = 3;
        paper4Count = 3;
        timerStart(600000);

        englishQ = getRandomNonRepeatingIntegers(paper1Count, 1, 10);
        _paper2 = getRandomNonRepeatingIntegers(paper2Count, 1, 5);
        _paper3 = getRandomNonRepeatingIntegers(paper3Count, 1, 5);
        _paper4 = getRandomNonRepeatingIntegers(paper4Count, 1, 5);




        Log.e("shuffle", "paper1 ::: " + englishQ + "paper2 ::: " + _paper2 + "paper3 ::: " + _paper3 + "paper4 ::: " + _paper4);
        //Stop deletion

        Collections.shuffle(englishQ);
        Collections.shuffle(_paper2);
        Collections.shuffle(_paper3);
        Collections.shuffle(_paper4);

        Bundle bundle = getIntent().getExtras();
        paper2 = bundle.getString("paper2");
        paper3 = bundle.getString("paper3");
        paper4 = bundle.getString("paper4");
        examNo = bundle.getString("examNo");
        studentName = bundle.getString("studentName");
        _demo = bundle.getString("demo");

        String $paper2 = "m"+paper2;
        String $paper3 = "m"+paper3;
        String $paper4 = "m"+paper4;

        paper1db = database.getReference().child("mEnglish Language");
        paper1dbComprehesion = database.getReference().child("mEnglish LanguageComprehension");
        paper2db = database.getReference().child($paper2);
        paper3db = database.getReference().child($paper3);
        paper4db = database.getReference().child($paper4);

        currentPaper1Count = 0;
        currentPaper2Count = 0;
        currentPaper3Count = 0;
        currentPaper4Count = 0;


        scorePaper1 = 0;
        scorePaper2 = 0;
        scorePaper3 = 0;
        scorePaper4 = 0;

        comprehensionQuestionCount = 0;

        initUi();
        getQuestionNo();

        if(!_demo.equals("true")){
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    int $remainingSubscription = Integer.parseInt(String.valueOf(dataSnapshot.child(userId).child("remaining_subscription").getValue()));
                    users.child(userId).child("remaining_subscription").setValue($remainingSubscription - 1);

                    if(($remainingSubscription - 1) == 0){
                        users.child(userId).child("subscription").setValue(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void initUi() {

        scroll = (ScrollView) findViewById(R.id.scroll);

        more = (RelativeLayout) findViewById(R.id.more);
        questionImage = (ImageView) findViewById(R.id.question_image);
        comprehension = (TextView) findViewById(R.id.comprehension);

        progressBar = (ProgressBar)findViewById(R.id.progress);
        Wave mWave = new Wave();
        mWave.setBounds(0,0,100,100);
        mWave.setColor(R.color.colorPrimaryDark);
        progressBar.setIndeterminateDrawable(mWave);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setAlpha((float)0.5);

        number = (TextView) findViewById(R.id.number);
        time = (TextView) findViewById(R.id.time);
        question = (TextView) findViewById(R.id.question);
        instruction = (TextView) findViewById(R.id.instruction);

        demo = (TextView) findViewById(R.id.demo);
        minute = (TextView) findViewById(R.id.minute);

        if(_demo.equals("true")){
            demo.setVisibility(View.VISIBLE);
        }else{
            demo.setVisibility(View.VISIBLE);
        }

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        optionA = (RadioButton) findViewById(R.id.optionA);
        optionB = (RadioButton) findViewById(R.id.optionB);
        optionC = (RadioButton) findViewById(R.id.optionC);
        optionD = (RadioButton) findViewById(R.id.optionD);


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

        submit = (Button) findViewById(R.id.submit);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerPause();

                String selectedAnswer = getOptionSelected();
                if(selectedAnswer == null){

                    final AlertDialog alertDialog = new AlertDialog.Builder(ExamActivity.this).create();
                    alertDialog.setTitle("Oops...");
                    alertDialog.setMessage("Please select an answer" );
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   alertDialog.dismiss();
                                }
                            });


                    alertDialog.show();
                }//last demo question

                else if(currentPaper1Count == paper1Count && currentPaper2Count == paper2Count && currentPaper3Count == paper3Count && currentPaper4Count == paper4Count){
                    //Intent to final score page because this is the last question;

                    //check if user passed the question or not
                    if(currentPaper == 1 && selectedAnswer.equals(answer)){
                        scorePaper1 ++;
                    }else if(currentPaper == 2 && selectedAnswer.equals(answer)){
                        scorePaper2 ++;
                    }else if(currentPaper == 3 && selectedAnswer.equals(answer)){
                        scorePaper3 ++;
                    }else if(currentPaper == 4 && selectedAnswer.equals(answer)){
                        scorePaper4 ++;
                    }else{
                        //User failed the question
                        //optionA.setChecked(true);
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    String total = String.valueOf(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4);


                    if(!_demo.equals("true")){
                        //save users score

                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String lbcount = String.valueOf(dataSnapshot.child(userId).child("lbKey").getValue());
                                String currentTotal = String.valueOf(dataSnapshot.child(userId).child("currentExamTotal").child("total").getValue());
                                leaderboard.child(lbcount).child("star").setValue(String.valueOf(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4));

                                if(Integer.parseInt(currentTotal) <= scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4){
                                    users.child(userId).child("currentExam").child("English Language").setValue(scorePaper1);
                                    users.child(userId).child("currentExam").child(paper2).setValue(scorePaper2);
                                    users.child(userId).child("currentExam").child(paper3).setValue(scorePaper3);
                                    users.child(userId).child("currentExam").child(paper4).setValue(scorePaper4);
                                    users.child(userId).child("currentExamTotal").child("total").setValue(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4);
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        /*users.child(userId).child("currentExam").child("English Language").setValue(scorePaper1);
                        users.child(userId).child("currentExam").child(paper2).setValue(scorePaper2);
                        users.child(userId).child("currentExam").child(paper3).setValue(scorePaper3);
                        users.child(userId).child("currentExam").child(paper4).setValue(scorePaper4);
                        users.child(userId).child("currentExamTotal").child("total").setValue(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4);
*/
                    }

                    Intent intent = new Intent(ExamActivity.this, ExamScoreActivity.class);
                    intent.putExtra("total", total);
                    intent.putExtra("paper1", "English Language" );
                    intent.putExtra("paper2", paper2 );
                    intent.putExtra("paper3", paper3 );
                    intent.putExtra("paper4", paper4 );
                    intent.putExtra("paper1score", String.valueOf(scorePaper1));
                    intent.putExtra("paper2score", String.valueOf(scorePaper2));
                    intent.putExtra("paper3score", String.valueOf(scorePaper3) );
                    intent.putExtra("paper4score", String.valueOf(scorePaper4) );
                    intent.putExtra("ExamNo", examNo );
                    intent.putExtra("StudentName", studentName );
                    intent.putExtra("demo", _demo );
                    progressBar.setVisibility(View.INVISIBLE);
                    startActivity(intent);

                }//not the last question
                else{




                    if(currentPaper == 1 && selectedAnswer.equals(answer)){
                        scorePaper1 ++;
                        progressBar.setVisibility(View.VISIBLE);

                        //optionA.setChecked(true);



                        getQuestionNo();
                    }else if(currentPaper == 2 && selectedAnswer.equals(answer)){
                        scorePaper2 ++;
                        progressBar.setVisibility(View.VISIBLE);

                        //optionA.setChecked(true);


                        getQuestionNo();
                    }else if(currentPaper == 3 && selectedAnswer.equals(answer)){
                        scorePaper3 ++;
                        progressBar.setVisibility(View.VISIBLE);

                        //optionA.setChecked(true);


                        getQuestionNo();
                    }else if(currentPaper == 4 && selectedAnswer.equals(answer)){
                        scorePaper4 ++;
                        progressBar.setVisibility(View.VISIBLE);

                        //optionA.setChecked(true);


                        getQuestionNo();
                    }else{
                        //User failed the question
                        //optionA.setChecked(true);

                        progressBar.setVisibility(View.VISIBLE);

                        getQuestionNo();
                    }

                    //save users score just incase the test cancels provided it is not a demo test

                    if(!_demo.equals("true")){

                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String lbcount = String.valueOf(dataSnapshot.child(userId).child("lbKey").getValue());
                                String currentTotal = String.valueOf(dataSnapshot.child(userId).child("currentExamTotal").child("total").getValue());
                                leaderboard.child(lbcount).child("star").setValue(String.valueOf(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4));

                                if(Integer.parseInt(currentTotal) <= scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4){
                                    users.child(userId).child("currentExam").child("English Language").setValue(scorePaper1);
                                    users.child(userId).child("currentExam").child(paper2).setValue(scorePaper2);
                                    users.child(userId).child("currentExam").child(paper3).setValue(scorePaper3);
                                    users.child(userId).child("currentExam").child(paper4).setValue(scorePaper4);
                                    users.child(userId).child("currentExamTotal").child("total").setValue(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4);
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        /*users.child(userId).child("currentExam").child("English Language").setValue(scorePaper1);
                        users.child(userId).child("currentExam").child(paper2).setValue(scorePaper2);
                        users.child(userId).child("currentExam").child(paper3).setValue(scorePaper3);
                        users.child(userId).child("currentExam").child(paper4).setValue(scorePaper4);
                        users.child(userId).child("currentExamTotal").child("total").setValue(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4);*/


                    }


                    optionA.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                    optionB.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                    optionC.setBackground(getResources().getDrawable(R.drawable.transparent_text));
                    optionD.setBackground(getResources().getDrawable(R.drawable.transparent_text));

                    optionA.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    optionB.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    optionC.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    optionD.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    scroll.scrollTo(0, 0);

                }

            }
        });

    }

    private void getQuestion(final int questionNo, final int paperNo) {

        Log.e("result", "" + questionNo + " ::: " + paperNo);
        radioGroup.clearCheck();

        if(paperNo == 1){

            if(comprehensionQuestionCount ==  0 && !_demo.equals("true")){

                //select comprehension passage type
                comprehensionType = (int) (Math.random() * totalComprehesionBatchSize) + 1;

                comprehensionQuestionCount = (comprehensionType * 5) - 4;

                //user has not answered comprehension questions
                paper1dbComprehesion.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String mquestion = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("question").getValue());
                        String moptionA = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("optionA").getValue());
                        String moptionB = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("optionB").getValue());
                        String moptionC = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("optionC").getValue());
                        String moptionD = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("optionD").getValue());
                        String manswer = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("answer").getValue()).toLowerCase();
                        String mcomprehension = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("comprehension").getValue());
                        String minstruction = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("instruction").getValue());


                        //number.setText(String.valueOf(currentPaper1Count));
                        question.setText(mquestion);
                        optionA.setText(moptionA);
                        optionB.setText(moptionB);
                        optionC.setText(moptionC);
                        optionD.setText(moptionD);


                        instruction.setText(minstruction);
                        answer = manswer;

                        //Configure comprehension
                        more.setVisibility(View.VISIBLE);
                        comprehension.setVisibility(View.VISIBLE);
                        questionImage.setVisibility(View.INVISIBLE);
                        comprehension.setText(mcomprehension);


                        if(minstruction.equals("")){
                            instruction.setVisibility(View.GONE);
                        }else{
                            instruction.setVisibility(View.VISIBLE);
                        }

                        currentPaper = 1;
                        comprehensionQuestionCount ++;
                        progressBar.setVisibility(View.INVISIBLE);
                        timerResume();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }else if (comprehensionQuestionCount <= 4 && !_demo.equals("true")){
                //user has answered first comprehension question
                paper1dbComprehesion.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String mquestion = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("question").getValue());
                        String moptionA = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("optionA").getValue());
                        String moptionB = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("optionB").getValue());
                        String moptionC = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("optionC").getValue());
                        String moptionD = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("optionD").getValue());
                        String manswer = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("answer").getValue()).toLowerCase();
                        String mcomprehension = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("comprehension").getValue());
                        String minstruction = String.valueOf(dataSnapshot.child(String.valueOf(comprehensionQuestionCount)).child("instruction").getValue());


                        //number.setText(String.valueOf(currentPaper1Count));
                        question.setText(mquestion);
                        optionA.setText(moptionA);
                        optionB.setText(moptionB);
                        optionC.setText(moptionC);
                        optionD.setText(moptionD);


                        instruction.setText(minstruction);
                        answer = manswer;

                        //Configure comprehension
                        more.setVisibility(View.VISIBLE);
                        comprehension.setVisibility(View.VISIBLE);
                        questionImage.setVisibility(View.INVISIBLE);
                        comprehension.setText(mcomprehension);


                        if(minstruction.equals("")){
                            instruction.setVisibility(View.GONE);
                        }else{
                            instruction.setVisibility(View.VISIBLE);
                        }

                        currentPaper = 1;
                        comprehensionQuestionCount ++;
                        progressBar.setVisibility(View.INVISIBLE);
                        timerResume();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }else{

                //not a comprehension passage question
                //TODO: show english comprehesion
                paper1db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String mquestion = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("question").getValue());
                        String moptionA = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionA").getValue());
                        String moptionB = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionB").getValue());
                        String moptionC = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionC").getValue());
                        String moptionD = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionD").getValue());
                        String manswer = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("answer").getValue()).toLowerCase();
                        String minstruction = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("instruction").getValue());


                        //number.setText(String.valueOf(currentPaper1Count));
                        question.setText(mquestion);
                        optionA.setText(moptionA);
                        optionB.setText(moptionB);
                        optionC.setText(moptionC);
                        optionD.setText(moptionD);

                        more.setVisibility(View.GONE);


                        instruction.setText(minstruction);
                        answer = manswer;

                        if(minstruction.equals("")){
                            instruction.setVisibility(View.GONE);
                        }else{
                            instruction.setVisibility(View.VISIBLE);
                        }

                        currentPaper = 1;
                        progressBar.setVisibility(View.INVISIBLE);
                        timerResume();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


        }else if(paperNo == 2){

            paper2db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String mquestion = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("question").getValue());
                    String moptionA = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionA").getValue());
                    String moptionB = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionB").getValue());
                    String moptionC = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionC").getValue());
                    String moptionD = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionD").getValue());
                    String manswer = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("answer").getValue()).toLowerCase();
                    String minstruction = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("instruction").getValue());
                    String mimageUrl = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("image").getValue());

                    //number.setText(String.valueOf(currentPaper2Count));
                    question.setText(mquestion);
                    optionA.setText(moptionA);
                    optionB.setText(moptionB);
                    optionC.setText(moptionC);
                    optionD.setText(moptionD);
                    instruction.setText(minstruction);

                    answer = manswer;

                    if(minstruction.equals("")){
                        instruction.setVisibility(View.GONE);
                    }else{
                        instruction.setVisibility(View.VISIBLE);
                    }

                    if (mimageUrl.length() >= 10){
                        //question has an image
                        more.setVisibility(View.VISIBLE);
                        comprehension.setVisibility(View.GONE);
                        Picasso.with(ExamActivity.this).load(mimageUrl).into(questionImage);
                        questionImage.setVisibility(View.VISIBLE);

                    }else{
                        more.setVisibility(View.GONE);
                    }

                    currentPaper = 2;
                    progressBar.setVisibility(View.INVISIBLE);
                    timerResume();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else if(paperNo == 3){

            paper3db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String mquestion = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("question").getValue());
                    String moptionA = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionA").getValue());
                    String moptionB = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionB").getValue());
                    String moptionC = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionC").getValue());
                    String moptionD = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionD").getValue());
                    String manswer = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("answer").getValue()).toLowerCase();
                    String minstruction = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("instruction").getValue());
                    String mimageUrl = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("image").getValue());


                    //number.setText(String.valueOf(currentPaper3Count));
                    question.setText(mquestion);
                    optionA.setText(moptionA);
                    optionB.setText(moptionB);
                    optionC.setText(moptionC);
                    optionD.setText(moptionD);
                    instruction.setText(minstruction);
                    answer = manswer;

                    if(minstruction.equals("")){
                        instruction.setVisibility(View.GONE);
                    }else{
                        instruction.setVisibility(View.VISIBLE);
                    }


                    if (mimageUrl.length() >= 10){
                        //question has an image
                        more.setVisibility(View.VISIBLE);
                        comprehension.setVisibility(View.GONE);
                        Picasso.with(ExamActivity.this).load(mimageUrl).into(questionImage);
                        questionImage.setVisibility(View.VISIBLE);

                    }else{
                        more.setVisibility(View.GONE);
                    }


                    currentPaper = 3;
                    progressBar.setVisibility(View.INVISIBLE);
                    timerResume();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }else if(paperNo == 4){

            paper4db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String mquestion = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("question").getValue());
                    String moptionA = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionA").getValue());
                    String moptionB = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionB").getValue());
                    String moptionC = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionC").getValue());
                    String moptionD = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("optionD").getValue());
                    String manswer = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("answer").getValue()).toLowerCase();
                    String minstruction = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("instruction").getValue());
                    String mimageUrl = String.valueOf(dataSnapshot.child(String.valueOf(questionNo)).child("image").getValue());


                    //number.setText(String.valueOf(currentPaper4Count));
                    question.setText(mquestion);
                    optionA.setText(moptionA);
                    optionB.setText(moptionB);
                    optionC.setText(moptionC);
                    optionD.setText(moptionD);
                    instruction.setText(minstruction);
                    answer = manswer;

                    if(minstruction.equals("")){
                        instruction.setVisibility(View.GONE);
                    }else{
                        instruction.setVisibility(View.VISIBLE);
                    }


                    if (mimageUrl.length() >= 10){
                        //question has an image
                        more.setVisibility(View.VISIBLE);
                        comprehension.setVisibility(View.GONE);
                        Picasso.with(ExamActivity.this).load(mimageUrl).into(questionImage);
                        questionImage.setVisibility(View.VISIBLE);

                    }else{
                        more.setVisibility(View.GONE);
                    }

                    currentPaper = 4;
                    progressBar.setVisibility(View.INVISIBLE);
                    timerResume();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void getQuestionNo() {

        //get random question number first
        int questionNoPaper1 = (int) (10 * Math.random()) + 1;
        int questionNoPaper = (int) (5 * Math.random()) + 1;


        if(currentPaper1Count != paper1Count){
            getQuestion(englishQ.get(0), 1);
            englishQ.remove(0);
            //englishQ = removeTheElement(englishQ, 0);
            //removeItems(englishQ, 0);
            currentPaper1Count++;
            return;
        }else if(currentPaper2Count != paper2Count){
            getQuestion(_paper2.get(0), 2);
            _paper2.remove(0);
            //_paper2 = removeTheElement(_paper2, 0);
            //removeItems(_paper2, 0);
            currentPaper2Count++;
            return;
        }else if(currentPaper3Count != paper3Count){
            getQuestion(_paper3.get(0), 3);
            _paper3.remove(0);
            //_paper3 = removeTheElement(_paper3, 0);
            //removeItems(_paper3, 0);
            currentPaper3Count++;
            return;
        }else if(currentPaper4Count != paper4Count){
            getQuestion(_paper4.get(0), 4);
            _paper4.remove(0);
            //_paper4 = removeTheElement(_paper4, 0);
            //removeItems(_paper4, 0);
            currentPaper4Count++;
            return;
        }

        Log.e("error", "repeated question" );
    }

    public int removeItems(Object[] a, int... r) {
        int shift = 0;
        for (int i = 0; i < a.length; i++) {
            if (shift < r.length && i == r[shift])  // i-th item needs to be removed
                shift++;                            // increment `shift`
            else
                a[i - shift] = a[i];                // move i-th item `shift` positions left
        }
        for (int i = a.length - shift; i < a.length; i++)
            a[i] = null;                            // replace remaining items by nulls

        return a.length - shift;                    // return new "length"
    }

    public static int[] removeTheElement(int[] arr,
                                         int index)
    {

        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null
                || index < 0
                || index >= arr.length) {

            return arr;
        }

        // Create another array of size one less
        int[] anotherArray = new int[arr.length - 1];

        // Copy the elements except the index
        // from original array to the other array
        for (int i = 0, k = 0; i < arr.length; i++) {

            // if the index is
            // the removal element index
            if (i == index) {
                continue;
            }

            // if the index is not
            // the removal element index
            anotherArray[k++] = arr[i];
        }

        // return the resultant array
        return anotherArray;
    }



    public static boolean contains(int[] arr, int item) {
        int index = Arrays.binarySearch(arr, item);
        return index >= 0;
    }

    static int[] addElement(int[] a, int e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
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

    public void timerStart(long timeLengthMilli) {
        timer = new CountDownTimer(timeLengthMilli, 1000) {

            @Override
            public void onTick(long milliTillFinish) {
                milliLeft = milliTillFinish;
                long min = (milliTillFinish / (1000 * 60));
                int seconds = (int) (milliTillFinish / 1000) % 60 ;
                int minutes = (int) ((milliTillFinish / (1000*60)));
                int hours   = (int) ((milliTillFinish / (1000*60*60)) % 24);

                String $time = "" + minutes;
                time.setText($time);

                if(minutes <= 5){
                    time.setBackground(getResources().getDrawable(R.drawable.circle_number_red));
                    minute.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onFinish() {
                //time has finished lets tell test taker that his time is up and pass him to the next Intent

                //save users score  if it  is not a demo test
                if(!_demo.equals("true")){


                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String lbcount = String.valueOf(dataSnapshot.child(userId).child("lbKey").getValue());
                            String currentTotal = String.valueOf(dataSnapshot.child(userId).child("currentExamTotal").child("total").getValue());
                            leaderboard.child(lbcount).child("star").setValue(String.valueOf(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4));

                            if(Integer.parseInt(currentTotal) <= scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4){
                                users.child(userId).child("currentExam").child("English Language").setValue(scorePaper1);
                                users.child(userId).child("currentExam").child(paper2).setValue(scorePaper2);
                                users.child(userId).child("currentExam").child(paper3).setValue(scorePaper3);
                                users.child(userId).child("currentExam").child(paper4).setValue(scorePaper4);
                                users.child(userId).child("currentExamTotal").child("total").setValue(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4);
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }



                Intent intent = new Intent(ExamActivity.this, ExamScoreActivity.class);
                intent.putExtra("total", String.valueOf(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4));
                intent.putExtra("paper1", "English Language" );
                intent.putExtra("paper2", String.valueOf(paper2) );
                intent.putExtra("paper3", String.valueOf(paper3) );
                intent.putExtra("paper4", String.valueOf(paper4) );
                intent.putExtra("paper1score", String.valueOf(scorePaper1));
                intent.putExtra("paper2score", String.valueOf(scorePaper2));
                intent.putExtra("paper3score", String.valueOf(scorePaper3) );
                intent.putExtra("paper4score", String.valueOf(scorePaper4) );
                intent.putExtra("ExamNo", String.valueOf(examNo) );
                intent.putExtra("StudentName", String.valueOf(studentName) );
                intent.putExtra("demo", String.valueOf(_demo) );
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(intent);
            }

        };
        timer.start();
    }

    public static int getRandomInt(int min, int max) {
        Random random = new Random();

        return random.nextInt((max - min) + 1) + min;
    }

    public static ArrayList<Integer> getRandomNonRepeatingIntegers(int size, int min,
                                                                   int max) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();

        while (numbers.size() < size) {
            int random = getRandomInt(min, max);

            if (!numbers.contains(random)) {
                numbers.add(random);
            }
        }

        return numbers;
    }


    public void timerPause() {
        timer.cancel();
    }

    private void timerResume() {
        timerStart(milliLeft);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ExamActivity.this, NavigationActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(ExamActivity.this, NavigationActivity.class);
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

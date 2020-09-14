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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.Arrays;
import java.util.Collections;

public class ExamActivity extends AppCompatActivity {

    TextView number, time, question, instruction, demo;
    RadioGroup radioGroup;
    RadioButton optionA, optionB, optionC, optionD;
    Button submit;
    int paper1Count, paper2Count, paper3Count, paper4Count;
    int currentPaper1Count, currentPaper2Count, currentPaper3Count, currentPaper4Count, currentPaper;
    String paper1, paper2, paper3, paper4, answer, examNo, studentName, _demo;

    int[] englishQ = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    int[] _paper2 = { 1, 2, 3, 4, 5};
    int[] _paper3 = { 1, 2, 3, 4, 5};
    int[] _paper4 = { 1, 2, 3, 4, 5};
    int[] _paper5 = { 1, 2, 3, 4, 5};


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference users = database.getReference().child("users");
    DatabaseReference paper1db;
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

        /*for (int i = 0; i < englishQ.length; i++) {
            englishQ[i] = i+1;
            Collections.shuffle(Arrays.asList(englishQ));

        }

        for (int i = 0; i < _paper2.length; i++) {
            _paper2[i] = i+1;
            _paper3[i] = i+1;
            _paper4[i] = i+1;
            Collections.shuffle(Arrays.asList(_paper2));
            Collections.shuffle(Arrays.asList(_paper3));
            Collections.shuffle(Arrays.asList(_paper4));
        }
*/
        Collections.shuffle(Arrays.asList(englishQ));
        Collections.shuffle(Arrays.asList(_paper2));
        Collections.shuffle(Arrays.asList(_paper3));
        Collections.shuffle(Arrays.asList(_paper4));

        Bundle bundle = getIntent().getExtras();
        paper2 = bundle.getString("paper2");
        paper3 = bundle.getString("paper3");
        paper4 = bundle.getString("paper4");
        examNo = bundle.getString("examNo");
        studentName = bundle.getString("studentName");
        _demo = bundle.getString("demo");

        String $paper2 = "test"+paper2;
        String $paper3 = "test"+paper3;
        String $paper4 = "test"+paper4;

        paper1db = database.getReference().child("testEnglish Language");
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

        initUi();
    }

    private void initUi() {
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

        paper1Count = 10; //60
        paper2Count = 5; //40
        paper3Count = 5; //40
        paper4Count = 5; //40

        getQuestionNo();
        timerStart(10800000);


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
                }//last question
                else if(currentPaper1Count == 10 && currentPaper2Count == 5 && currentPaper3Count == 5 && currentPaper4Count == 5){
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
                        optionA.setChecked(true);
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    String total = String.valueOf(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4);
                    users.child(userId).child("currentExam").child("English Language").setValue(scorePaper1);
                    users.child(userId).child("currentExam").child(paper2).setValue(scorePaper2);
                    users.child(userId).child("currentExam").child(paper3).setValue(scorePaper3);
                    users.child(userId).child("currentExam").child(paper4).setValue(scorePaper4);
                    users.child(userId).child("currentExamTotal").child("total").setValue(scorePaper1 + scorePaper2 + scorePaper3 + scorePaper4);


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

                        optionA.setChecked(true);



                        getQuestionNo();
                    }else if(currentPaper == 2 && selectedAnswer.equals(answer)){
                        scorePaper2 ++;
                        progressBar.setVisibility(View.VISIBLE);

                        optionA.setChecked(true);


                        getQuestionNo();
                    }else if(currentPaper == 3 && selectedAnswer.equals(answer)){
                        scorePaper3 ++;
                        progressBar.setVisibility(View.VISIBLE);

                        optionA.setChecked(true);


                        getQuestionNo();
                    }else if(currentPaper == 4 && selectedAnswer.equals(answer)){
                        scorePaper4 ++;
                        progressBar.setVisibility(View.VISIBLE);

                        optionA.setChecked(true);


                        getQuestionNo();
                    }else{
                        //User failed the question
                        optionA.setChecked(true);

                        progressBar.setVisibility(View.VISIBLE);
                        getQuestionNo();
                    }
                }

            }
        });

    }

    private void getQuestion(final int questionNo, final int paperNo) {
        if(paperNo == 1){
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

                    //number.setText(String.valueOf(currentPaper2Count));
                    question.setText(mquestion);
                    optionA.setText(moptionA);
                    optionB.setText(moptionB);
                    optionC.setText(moptionC);
                    optionD.setText(moptionD);
                    instruction.setText(minstruction);

                    answer = manswer;

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


        if(currentPaper1Count != 10){
            getQuestion(englishQ[0], 1);
            englishQ = removeTheElement(englishQ, 0);
            //removeItems(englishQ, 0);
            currentPaper1Count++;
            return;
        }else if(currentPaper2Count != 5){
            getQuestion(_paper2[0], 2);
            _paper2 = removeTheElement(_paper2, 0);
            //removeItems(_paper2, 0);
            currentPaper2Count++;
            return;
        }else if(currentPaper3Count != 5){
            getQuestion(_paper3[0], 3);
            _paper3 = removeTheElement(_paper3, 0);
            //removeItems(_paper3, 0);
            currentPaper3Count++;
            return;
        }else if(currentPaper4Count != 5){
            getQuestion(_paper4[0], 4);
            _paper4 = removeTheElement(_paper4, 0);
            //removeItems(_paper4, 0);
            currentPaper4Count++;
            return;
        }

        /*if(currentPaper1Count < 10 && !contains(paper1QuestionsArray,questionNoPaper1)){
            currentPaper1Count++;
            paper1QuestionsArray = addElement(paper1QuestionsArray, questionNoPaper1);
            getQuestion(questionNoPaper1, 1);
            return;
        }else if(currentPaper2Count < 5 && !contains(paper2QuestionsArray,questionNoPaper)){
            currentPaper2Count++;
            paper2QuestionsArray = addElement(paper2QuestionsArray, questionNoPaper);
            getQuestion(questionNoPaper, 2);
            return;
        }else if(currentPaper3Count < 5 && !contains(paper3QuestionsArray,questionNoPaper)){
            currentPaper3Count++;
            paper3QuestionsArray = addElement(paper3QuestionsArray, questionNoPaper);
            getQuestion(questionNoPaper, 3);
            return;
        }else if(currentPaper4Count < 5 && !contains(paper4QuestionsArray,questionNoPaper)){
            currentPaper4Count++;
            paper4QuestionsArray = addElement(paper4QuestionsArray, questionNoPaper);
            getQuestion(questionNoPaper, 4);
            return;
        }else{
            getQuestionNo();
        }*/

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
                int minutes = (int) ((milliTillFinish / (1000*60)) % 60);
                int hours   = (int) ((milliTillFinish / (1000*60*60)) % 24);

                String $time = hours + ":" + minutes;
                time.setText($time);
            }

            @Override
            public void onFinish() {
                //time has finished lets tell test taker that his time is up and pass him to the next Intent
            }

        };
        timer.start();
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

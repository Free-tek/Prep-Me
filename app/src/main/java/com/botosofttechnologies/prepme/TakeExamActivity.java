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
import android.widget.CheckBox;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class
TakeExamActivity extends AppCompatActivity {

    TextView candidateName, exam_no, paper1, paper2, paper3, paper4, change_subject, selectSubject, demo;
    Button take_exam, finished;
    CheckBox English, Maths, Physics, Chemistry, Biology, Commerce, Accounting, Government, Economics, Literature, Geography, Agric, History, Crk;
    ImageView account;

    private Typeface header, subheading;
    int checkAccumulator, $count;

    String $paper1, $paper2, $paper3, $paper4 ;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference users = database.getReference().child("users");

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    static FirebaseUser User = mAuth.getCurrentUser();
    static final String userId = User.getUid();

    ProgressBar progressBar;

    String ExamNo, _demo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_exam);

        Bundle bundle = getIntent().getExtras();
        _demo = bundle.getString("demo");
        //check if user has subscribed
        checkSubscription();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void checkSubscription() {

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String msubcribed = String.valueOf(dataSnapshot.child(userId).child("subscription").getValue());
                if(_demo.equals("true")){
                    initUi();
                }else if(msubcribed.equals("false")){

                    AlertDialog alertDialog = new AlertDialog.Builder(TakeExamActivity.this).create();
                    alertDialog.setTitle("Oops...");
                    alertDialog.setMessage("Please you have to subscribe to take this test" );
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Subscribe Now",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(TakeExamActivity.this, PaymentActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(TakeExamActivity.this, NavigationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    alertDialog.show();
                }else{
                    initUi();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initUi() {
        progressBar = (ProgressBar)findViewById(R.id.progress);
        Wave mWave = new Wave();
        mWave.setBounds(0,0,100,100);
        mWave.setColor(R.color.colorPrimaryDark);
        progressBar.setIndeterminateDrawable(mWave);
        progressBar.setVisibility(View.VISIBLE);

        candidateName = (TextView) findViewById(R.id.candidateName);
        exam_no = (TextView) findViewById(R.id.exam_no);
        paper1 = (TextView) findViewById(R.id.paper1);
        paper2 = (TextView) findViewById(R.id.paper2);
        paper3 = (TextView) findViewById(R.id.paper3);
        paper4 = (TextView) findViewById(R.id.paper4);
        change_subject = (TextView) findViewById(R.id.change_subject);
        selectSubject = (TextView) findViewById(R.id.selectSubject);

        demo = (TextView) findViewById(R.id.demo);

        take_exam = (Button) findViewById(R.id.take_exam);
        finished = (Button) findViewById(R.id.finished);

        English = (CheckBox) findViewById(R.id.English);
        Maths = (CheckBox) findViewById(R.id.Maths);
        Physics = (CheckBox) findViewById(R.id.Physics);
        Chemistry = (CheckBox) findViewById(R.id.Chemistry);
        Biology = (CheckBox) findViewById(R.id.Biology);
        Commerce = (CheckBox) findViewById(R.id.Commerce);
        Accounting = (CheckBox) findViewById(R.id.Accounting);
        Government = (CheckBox) findViewById(R.id.Government);
        Economics = (CheckBox) findViewById(R.id.Economics);
        Literature = (CheckBox) findViewById(R.id.Literature);
        Geography = (CheckBox) findViewById(R.id.Geography);
        Agric = (CheckBox) findViewById(R.id.Agric);
        History = (CheckBox) findViewById(R.id.History);
        Crk = (CheckBox) findViewById(R.id.Crk);

        account = (ImageView) findViewById(R.id.account);

        ExamNo = getExamNo();
        exam_no.setText(ExamNo);

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mName = String.valueOf(dataSnapshot.child(userId).child("name").getValue());
                String mpaper1 = String.valueOf(dataSnapshot.child(userId).child("subjects").child("0").getValue());
                String mpaper2 = String.valueOf(dataSnapshot.child(userId).child("subjects").child("1").getValue());
                String mpaper3 = String.valueOf(dataSnapshot.child(userId).child("subjects").child("2").getValue());
                String mpaper4 = String.valueOf(dataSnapshot.child(userId).child("subjects").child("3").getValue());

                candidateName.setText(mName);
                paper1.setText(mpaper1);
                paper2.setText(mpaper2);
                paper3.setText(mpaper3);
                paper4.setText(mpaper4);

                progressBar.setVisibility(View.INVISIBLE);
                take_exam.setVisibility(View.VISIBLE);
                account.setVisibility(View.VISIBLE);
                candidateName.setVisibility(View.VISIBLE);
                exam_no.setVisibility(View.VISIBLE);
                paper1.setVisibility(View.VISIBLE);
                paper2.setVisibility(View.VISIBLE);
                paper3.setVisibility(View.VISIBLE);
                paper4.setVisibility(View.VISIBLE);
                change_subject.setVisibility(View.VISIBLE);

                if(_demo.equals("true")){
                    demo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        header = Typeface.createFromAsset(getAssets(), "fonts/heading.ttf");
        subheading = Typeface.createFromAsset(getAssets(), "fonts/subheading1.ttf");

        selectSubject.setTypeface(subheading);
        candidateName.setTypeface(subheading);
        exam_no.setTypeface(subheading);
        paper1.setTypeface(subheading);
        paper2.setTypeface(subheading);
        paper3.setTypeface(subheading);
        paper4.setTypeface(subheading);
        selectSubject.setTypeface(header);



        English.setTypeface(subheading);
        Maths.setTypeface(subheading);
        Physics.setTypeface(subheading);
        Chemistry.setTypeface(subheading);
        Biology.setTypeface(subheading);
        Commerce.setTypeface(subheading);
        Accounting.setTypeface(subheading);
        Government.setTypeface(subheading);
        Economics.setTypeface(subheading);
        Literature.setTypeface(subheading);
        Geography.setTypeface(subheading);
        Agric.setTypeface(subheading);
        History.setTypeface(subheading);
        Crk.setTypeface(subheading);


        selectSubject.setVisibility(View.INVISIBLE);
        English.setVisibility(View.INVISIBLE);
        Maths.setVisibility(View.INVISIBLE);
        Physics.setVisibility(View.INVISIBLE);
        Chemistry.setVisibility(View.INVISIBLE);
        Biology.setVisibility(View.INVISIBLE);
        Commerce.setVisibility(View.INVISIBLE);
        Accounting.setVisibility(View.INVISIBLE);
        Government.setVisibility(View.INVISIBLE);
        Economics.setVisibility(View.INVISIBLE);
        Literature.setVisibility(View.INVISIBLE);
        Geography.setVisibility(View.INVISIBLE);
        Agric.setVisibility(View.INVISIBLE);
        History.setVisibility(View.INVISIBLE);
        Crk.setVisibility(View.INVISIBLE);
        finished.setVisibility(View.INVISIBLE);





        change_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSubject.setVisibility(View.VISIBLE);
                English.setVisibility(View.VISIBLE);
                Maths.setVisibility(View.VISIBLE);
                Physics.setVisibility(View.VISIBLE);
                Chemistry.setVisibility(View.VISIBLE);
                Biology.setVisibility(View.VISIBLE);
                Commerce.setVisibility(View.VISIBLE);
                Accounting.setVisibility(View.VISIBLE);
                Government.setVisibility(View.VISIBLE);
                Economics.setVisibility(View.VISIBLE);
                Literature.setVisibility(View.VISIBLE);
                Geography.setVisibility(View.VISIBLE);
                Agric.setVisibility(View.VISIBLE);
                History.setVisibility(View.VISIBLE);
                Crk.setVisibility(View.VISIBLE);
                finished.setVisibility(View.VISIBLE);

                take_exam.setVisibility(View.INVISIBLE);
                account.setVisibility(View.INVISIBLE);
                candidateName.setVisibility(View.INVISIBLE);
                exam_no.setVisibility(View.INVISIBLE);
                paper1.setVisibility(View.INVISIBLE);
                paper2.setVisibility(View.INVISIBLE);
                paper3.setVisibility(View.INVISIBLE);
                paper4.setVisibility(View.INVISIBLE);
                change_subject.setVisibility(View.INVISIBLE);


            }
        });


        finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAccumulator = 0;
                countCheck();
                if(!English.isChecked()){
                    Toast.makeText(TakeExamActivity.this, "English is a compulsory subject", Toast.LENGTH_SHORT).show();
                }else if(checkAccumulator != 4){
                    Toast.makeText(TakeExamActivity.this,  "Select only four subject combinations", Toast.LENGTH_SHORT).show();
                }else if (checkAccumulator == 4){
                    getChecked();

                    selectSubject.setVisibility(View.INVISIBLE);
                    English.setVisibility(View.INVISIBLE);
                    Maths.setVisibility(View.INVISIBLE);
                    Physics.setVisibility(View.INVISIBLE);
                    Chemistry.setVisibility(View.INVISIBLE);
                    Biology.setVisibility(View.INVISIBLE);
                    Commerce.setVisibility(View.INVISIBLE);
                    Accounting.setVisibility(View.INVISIBLE);
                    Government.setVisibility(View.INVISIBLE);
                    Economics.setVisibility(View.INVISIBLE);
                    Literature.setVisibility(View.INVISIBLE);
                    Geography.setVisibility(View.INVISIBLE);
                    Agric.setVisibility(View.INVISIBLE);
                    History.setVisibility(View.INVISIBLE);
                    Crk.setVisibility(View.INVISIBLE);
                    finished.setVisibility(View.INVISIBLE);


                    take_exam.setVisibility(View.VISIBLE);
                    account.setVisibility(View.VISIBLE);
                    candidateName.setVisibility(View.VISIBLE);
                    exam_no.setVisibility(View.VISIBLE);
                    paper1.setVisibility(View.VISIBLE);
                    paper2.setVisibility(View.VISIBLE);
                    paper3.setVisibility(View.VISIBLE);
                    paper4.setVisibility(View.VISIBLE);
                    change_subject.setVisibility(View.VISIBLE);

                    paper1.setText($paper1);
                    paper2.setText($paper2);
                    paper3.setText($paper3);
                    paper4.setText($paper4);

                    users.child(userId).child("subjects").child("0").setValue($paper1);
                    users.child(userId).child("subjects").child("1").setValue($paper2);
                    users.child(userId).child("subjects").child("2").setValue($paper3);
                    users.child(userId).child("subjects").child("3").setValue($paper4);


                }
            }
        });


        take_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakeExamActivity.this, ExamActivity.class);

                $paper1 = String.valueOf(paper1.getText());
                $paper2 = String.valueOf(paper2.getText());
                $paper3 = String.valueOf(paper3.getText());
                $paper4 = String.valueOf(paper4.getText());
                String studentName = String.valueOf(candidateName.getText());

                intent.putExtra("paper2", $paper2 );
                intent.putExtra("paper3", $paper3 );
                intent.putExtra("paper4", $paper4 );
                intent.putExtra("examNo", ExamNo );
                intent.putExtra("studentName", studentName );
                intent.putExtra("demo", _demo );

                Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();

                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String date = dateFormat.format(currentDate);
                users.child(userId).child("currentExam").child("date").setValue(date);
                users.child(userId).child("currentExam").child("examNo").setValue(ExamNo);
                users.child(userId).child("currentExam").child("paper1").setValue($paper1);
                users.child(userId).child("currentExam").child("paper2").setValue($paper2);
                users.child(userId).child("currentExam").child("paper3").setValue($paper3);
                users.child(userId).child("currentExam").child("paper4").setValue($paper4);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getChecked() {
        CheckBox[] checkBoxes = {English, Maths, Physics, Chemistry, Biology, Commerce, Accounting, Government, Economics, Literature, Geography, Agric, History, Crk};
        int i = 1;
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                String subject = checkBox.getText().toString();
                if(i == 1){
                    $paper1 = subject;
                    i++;
                }else if(i == 2){
                    $paper2 = subject;
                    i++;
                }else if(i == 3){
                    $paper3 = subject;
                    i++;
                }else if(i == 4){
                    $paper4 = subject;
                    i++;
                }

            }
        }

    }


    private boolean countCheck() {

        CheckBox[] checkBoxes = {English, Maths, Physics, Chemistry, Biology, Commerce, Accounting, Government, Economics, Literature, Geography, Agric, History, Crk};
        boolean isChecked = false;
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                checkAccumulator += 1;
                isChecked = checkAccumulator == 4;
            } else {
                isChecked = false;
            }
        }
        return isChecked;
    }


    public String getExamNo(){
        int number1, number2;
        char a, b, c, d, e, f;
        final String examNo;

        Random rnd = new Random();
        a = (char) (rnd.nextInt(26) + 'A');
        b = (char) (rnd.nextInt(26) + 'A');
        c = (char) (rnd.nextInt(26) + 'A');
        d = (char) (rnd.nextInt(26) + 'A');
        e = (char) (rnd.nextInt(26) + 'A');
        f = (char) (rnd.nextInt(26) + 'A');
        number1 = (int) (10 * Math.random());
        number2 = (int) (10 * Math.random());
        examNo = a + "" + b + "" + c + "" + d + "" + e + "" + f + "" + number1 + "" + number2;

        return examNo;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TakeExamActivity.this, NavigationActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(TakeExamActivity.this, NavigationActivity.class);
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

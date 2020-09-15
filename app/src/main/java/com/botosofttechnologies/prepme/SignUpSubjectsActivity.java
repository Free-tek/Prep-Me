package com.botosofttechnologies.prepme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpSubjectsActivity extends AppCompatActivity {

    CheckBox English, Maths, Physics, Chemistry, Biology, Commerce, Accounting, Government, Economics, Literature, Geography, Agric, History, Crk;
    ImageView back;
    private FirebaseAuth mAuth;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference usersID = database.getReference().child("userId");
    final DatabaseReference leaderboard = database.getReference().child("leader_board");

    private Typeface header, subheading;

    int checkAccumulator, $count, lbCount;
    String email, password, age, surname, firstName, phoneNo;

    ProgressDialog progressDialog;

    Button Done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_subjects);

        Bundle bundle = getIntent().getExtras();
        surname = bundle.getString("surname");
        firstName  = bundle.getString("firstName");
        phoneNo = bundle.getString("phoneNo");
        email = bundle.getString("email");
        password = bundle.getString("password");
        age = bundle.getString("age");



        mAuth = FirebaseAuth.getInstance();
        initUi();

        usersID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                $count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initUi(){

        header = Typeface.createFromAsset(getAssets(), "fonts/heading.ttf");
        subheading = Typeface.createFromAsset(getAssets(), "fonts/subheading1.ttf");

        back = (ImageView) findViewById(R.id.back);

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


        English.setTypeface(header);
        Maths.setTypeface(header);
        Physics.setTypeface(header);
        Chemistry.setTypeface(header);
        Biology.setTypeface(header);
        Commerce.setTypeface(header);
        Accounting.setTypeface(header);
        Government.setTypeface(header);
        Economics.setTypeface(header);
        Literature.setTypeface(header);
        Geography.setTypeface(header);
        Agric.setTypeface(header);
        History.setTypeface(header);
        Crk.setTypeface(header);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpSubjectsActivity.this, SignUpActivity.class);
                intent.putExtra("surname", surname);
                intent.putExtra("firstName", firstName);
                intent.putExtra("phoneNo", phoneNo);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("age", age);
                startActivity(intent);
            }
        });



        Done = (Button) findViewById(R.id.Done);

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAccumulator = 0;
                countCheck();

                if(!English.isChecked()){
                    Toast.makeText(SignUpSubjectsActivity.this, "English is a compulsory subject", Toast.LENGTH_SHORT).show();

                }else if(checkAccumulator != 4){
                    Toast.makeText(SignUpSubjectsActivity.this,  "Select only four subject combinations, don't worry you can always change them", Toast.LENGTH_SHORT).show();
                }else if(!isNetworkAvailable()){
                    Toast.makeText(SignUpSubjectsActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();

                }else{

                    final Intent intentTest = new Intent(SignUpSubjectsActivity.this, NavigationActivity.class);
                    final Intent intent = new Intent(SignUpSubjectsActivity.this, LoginActivity.class);

                    final String $email = String.valueOf(email);
                    final String $password = String.valueOf(password);

                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpSubjectsActivity.this).create();
                    alertDialog.setTitle("Terms and Conditions");
                    alertDialog.setMessage("*By using Prep Me, you agree to abide by the laid down rules for the online tests as delegated from time to time. \n"

                            + "*You agree to the judgement of the panels in announcing the winners for the online test.\n");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Agree",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    progressDialog = new ProgressDialog(SignUpSubjectsActivity.this);
                                    progressDialog.setMessage("Creating Account");
                                    progressDialog.setCanceledOnTouchOutside(true);
                                    progressDialog.show();

                                    mAuth.createUserWithEmailAndPassword($email, $password)
                                            .addOnCompleteListener(SignUpSubjectsActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if (task.isSuccessful()) {

                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        saveData();
                                                        sendEmailVerification();
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SignUpSubjectsActivity.this, "Sign up successful, We sent you a verification email, visit your mail now to complete the sign up process", Toast.LENGTH_SHORT).show();
                                                        startActivity(intent);
                                                        finish();
                                                    }else {
                                                        if (checkEmailExistsOrNot(email)){
                                                            Toast.makeText(SignUpSubjectsActivity.this, "Ooops.. email already exists",
                                                                    Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                        }else{
                                                            progressDialog.dismiss();
                                                            Toast.makeText(SignUpSubjectsActivity.this, "Sign up failed, please try again",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }


                                                    }
                                                }
                                            });

                                }
                            });


                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss",
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


    private void saveData() {
        final String uid = mAuth.getCurrentUser().getUid();
        checkAccumulator = 0;



        final DatabaseReference userId = database.getReference().child("users").child(uid);

        String $surname = String.valueOf(surname);
        String $firstname = String.valueOf(firstName);
        final String $fullName = $surname + " " + $firstname;
        String $email = String.valueOf(email);
        String $phoneNo = String.valueOf(phoneNo);
        String $password = String.valueOf(password);
        String $age = String.valueOf(age);


        userId.child("name").setValue($fullName);
        userId.child("email").setValue($email);
        userId.child("phoneNo").setValue($phoneNo);
        userId.child("password").setValue($password);
        userId.child("age").setValue($age);
        userId.child("subscription").setValue(false);
        userId.child("coins").setValue(0);
        userId.child("remaining_subscription").setValue(0);
        userId.child("lastQuizDate").setValue("2000-02-21");


        leaderboard.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lbCount = (int) dataSnapshot.getChildrenCount();
                lbCount = lbCount + 1;
                leaderboard.child(String.valueOf(lbCount)).child("name").setValue($fullName);
                leaderboard.child(String.valueOf(lbCount)).child("star").setValue("0");


                userId.child("lbKey").setValue(String.valueOf(lbCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        CheckBox[] checkBoxes = {English, Maths, Physics, Chemistry, Biology, Commerce, Accounting, Government, Economics, Literature, Geography, Agric, History, Crk};

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                String subject = checkBox.getText().toString();
                if (subject.equals("English Lang")){
                    userId.child("subjects").child(String.valueOf(checkAccumulator)).setValue("English Language");
                }else{
                    userId.child("subjects").child(String.valueOf(checkAccumulator)).setValue(subject);
                }

                checkAccumulator += 1;
            }
        }


        //lets create a new node on firebase containing each user email as parent and their userID as a child

        usersID.child(String.valueOf($count+1)).child("email").setValue($email);
        usersID.child(String.valueOf($count+1)).child("userId").setValue(uid);

    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(SignUpSubjectsActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpSubjectsActivity.this,
                                    "We sent you a verification email, visit your mail now to complete the sign up process",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpSubjectsActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean checkEmailExistsOrNot(String email){

        final boolean[] result = {true};
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                if (task.getResult().getSignInMethods().size() == 0){
                    result[0] =  false;
                    //email doesnt exist
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        return result[0];

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}

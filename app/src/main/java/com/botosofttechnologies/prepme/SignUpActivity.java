package com.botosofttechnologies.prepme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    TextView /*title, text1 , text2, text3, text4, text5, text6,*/ ageText;
    EditText surname, firstName, email, phoneNo, password, confirmPassword;
    Spinner age;
    Button Done;
    int checkAccumulator, $count, lbCount;
    ImageView back;

    String _email, _password, _age, _surname, _firstName, _phoneNo;

    private FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference usersID = database.getReference().child("userId");
    final DatabaseReference leaderboard = database.getReference().child("leader_board");


    ProgressDialog progressDialog;

    private Typeface header, subheading;

    private ActionBar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Bundle bundle = getIntent().getExtras();
        _surname = bundle.getString("surname");
        _firstName  = bundle.getString("firstName");
        _phoneNo = bundle.getString("phoneNo");
        _email = bundle.getString("email");
        _password = bundle.getString("password");
        _age = bundle.getString("age");

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

    private void initUi() {
        toolbar = getSupportActionBar();



        //title= (TextView) findViewById(R.id.title);
        /*text1= (TextView) findViewById(R.id.text1);
        text2= (TextView) findViewById(R.id.text2);
        text3= (TextView) findViewById(R.id.text3);
        text4= (TextView) findViewById(R.id.text4);
        text5= (TextView) findViewById(R.id.text5);
        text6= (TextView) findViewById(R.id.text6);*/
        ageText= (TextView) findViewById(R.id.ageText);
        back = (ImageView) findViewById(R.id.back);


        header = Typeface.createFromAsset(getAssets(), "fonts/heading.ttf");
        subheading = Typeface.createFromAsset(getAssets(), "fonts/subheading1.ttf");
        //title.setTypeface(header);
       /* text1.setTypeface(header);
        text2.setTypeface(header);
        text3.setTypeface(header);
        text4.setTypeface(header);
        text5.setTypeface(header);
        text6.setTypeface(header);
        ageText.setTypeface(header);*/




        surname = (EditText) findViewById(R.id.surname);
        firstName = (EditText) findViewById(R.id.firstName);
        email = (EditText) findViewById(R.id.email);
        phoneNo = (EditText) findViewById(R.id.phoneNo);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);


        age = (Spinner) findViewById(R.id.age);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });



        Done = (Button) findViewById(R.id.Done);


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(SignUpActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.age));
        dataAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        age.setAdapter(dataAdapter);


        //set values

        surname.setText(_surname);
        firstName.setText(_firstName);
        email.setText(_email);
        password.setText(_password);
        confirmPassword.setText(_password);
        phoneNo.setText(_phoneNo);
        if (_age != null && !(_age.equals(""))){
            int spinnerPosition = dataAdapter.getPosition(_age);
            age.setSelection(spinnerPosition);
        }




        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!validateForm()){
                    Toast.makeText(SignUpActivity.this, "Check the form for error", Toast.LENGTH_SHORT).show();

                }else if(!isNetworkAvailable()){
                    Toast.makeText(SignUpActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }else{

                    mAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.getResult().getSignInMethods().size() == 0){

                                Intent intent = new Intent(SignUpActivity.this, SignUpSubjectsActivity.class);
                                intent.putExtra("surname", surname.getText().toString());
                                intent.putExtra("firstName", firstName.getText().toString());
                                intent.putExtra("phoneNo", phoneNo.getText().toString());
                                intent.putExtra("email", email.getText().toString());
                                intent.putExtra("password", password.getText().toString());
                                intent.putExtra("age", String.valueOf(age.getSelectedItem()));
                                startActivity(intent);

                            }else {

                                Toast.makeText(SignUpActivity.this, "Ooops.. email already exists",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });



                }



                    /*final Intent intent = new Intent(SignUpActivity.this, NavigationActivity.class);

                    final String $email = String.valueOf(email.getText());
                    final String $password = String.valueOf(password.getText());

                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                    alertDialog.setTitle("Terms and Conditions");
                    alertDialog.setMessage("*By using Prep Me, you agree to abide by the laid down rules for the online tests as delegated from time to time. \n"

                            + "*You agree to the judgement of the panels in announcing the winners for the online test.\n");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Agree",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    progressDialog = new ProgressDialog(SignUpActivity.this);
                                    progressDialog.setMessage("Creating Account");
                                    progressDialog.setCanceledOnTouchOutside(true);
                                    progressDialog.show();

                                    mAuth.createUserWithEmailAndPassword($email, $password)
                                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if (task.isSuccessful()) {

                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        saveData();
                                                        sendEmailVerification();
                                                        progressDialog.dismiss();
                                                        startActivity(intent);
                                                        finish();
                                                    }else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SignUpActivity.this, "Sign up failed, please check your network and try again",
                                                                Toast.LENGTH_SHORT).show();
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


                    alertDialog.show();*/



            }
        });

    }

    private boolean validateForm() {
        boolean valid = true;

        String $surname = surname.getText().toString();
        if (TextUtils.isEmpty($surname)) {
            surname.setError("Required.");
            valid = false;
        } else {
            surname.setError(null);
        }


        String $firstName = firstName.getText().toString();
        if (TextUtils.isEmpty($firstName)) {
            firstName.setError("Required.");
            valid = false;
        } else {
            firstName.setError(null);
        }


        String $email = email.getText().toString();
        if (TextUtils.isEmpty($email)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        String $phoneNo = phoneNo.getText().toString();
        if (TextUtils.isEmpty($phoneNo)) {
            phoneNo.setError("Required.");
            valid = false;
        } else {
            phoneNo.setError(null);
        }


        String $password = password.getText().toString();
        if (TextUtils.isEmpty($password)) {
            password.setError("Required.");
            valid = false;
        } else {
            password.setError(null);
        }





        String $confirmPassword = confirmPassword.getText().toString();
        if (TextUtils.isEmpty($confirmPassword)) {
            confirmPassword.setError("Required.");
            valid = false;
        } else {
            confirmPassword.setError(null);
        }

        if($confirmPassword.equals($password)){
            valid = true;
        }else{
            valid = false;
            Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }


        return valid;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}

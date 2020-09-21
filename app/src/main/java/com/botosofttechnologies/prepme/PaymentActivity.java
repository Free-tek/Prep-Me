package com.botosofttechnologies.prepme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class PaymentActivity extends AppCompatActivity {

    EditText surname, firstName, cardNumber, cvv, expiry_date;
    ImageView cardValid, success, t1, t2, t3, t4, t5, back;
    TextView text1, text2, text3, text4, text5, successText;
    Button process;
    ProgressDialog progressDialog;
    private Card card;
    int $count, amount, $remainingTests;
    String userId, $name, $phoneNo;

    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;

    String $subscription;

    final DatabaseReference users = database.getReference().child("users");
    final DatabaseReference payments = database.getReference().child("payments");

    private Typeface header, subheading;

    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        PaystackSdk.initialize(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                $name = String.valueOf(dataSnapshot.child(userId).child("name").getValue());
                $phoneNo = String.valueOf(dataSnapshot.child(userId).child("phoneNo").getValue());
                $subscription = String.valueOf(dataSnapshot.child(userId).child("subscription").getValue());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        initUi();
    }

    private void initUi() {
        toolbar = getSupportActionBar();


        surname = (EditText) findViewById(R.id.surname);
        firstName = (EditText) findViewById(R.id.firstName);
        cardNumber = (EditText) findViewById(R.id.cardNumber);
        cvv = (EditText) findViewById(R.id.cvv);
        expiry_date = (EditText) findViewById(R.id.expiry_date);


        cardValid = (ImageView) findViewById(R.id.cardValid);
        success = (ImageView) findViewById(R.id.success);
        back = (ImageView) findViewById(R.id.back);

        t1 = (ImageView) findViewById(R.id.t1);
        t2 = (ImageView) findViewById(R.id.t2);
        t3 = (ImageView) findViewById(R.id.t3);
        t4 = (ImageView) findViewById(R.id.t4);
        t5 = (ImageView) findViewById(R.id.t5);

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        text4 = (TextView) findViewById(R.id.text4);
        text5 = (TextView) findViewById(R.id.text5);
        successText = (TextView) findViewById(R.id.successText);

        process = (Button) findViewById(R.id.process);


        header = Typeface.createFromAsset(getAssets(), "fonts/heading.ttf");
        subheading = Typeface.createFromAsset(getAssets(), "fonts/subheading1.ttf");
        /*text1.setTypeface(header);
        text2.setTypeface(header);
        text3.setTypeface(header);
        text4.setTypeface(header);
        text5.setTypeface(header);*/
        successText.setTypeface(header);

        success.setVisibility(View.INVISIBLE);
        successText.setVisibility(View.INVISIBLE);
        cardValid.setVisibility(View.INVISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, NavigationActivity.class);
                startActivity(intent);
            }
        });

        process.setText(R.string.check_validity);
        expiry_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String $cardNumber = String.valueOf(cardNumber.getText());
                    int $expiryMonth = Integer.parseInt(String.valueOf(expiry_date.getText()).substring(0,2));
                    int $expiryYear = Integer.parseInt(String.valueOf(expiry_date.getText()).substring(3,5));
                    String $cvv = String.valueOf(cvv.getText());

                    cardValid.setVisibility(View.VISIBLE);
                   card = new Card($cardNumber, $expiryMonth, $expiryYear, $cvv);
                    if(card.isValid()){
                        cardValid.setImageDrawable(getResources().getDrawable(R.drawable.check));
                        process.setVisibility(View.VISIBLE);
                        text1.setVisibility(View.INVISIBLE);
                        text2.setVisibility(View.INVISIBLE);
                        text3.setVisibility(View.INVISIBLE);
                        text4.setVisibility(View.INVISIBLE);
                        text5.setVisibility(View.INVISIBLE);
                    }else{
                        Toast.makeText(PaymentActivity.this, "please check card details and try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public String toString() {
                return "$classname{}";
            }

            @Override
            public void onClick(View v) {
                String $processText = String.valueOf(process.getText());
                if(checkValidity()){
                    if($processText.equalsIgnoreCase("CHECK VALIDITY")){
                        String $cardNumber = String.valueOf(cardNumber.getText());
                        int $expiryMonth = Integer.parseInt(String.valueOf(expiry_date.getText()).substring(0,2));
                        int $expiryYear = Integer.parseInt(String.valueOf(expiry_date.getText()).substring(3,5));
                        String $cvv = String.valueOf(cvv.getText());

                        cardValid.setVisibility(View.VISIBLE);
                        card = new Card($cardNumber, $expiryMonth, $expiryYear, $cvv);
                        if(card.isValid()){
                            cardValid.setBackgroundResource(R.drawable.check);
                            process.setVisibility(View.VISIBLE);
                            process.setText(R.string.process_payment);
                        }else{
                            Toast.makeText(PaymentActivity.this, "please check card details and try again", Toast.LENGTH_SHORT).show();
                        }

                    }else if($processText.equalsIgnoreCase("PROCESS PAYMENT")){
                        AlertDialog alertDialog = new AlertDialog.Builder(PaymentActivity.this).create();
                        alertDialog.setTitle("Proceed");
                        alertDialog.setMessage("Do you want to proceed to pay N2000?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        progressDialog = new ProgressDialog(PaymentActivity.this);
                                        progressDialog.setMessage("Making Transfer...");
                                        progressDialog.setCanceledOnTouchOutside(true);
                                        progressDialog.show();

                                        performCharge();

                                    }

                                });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }

                                });


                        alertDialog.show();

                    }
                }


            }
        });
    }


    private void performCharge() {

        amount = 200000; //coverted to float .00 so charge is 2000
        Charge charge = new Charge();
        charge.setCard(card);
        charge.setEmail("adewole63@gmail.com");
        charge.setAmount(amount);

        PaystackSdk.chargeCard(PaymentActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                success.setVisibility(View.VISIBLE);
                successText.setVisibility(View.VISIBLE);


                surname.setVisibility(View.INVISIBLE);
                firstName.setVisibility(View.INVISIBLE);
                cardNumber.setVisibility(View.INVISIBLE);
                cvv.setVisibility(View.INVISIBLE);
                expiry_date.setVisibility(View.INVISIBLE);

                cardValid.setVisibility(View.INVISIBLE);
                /*text1.setVisibility(View.INVISIBLE);
                text2.setVisibility(View.INVISIBLE);
                text3.setVisibility(View.INVISIBLE);
                text4.setVisibility(View.INVISIBLE);
                text5.setVisibility(View.INVISIBLE);*/

                t1.setVisibility(View.INVISIBLE);
                t2.setVisibility(View.INVISIBLE);
                t3.setVisibility(View.INVISIBLE);
                t4.setVisibility(View.INVISIBLE);
                t5.setVisibility(View.INVISIBLE);

                process.setVisibility(View.INVISIBLE);

                final String $userId = user.getUid();

                Date currentDate = (Date) java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("Africa/Lagos")).getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                final String date = dateFormat.format(currentDate);

                users.child($userId).child("subscription").setValue(true);
                users.child($userId).child("subscription_date").setValue(date);
                users.child($userId).child("subject_change_count").setValue(0);

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        $remainingTests = Integer.parseInt(String.valueOf(dataSnapshot.child(userId).child("remaining_subscription").getValue()));
                        users.child($userId).child("remaining_subscription").setValue($remainingTests + 5);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //


                payments.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        $count = (int) dataSnapshot.getChildrenCount();
                        payments.child(String.valueOf($count + 1)).child("userId").setValue($userId);
                        payments.child(String.valueOf($count + 1)).child("name").setValue($name);
                        payments.child(String.valueOf($count + 1)).child("date").setValue(date);
                        payments.child(String.valueOf($count + 1)).child("amount").setValue(amount);
                        payments.child(String.valueOf($count + 1)).child("phoneNo").setValue($phoneNo);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

            @Override
            public void beforeValidate(Transaction transaction) {

            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Toast.makeText(PaymentActivity.this, "Payment was unsuccessful, check card details and try again", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


    }

    public boolean checkValidity(){
        boolean valid = true;

        if(surname.getText().length() == 0 ){
            Toast.makeText(this, "Surname cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(firstName.getText().length() == 0 )if(surname.getText().length() == 0 ){
            Toast.makeText(this, "Firstname cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(cardNumber.getText().length() != 16 )if(surname.getText().length() == 0 ){
            Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(cvv.getText().length() != 3 ){
            Toast.makeText(this, "Invalid CVV", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(expiry_date.getText().length() != 5){
            Toast.makeText(PaymentActivity.this, "Invalid expiry date use mm/yy format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(String.valueOf(expiry_date.getText().charAt(2)).equals("/")) ){
            Toast.makeText(PaymentActivity.this, "Invalid expiry date use mm/yy format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(String.valueOf(expiry_date.getText()).substring(0,2).matches("[0-9]+"))){
            Toast.makeText(PaymentActivity.this, "Invalid expiry date use mm/yy format", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(String.valueOf(expiry_date.getText()).substring(3,5).matches("[0-9]+"))){
            Toast.makeText(PaymentActivity.this, "Invalid expiry date use mm/yy format", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PaymentActivity.this, NavigationActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(PaymentActivity.this, NavigationActivity.class);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}

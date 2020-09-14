package com.botosofttechnologies.prepme;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class ExamScoreActivity extends AppCompatActivity {

    private String total, paper1, paper2, paper3, paper4, examNo, studentName, scorePaper1, scorePaper2, scorePaper3, scorePaper4, _demo;
    ImageView account;
    TextView candidateName, exam_no, tpaper1, paper1score, tpaper2, paper2score, tpaper3, paper3score, tpaper4, paper4score, ttotal, demo;
    Button saveImage, subscribe;

    private  static final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_score);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        total = bundle.getString("total");
        paper1 = bundle.getString("paper1");
        paper2 = bundle.getString("paper2");
        paper3 = bundle.getString("paper3");
        paper4 = bundle.getString("paper4");
        scorePaper1 = bundle.getString("paper1score");
        scorePaper2 = bundle.getString("paper2score");
        scorePaper3 = bundle.getString("paper3score");
        scorePaper4 = bundle.getString("paper4score");
        examNo = bundle.getString("ExamNo");
        studentName = bundle.getString("studentName");
        _demo = bundle.getString("demo");

        initUi();

    }

    private void initUi() {
        candidateName = (TextView) findViewById(R.id.candidateName);
        exam_no = (TextView) findViewById(R.id.exam_no);
        tpaper1 = (TextView) findViewById(R.id.paper1);
        paper1score = (TextView) findViewById(R.id.paper1score);
        tpaper2 = (TextView) findViewById(R.id.paper2);
        paper2score = (TextView) findViewById(R.id.paper2score);
        tpaper3 = (TextView) findViewById(R.id.paper3);
        paper3score = (TextView) findViewById(R.id.paper3score);
        tpaper4 = (TextView) findViewById(R.id.paper4);
        paper4score = (TextView) findViewById(R.id.paper4score);
        ttotal = (TextView) findViewById(R.id.total);

        demo = (TextView) findViewById(R.id.demo);
        saveImage = (Button) findViewById(R.id.saveImage);
        subscribe = (Button) findViewById(R.id.subscribe);

        if(_demo.equals("true")){
            demo.setVisibility(View.VISIBLE);
            subscribe.setVisibility(View.VISIBLE);
        }

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExamScoreActivity.this, PaymentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Log.d("TAGGGG",  + "2:" + scorePaper2 + "3:" + scorePaper3);

        //Log.d("TAGGGG",  scorePaper1 + "2:" + scorePaper2 + "3:" + scorePaper3);
        float scoree1 = (Integer.parseInt(scorePaper1));
        float scoree2 = (Integer.parseInt(scorePaper2));
        float scoree3 = (Integer.parseInt(scorePaper3));
        float scoree4 = (Integer.parseInt(scorePaper4));
        float totale = (Integer.parseInt(total));
        Log.d("TAGGGG",    scorePaper1 + " 2:" + scorePaper2 + " 3:" + scorePaper3 + " 4:" + scorePaper4 + " total:" + total);

        candidateName.setText(studentName);
        exam_no.setText(examNo);
        tpaper1.setText("English Language");
        paper1score.setText(String.valueOf((int) ((scoree1/10)*100)));
        tpaper2.setText(paper2);
        paper2score.setText(String.valueOf((int) ((scoree2/5)*100)));
        tpaper3.setText(paper3);
        paper3score.setText(String.valueOf((int) ((scoree3/5)*100)));
        tpaper4.setText(paper4);
        paper4score.setText(String.valueOf((int) ((scoree4/5)*100)));
        String _total = (String.valueOf((int) ((totale/25)*100))) + "%";
        ttotal.setText(_total);

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeScreenshot();
            }
        });
    }


    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        //handle if app user doesnt give permission to access gallery
        if(ActivityCompat.checkSelfPermission(ExamScoreActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(ExamScoreActivity.this, "We need some permission to save this image", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },PERMISSION_REQUEST_CODE);
                return;
            }

        }else{

            try {
                // image naming and path  to include sd card  appending name you choose for file
                String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

                // create bitmap screen capture
                View v1 = getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                File imageFile = new File(mPath);

                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();

                //openScreenshot(imageFile);
                Toast.makeText(this, "image saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (Throwable e) {
                // Several error may come out with file handling or DOM
                e.printStackTrace();
            }

        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:
            {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }

            break;
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ExamScoreActivity.this, NavigationActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                Intent intent = new Intent(ExamScoreActivity.this, NavigationActivity.class);
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

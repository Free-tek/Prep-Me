package com.botosofttechnologies.prepme;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView logo;
    TextView title;

    private Typeface header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initUi();

        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                       Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                       startActivity(intent);
                    }
                },
                2000);

    }

    private void initUi() {

        title = (TextView) findViewById(R.id.title);

        header = Typeface.createFromAsset(getAssets(), "fonts/heading.ttf");
        title.setTypeface(header);


    }

}

package com.example.administrator.countdowntextview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.countdowntextviewlib.CountDownTextView;

public class MainActivity extends AppCompatActivity {

    private CountDownTextView timer;
    private EditText etTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etTime = (EditText) findViewById(R.id.et);
        timer = (CountDownTextView) findViewById(R.id.tv);
        timer.startCountDown(9);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.setCurrentSeconds(20);
            }
        });
    }

    public void onStopTime(View view) {
        timer.stopCountDown();
    }

    public void onStartTime(View view) {
        int seconds = 10;
        try {
            seconds = Integer.parseInt(etTime.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timer.isActive()) {
            timer.setCurrentSeconds(seconds);
        } else {
            timer.startCountDown(seconds);
        }
    }
}

package com.murrays.aiderv1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CalenderEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_events);
        configureEventstoCalendarButton();
    }
private void configureEventstoCalendarButton(){
        Button buttontoCalender = (Button) findViewById(R.id.buttontoCalender);
        buttontoCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
}

}
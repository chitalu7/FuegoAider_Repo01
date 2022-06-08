package com.murrays.aiderv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        configureCalenderEventsButton();

    }

    private void configureCalenderEventsButton() {
        Button buttoncalEvents = (Button) findViewById(R.id.buttoncalEvents);
        buttoncalEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CalendarActivity.this, CalenderEventsActivity.class));
            }


        });
    }
}
package com.murrays.aiderv1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_day);
        TextView tv = (TextView) findViewById(R.id.date_data);
        Intent intent = getIntent();
        String data = intent.getStringExtra("date");
        tv.setText(data);
    }
}
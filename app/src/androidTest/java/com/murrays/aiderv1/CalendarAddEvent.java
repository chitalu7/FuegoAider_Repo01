package com.murrays.aiderv1;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;
import org.junit.Test;

public class CalendarAddEvent {
    @Rule
    public ActivityScenarioRule<CalendarAddActivity> activityRule =
            new ActivityScenarioRule<>(CalendarAddActivity.class);
    @Test
    public void addEvent() {
        Espresso.onView(withId(R.id.event_date)).perform(typeText("07072022"));
        Espresso.onView(withId(R.id.event_time)).perform(typeText("0630"));
        Espresso.onView(withId(R.id.descript_textbox)).perform(typeText("party"));
        Espresso.onView(withId(R.id.notes_textbox)).perform(typeText("grad party"));
        Espresso.onView(withId(R.id.add_event_button)).perform(click());
    }
}

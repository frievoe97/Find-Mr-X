package com.friedrichvoelkers.mobbs_layout.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.friedrichvoelkers.mobbs_layout.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StartScreenTest {

    @Rule
    public ActivityTestRule<StartScreen> startScreenActivityTestRule = new ActivityTestRule<StartScreen>(StartScreen.class);

    @Test
    public void checkIfGameIdInputIsDisplayed() {
        onView(withId(R.id.edit_text_startscreen_game_id)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfUsernameInputIsDisplayed() {
        onView(withId(R.id.edit_text_startscreen_username_id)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfCreateGameButtonIsDisplayed() {
        onView(withId(R.id.button_startscreen_create_game)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfJoinGameButtonIsDisplayed() {
        onView(withId(R.id.button_startscreen_create_game)).check(matches(isDisplayed()));
    }
}

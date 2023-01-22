package com.friedrichvoelkers.mobbs_layout.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.friedrichvoelkers.mobbs_layout.R;
import com.friedrichvoelkers.mobbs_layout.model.GameData;
import com.friedrichvoelkers.mobbs_layout.model.GlobalState;
import com.friedrichvoelkers.mobbs_layout.model.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith (AndroidJUnit4.class)
public class SettingsScreenTest {

    @Before
    public void setData () {
        GlobalState.getInstance().setMyUserData(new User(true, 10000, "friedrichs-iphone", "friedrich"));
        GlobalState.getInstance().addUser(new User(true, 10000, "friedrich", "Friedrich"));
        GlobalState.getInstance().addUser(new User(false, 10001, "martin", "Martin"));
        GlobalState.getInstance().addUser(new User(false, 10002, "tobias", "Tobias"));
        GlobalState.getInstance().addUser(new User(false, 10003, "sabine", "Sabine"));
        GlobalState.getInstance().setGameData(new GameData());
        GlobalState.getInstance().getGameData().setGameId(676732);
        settingActivityTestRule.launchActivity(null);
    }

    @Rule
    public ActivityTestRule<SettingsScreen> settingActivityTestRule =
            new ActivityTestRule<SettingsScreen>(SettingsScreen.class, false, false);

    @Test
    public void checkIfGameDurationTextIsDisplayed () {
        onView(withId(R.id.text_setting_view_game_duration)).check(matches(isDisplayed()));
        onView(withId(R.id.text_setting_view_game_duration)).check(matches(isClickable()));
    }

    @Test
    public void checkIfMrXIntervalTextIsDisplayed () {
        onView(withId(R.id.text_setting_view_mr_x_interval)).check(matches(isDisplayed()));
        onView(withId(R.id.text_setting_view_mr_x_interval)).check(matches(isClickable()));
    }

    @Test
    public void checkIfMrXTextIsDisplayed () {
        onView(withId(R.id.text_setting_mr_x)).check(matches(isDisplayed()));
        onView(withId(R.id.text_setting_mr_x)).check(matches(isClickable()));
    }

    @Test
    public void checkIfNumberPickerIsDisplayed () {
        onView(withId(R.id.number_picker_setting_view)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfStartButtonIsDisplayed () {
        onView(withId(R.id.button_start_setting_view)).check(matches(isDisplayed()));
        onView(withId(R.id.button_start_setting_view)).check(matches(isClickable()));
    }

}


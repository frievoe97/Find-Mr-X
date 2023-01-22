package com.friedrichvoelkers.mobbs_layout.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.CoreMatchers.not;

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
public class JoiningScreenTest {

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
    public ActivityTestRule<JoiningScreen> settingActivityTestRule =
            new ActivityTestRule<JoiningScreen>(JoiningScreen.class, false, false);

    @Test
    public void checkIfHeaderIsDisplayed () {
        onView(withId(R.id.text_loading_view_header)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfRecyclerViewerIsDisplayed () {
        onView(withId(R.id.joining_screen_recycler_view)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkIfStartButtonIsDisplayed () {
        onView(withId(R.id.joining_screen_start_game)).check(matches(isDisplayed()));
        onView(withId(R.id.joining_screen_start_game)).check(matches(not(isEnabled())));
    }
}


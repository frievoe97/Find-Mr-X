package com.friedrichvoelkers.mobbs_layout.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
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
public class ChatScreenTest {

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
    public ActivityTestRule<ChatScreen> settingActivityTestRule =
            new ActivityTestRule<ChatScreen>(ChatScreen.class, false, false);

    @Test
    public void checkIfBackButtonIsDisplayed () {
        onView(withId(R.id.button_chat_view_send_back)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfTextInputIsDisplayed () {
        onView(withId(R.id.edittext_chat_view_message)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfSendButtonIsDisplayed () {
        onView(withId(R.id.button_chat_view_send)).check(matches(isDisplayed()));
    }



}


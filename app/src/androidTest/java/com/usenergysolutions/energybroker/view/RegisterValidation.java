package com.usenergysolutions.energybroker.view;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.usenergysolutions.energybroker.R;
import com.usenergysolutions.energybroker.utils.ToastMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterValidation {
    @Rule
    public ActivityTestRule<RegistrationActivity> activityRule = new ActivityTestRule<>(RegistrationActivity.class);
    private String password = "password";
    private String conPassword = "conPassword";

    @Test
    public void changeText_sameActivity() {
        // Type text and then press the button.
        onView(withId(R.id.registrationPasswordEditText))
                .perform(typeText(password));
        onView(withId(R.id.registrationConfirmPasswordEditText))
                .perform(typeText(conPassword)).perform(closeSoftKeyboard());
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //onView(withId(R.id.registrationConfirmPasswordEditText)).perform(typeText(conPassword));
        onView(withId(R.id.registrationCreateAccountButton)).perform(click());

        // Check that the text was changed.
        onView(withText(R.string.registration_password_not_equals_msg)).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}

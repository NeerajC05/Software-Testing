package com.pdiot.harty


import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdiot.harty.profile.ProfileActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AuthenticationTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ProfileActivity::class.java, true, false)

    private var emailString = "test@test.com"
    private var password = "Test123"

    @Test
    fun logInSuccessTest() {
        mActivityTestRule.launchActivity(Intent())
        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 0)))
        emailText.perform(scrollTo(), replaceText("test@test.com"), closeSoftKeyboard())

        val passwordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
        passwordText.perform(scrollTo(), replaceText("Test123"), closeSoftKeyboard())

        val loginButton2 = onView(allOf(withId(R.id.loginButton), withText("Login"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        loginButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val welcomeText = onView(allOf(withId(R.id.welcomeMessage), withText("Hi, Test Mctesty!"), withParent(withParent(withId(R.id.welcomeMessageCard))), isDisplayed()))
        welcomeText.check(matches(withText("Hi, Test Mctesty!")))

        onView(withId(R.id.login_button)).check(matches(isClickable()))
        onView(withId(R.id.login_button)).check(matches(isEnabled()))

        onView(withId(R.id.change_email_button)).check(matches(isClickable()))
        onView(withId(R.id.change_email_button)).check(matches(isEnabled()))

        onView(withId(R.id.change_password_button)).check(matches(isClickable()))
        onView(withId(R.id.change_password_button)).check(matches(isEnabled()))

        onView(withId(R.id.view_data_button)).check(matches(isClickable()))
        onView(withId(R.id.view_data_button)).check(matches(isEnabled()))

        onView(withId(R.id.delete_account_button)).check(matches(isClickable()))
        onView(withId(R.id.delete_account_button)).check(matches(isEnabled()))

        mActivityTestRule.finishActivity()
        Firebase.auth.signOut()
    }

    @Test
    fun logOutSuccessTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(emailString, password)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            Firebase.auth.signOut()

            val welcomeText = onView(allOf(withId(R.id.welcomeMessage), withText("Hi, Test Mctesty!"), withParent(withParent(withId(R.id.welcomeMessageCard))), isDisplayed()))
            welcomeText.check(matches(withText("Hi, Test Mctesty!")))

            onView(withId(R.id.login_button)).check(matches(isClickable()))
            onView(withId(R.id.login_button)).check(matches(isEnabled()))

            onView(withId(R.id.change_email_button)).check(matches(isClickable()))
            onView(withId(R.id.change_email_button)).check(matches(isEnabled()))

            onView(withId(R.id.change_password_button)).check(matches(isClickable()))
            onView(withId(R.id.change_password_button)).check(matches(isEnabled()))

            onView(withId(R.id.view_data_button)).check(matches(isClickable()))
            onView(withId(R.id.view_data_button)).check(matches(isEnabled()))

            onView(withId(R.id.delete_account_button)).check(matches(isClickable()))
            onView(withId(R.id.delete_account_button)).check(matches(isEnabled()))

            mActivityTestRule.finishActivity()
        } else {
            Assert.fail()
        }
    }

    @Test
    fun authenticationFailWrongEmailTest() {
        mActivityTestRule.launchActivity(Intent())
        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 0)))
        emailText.perform(scrollTo(), replaceText("wrong.test@test.com"), closeSoftKeyboard())

        val passwordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
        passwordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

        val loginButton2 = onView(allOf(withId(R.id.loginButton), withText("Login"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        loginButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val loginTitle = onView(allOf(withId(R.id.title), withText("Login"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        loginTitle.check(matches(withText("Login")))
        mActivityTestRule.finishActivity()
    }

    @Test
    fun authenticationFailWrongPasswordTest() {
        mActivityTestRule.launchActivity(Intent())
        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 0)))
        emailText.perform(scrollTo(), replaceText(emailString), closeSoftKeyboard())

        val passwordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
        passwordText.perform(scrollTo(), replaceText("WrongTest123"), closeSoftKeyboard())

        val loginButton2 = onView(allOf(withId(R.id.loginButton), withText("Login"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        loginButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val loginTitle = onView(allOf(withId(R.id.title), withText("Login"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        loginTitle.check(matches(withText("Login")))
        mActivityTestRule.finishActivity()
    }

    @Test
    fun authenticationFailEmptyEmailTest() {
        mActivityTestRule.launchActivity(Intent())
        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val passwordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
        passwordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

        val loginButton2 = onView(allOf(withId(R.id.loginButton), withText("Login"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        loginButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val loginTitle = onView(allOf(withId(R.id.title), withText("Login"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        loginTitle.check(matches(withText("Login")))
        mActivityTestRule.finishActivity()
    }

    @Test
    fun authenticationFailEmptyPasswordTest() {
        mActivityTestRule.launchActivity(Intent())
        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 0)))
        emailText.perform(scrollTo(), replaceText(emailString), closeSoftKeyboard())

        val loginButton2 = onView(allOf(withId(R.id.loginButton), withText("Login"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        loginButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val loginTitle = onView(allOf(withId(R.id.title), withText("Login"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        loginTitle.check(matches(withText("Login")))
        mActivityTestRule.finishActivity()
    }

    @Test
    fun authenticationFailEmptyFieldsTest() {
        mActivityTestRule.launchActivity(Intent())
        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val loginButton2 = onView(allOf(withId(R.id.loginButton), withText("Login"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        loginButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val loginTitle = onView(allOf(withId(R.id.title), withText("Login"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        loginTitle.check(matches(withText("Login")))
        mActivityTestRule.finishActivity()
    }


    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}

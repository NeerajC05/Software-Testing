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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdiot.harty.profile.ProfileActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class AccountCreationTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ProfileActivity::class.java, true, false)

    private var emailString = "test1@test.com"
    private var password = "Test123"

    @Test
    fun createAccountSuccessTest() {
        mActivityTestRule.launchActivity(Intent())

        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val signUpButton = onView(allOf(withId(R.id.signUpButton), withText("Sign up"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
        signUpButton.perform(scrollTo(), click())

        val nameText = onView(allOf(withId(R.id.name), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 0)))
        nameText.perform(scrollTo(), replaceText("Test Subject"), closeSoftKeyboard())

        val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
        emailText.perform(scrollTo(), replaceText(emailString), closeSoftKeyboard())

        val passwordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        passwordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

        val signUpButton2 = onView(allOf(withId(R.id.signUpButton), withText("Sign Up "), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
        signUpButton2.perform(scrollTo(), click())

        Thread.sleep(1000)
        mActivityTestRule.finishActivity()

        val login = Firebase.auth.signInWithEmailAndPassword(emailString, password)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser!!.delete()
        } else {
            Assert.fail()
        }
    }

    @Test
    fun createAccountFailTest() {
        mActivityTestRule.launchActivity(Intent())

        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val signUpButton = onView(allOf(withId(R.id.signUpButton), withText("Sign up"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
        signUpButton.perform(scrollTo(), click())

        val nameText = onView(allOf(withId(R.id.name), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 0)))
        nameText.perform(scrollTo(), replaceText("Test Subject"), closeSoftKeyboard())

        val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
        emailText.perform(scrollTo(), replaceText("test.exist@test.com"), closeSoftKeyboard())

        val passwordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        passwordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

        val signUpButton2 = onView(allOf(withId(R.id.signUpButton), withText("Sign Up "), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
        signUpButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val signUpTitle = onView(allOf(withId(R.id.title), withText("Sign Up"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        signUpTitle.check(matches(withText("Sign Up")))

        mActivityTestRule.finishActivity()
    }

    @Test
    fun createAccountFailEmptyNameTest() {
        mActivityTestRule.launchActivity(Intent())

        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val signUpButton = onView(allOf(withId(R.id.signUpButton), withText("Sign up"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
        signUpButton.perform(scrollTo(), click())

        val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
        emailText.perform(scrollTo(), replaceText("test.exist@test.com"), closeSoftKeyboard())

        val passwordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        passwordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

        val signUpButton2 = onView(allOf(withId(R.id.signUpButton), withText("Sign Up "), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
        signUpButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val signUpTitle = onView(allOf(withId(R.id.title), withText("Sign Up"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        signUpTitle.check(matches(withText("Sign Up")))

        mActivityTestRule.finishActivity()
    }

    @Test
    fun createAccountFailEmptyEmailTest() {
        mActivityTestRule.launchActivity(Intent())

        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val signUpButton = onView(allOf(withId(R.id.signUpButton), withText("Sign up"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
        signUpButton.perform(scrollTo(), click())

        val nameText = onView(allOf(withId(R.id.name), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 0)))
        nameText.perform(scrollTo(), replaceText("Test Subject"), closeSoftKeyboard())

        val passwordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
        passwordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

        val signUpButton2 = onView(allOf(withId(R.id.signUpButton), withText("Sign Up "), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
        signUpButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val signUpTitle = onView(allOf(withId(R.id.title), withText("Sign Up"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        signUpTitle.check(matches(withText("Sign Up")))

        mActivityTestRule.finishActivity()
    }

    @Test
    fun createAccountFailEmptyPasswordTest() {
        mActivityTestRule.launchActivity(Intent())

        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val signUpButton = onView(allOf(withId(R.id.signUpButton), withText("Sign up"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
        signUpButton.perform(scrollTo(), click())

        val nameText = onView(allOf(withId(R.id.name), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 0)))
        nameText.perform(scrollTo(), replaceText("Test Subject"), closeSoftKeyboard())

        val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
        emailText.perform(scrollTo(), replaceText("test.exist@test.com"), closeSoftKeyboard())

        val signUpButton2 = onView(allOf(withId(R.id.signUpButton), withText("Sign Up "), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
        signUpButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val signUpTitle = onView(allOf(withId(R.id.title), withText("Sign Up"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        signUpTitle.check(matches(withText("Sign Up")))

        mActivityTestRule.finishActivity()
    }

    @Test
    fun createAccountFailEmptyFieldsTest() {
        mActivityTestRule.launchActivity(Intent())

        val loginButton = onView(allOf(withId(R.id.login_button), withText("Login"), childAtPosition(childAtPosition(withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")), 2), 0), isDisplayed()))
        loginButton.perform(click())

        val signUpButton = onView(allOf(withId(R.id.signUpButton), withText("Sign up"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
        signUpButton.perform(scrollTo(), click())

        val signUpButton2 = onView(allOf(withId(R.id.signUpButton), withText("Sign Up "), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
        signUpButton2.perform(scrollTo(), click())

        Thread.sleep(1000)

        val signUpTitle = onView(allOf(withId(R.id.title), withText("Sign Up"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
        signUpTitle.check(matches(withText("Sign Up")))

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

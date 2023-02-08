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
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ModifyEmailTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ProfileActivity::class.java, true, false)

    private var oldEmailString = "test@test.com"
    private var newEmailString = "test@test.co.uk"
    private var password = "Test123"

    @Test
    fun modifyEmailSuccessTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(oldEmailString, password)
        Thread.sleep(1000)
        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_email_button)).check(matches(isClickable()))
            onView(withId(R.id.change_email_button)).check(matches(isEnabled()))

            val changeEmailButton = onView(allOf(withId(R.id.change_email_button), withText("Change Email Address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            changeEmailButton.perform(scrollTo(), click())

            val newEmailText = onView(allOf(withId(R.id.newEmail), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            newEmailText.perform(scrollTo(), replaceText(newEmailString), closeSoftKeyboard())

            val confirmPasswordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            confirmPasswordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

            val changeEmailButton2 = onView(allOf(withId(R.id.changeEmailButton), withText("Change My email address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changeEmailButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val profileText = onView(allOf(withId(R.id.title), withText("Profile"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
            profileText.check(matches(withText("Profile")))

            val changeEmailButton3 = onView(allOf(withId(R.id.change_email_button), withText("Change Email Address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            changeEmailButton3.perform(scrollTo(), click())

            val currentEmailText = onView(allOf(withId(R.id.currentEmail), withText("Current email: test@test.co.uk"), withParent(withParent(withId(R.id.scrollable))), isDisplayed()))
            currentEmailText.check(matches(withText("Current email: test@test.co.uk")))

            val currentUser = FirebaseAuth.getInstance().currentUser
            Assert.assertTrue(currentUser!!.email == newEmailString)
            currentUser.updateEmail(oldEmailString)
            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }

    }

    @Test
    fun modifyEmailFailTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(oldEmailString, password)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_email_button)).check(matches(isClickable()))
            onView(withId(R.id.change_email_button)).check(matches(isEnabled()))

            val changeEmailButton = onView(allOf(withId(R.id.change_email_button), withText("Change Email Address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            changeEmailButton.perform(scrollTo(), click())

            val newEmailText = onView(allOf(withId(R.id.newEmail), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            newEmailText.perform(scrollTo(), replaceText(newEmailString), closeSoftKeyboard())

            val confirmPasswordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            confirmPasswordText.perform(scrollTo(), replaceText("Test12"), closeSoftKeyboard())

            val changeEmailButton2 = onView(allOf(withId(R.id.changeEmailButton), withText("Change My email address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changeEmailButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val currentEmailText = onView(allOf(withId(R.id.currentEmail), withText("Current email: test@test.com"), withParent(withParent(withId(R.id.scrollable))), isDisplayed()))
            currentEmailText.check(matches(withText("Current email: test@test.com")))

            val currentUser = FirebaseAuth.getInstance().currentUser
            Assert.assertTrue(currentUser!!.email == oldEmailString)
            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }
    }

    @Test
    fun modifyEmailFailDuplicateEmailTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(oldEmailString, password)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_email_button)).check(matches(isClickable()))
            onView(withId(R.id.change_email_button)).check(matches(isEnabled()))

            val changeEmailButton = onView(allOf(withId(R.id.change_email_button), withText("Change Email Address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            changeEmailButton.perform(scrollTo(), click())

            val newEmailText = onView(allOf(withId(R.id.newEmail), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            newEmailText.perform(scrollTo(), replaceText(oldEmailString), closeSoftKeyboard())

            val confirmPasswordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            confirmPasswordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

            val changeEmailButton2 = onView(allOf(withId(R.id.changeEmailButton), withText("Change My email address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changeEmailButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val currentEmailText = onView(allOf(withId(R.id.currentEmail), withText("Current email: test@test.com"), withParent(withParent(withId(R.id.scrollable))), isDisplayed()))
            currentEmailText.check(matches(withText("Current email: test@test.com")))

            val currentUser = FirebaseAuth.getInstance().currentUser
            Assert.assertTrue(currentUser!!.email == oldEmailString)
            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }

    }

    @Test
    fun modifyEmailFailExistingEmailTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(oldEmailString, password)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_email_button)).check(matches(isClickable()))
            onView(withId(R.id.change_email_button)).check(matches(isEnabled()))

            val changeEmailButton = onView(allOf(withId(R.id.change_email_button), withText("Change Email Address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            changeEmailButton.perform(scrollTo(), click())

            val newEmailText = onView(allOf(withId(R.id.newEmail), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            newEmailText.perform(scrollTo(), replaceText("test.exist@test.com"), closeSoftKeyboard())

            val confirmPasswordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            confirmPasswordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

            val changeEmailButton2 = onView(allOf(withId(R.id.changeEmailButton), withText("Change My email address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changeEmailButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val currentEmailText = onView(allOf(withId(R.id.currentEmail), withText("Current email: test@test.com"), withParent(withParent(withId(R.id.scrollable))), isDisplayed()))
            currentEmailText.check(matches(withText("Current email: test@test.com")))

            val currentUser = FirebaseAuth.getInstance().currentUser
            Assert.assertTrue(currentUser!!.email == oldEmailString)
            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }

    }

    @Test
    fun modifyEmailEmptyEmailTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(oldEmailString, password)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_email_button)).check(matches(isClickable()))
            onView(withId(R.id.change_email_button)).check(matches(isEnabled()))

            val changeEmailButton = onView(allOf(withId(R.id.change_email_button), withText("Change Email Address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            changeEmailButton.perform(scrollTo(), click())

            val confirmPasswordText = onView(allOf(withId(R.id.passwordInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            confirmPasswordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

            val changeEmailButton2 = onView(allOf(withId(R.id.changeEmailButton), withText("Change My email address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changeEmailButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val currentEmailText = onView(allOf(withId(R.id.currentEmail), withText("Current email: test@test.com"), withParent(withParent(withId(R.id.scrollable))), isDisplayed()))
            currentEmailText.check(matches(withText("Current email: test@test.com")))

            val currentUser = FirebaseAuth.getInstance().currentUser
            Assert.assertTrue(currentUser!!.email == oldEmailString)
            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }
    }

    @Test
    fun modifyEmailEmptyPasswordTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(oldEmailString, password)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_email_button)).check(matches(isClickable()))
            onView(withId(R.id.change_email_button)).check(matches(isEnabled()))

            val changeEmailButton = onView(allOf(withId(R.id.change_email_button), withText("Change Email Address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            changeEmailButton.perform(scrollTo(), click())

            val newEmailText = onView(allOf(withId(R.id.newEmail), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            newEmailText.perform(scrollTo(), replaceText(newEmailString), closeSoftKeyboard())

            val changeEmailButton2 = onView(allOf(withId(R.id.changeEmailButton), withText("Change My email address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changeEmailButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val currentEmailText = onView(allOf(withId(R.id.currentEmail), withText("Current email: test@test.com"), withParent(withParent(withId(R.id.scrollable))), isDisplayed()))
            currentEmailText.check(matches(withText("Current email: test@test.com")))

            val currentUser = FirebaseAuth.getInstance().currentUser
            Assert.assertTrue(currentUser!!.email == oldEmailString)
            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }

    }

    @Test
    fun modifyEmailEmptyFieldsTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(oldEmailString, password)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_email_button)).check(matches(isClickable()))
            onView(withId(R.id.change_email_button)).check(matches(isEnabled()))

            val changeEmailButton = onView(allOf(withId(R.id.change_email_button), withText("Change Email Address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            changeEmailButton.perform(scrollTo(), click())

            Thread.sleep(1000)

            val changeEmailButton2 = onView(allOf(withId(R.id.changeEmailButton), withText("Change My email address"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changeEmailButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val currentEmailText = onView(allOf(withId(R.id.currentEmail), withText("Current email: test@test.com"), withParent(withParent(withId(R.id.scrollable))), isDisplayed()))
            currentEmailText.check(matches(withText("Current email: test@test.com")))

            val currentUser = FirebaseAuth.getInstance().currentUser
            Assert.assertTrue(currentUser!!.email == oldEmailString)
            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }
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

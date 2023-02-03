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
class ModifyPasswordTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ProfileActivity::class.java, true, false)

    private var emailString = "test@test.com"
    private var oldPassword = "Test123"
    private var newPassword = "NewTest123"

    @Test
    fun modifyPasswordSuccessTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(emailString, oldPassword)
        Thread.sleep(1000)
        mActivityTestRule.launchActivity(Intent())
        if (login.isSuccessful) {
            onView(withId(R.id.change_password_button)).check(matches(isClickable()))
            onView(withId(R.id.change_password_button)).check(matches(isEnabled()))

            val changePasswordButton = onView(allOf(withId(R.id.change_password_button), withText("Change Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            changePasswordButton.perform(scrollTo(), click())

            val oldPasswordText = onView(allOf(withId(R.id.oldPassword), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            oldPasswordText.perform(scrollTo(), replaceText(oldPassword), closeSoftKeyboard())

            val newPasswordText = onView(allOf(withId(R.id.newPassword), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            newPasswordText.perform(scrollTo(), replaceText(newPassword), closeSoftKeyboard())

            val changePasswordButton2 = onView(allOf(withId(R.id.changePasswordButton), withText("Change My Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changePasswordButton2.perform(scrollTo(), click())
            Thread.sleep(1000)
            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
            val newLogin = Firebase.auth.signInWithEmailAndPassword(emailString, newPassword)
            Thread.sleep(1000)
            if (newLogin.isSuccessful) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                currentUser!!.updatePassword(oldPassword)
                Firebase.auth.signOut()
            } else {
                Assert.fail()
            }
        } else {
            Assert.fail()
        }
    }

    @Test
    fun modifyPasswordFailShortPasswordTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(emailString, oldPassword)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_password_button)).check(matches(isClickable()))
            onView(withId(R.id.change_password_button)).check(matches(isEnabled()))

            val changePasswordButton = onView(allOf(withId(R.id.change_password_button), withText("Change Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            changePasswordButton.perform(scrollTo(), click())

            val oldPasswordText = onView(allOf(withId(R.id.oldPassword), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            oldPasswordText.perform(scrollTo(), replaceText(oldPassword), closeSoftKeyboard())

            val newPasswordText = onView(allOf(withId(R.id.newPassword), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            newPasswordText.perform(scrollTo(), replaceText("Test1"), closeSoftKeyboard())

            val changePasswordButton2 = onView(allOf(withId(R.id.changePasswordButton), withText("Change My Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changePasswordButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val passwordPage = onView(allOf(withId(R.id.title), withText("Change your Password"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
            passwordPage.check(matches(withText("Change your Password")))

            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }
    }

    @Test
    fun modifyPasswordFailTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(emailString, oldPassword)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_password_button)).check(matches(isClickable()))
            onView(withId(R.id.change_password_button)).check(matches(isEnabled()))

            val changePasswordButton = onView(allOf(withId(R.id.change_password_button), withText("Change Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            changePasswordButton.perform(scrollTo(), click())

            val oldPasswordText = onView(allOf(withId(R.id.oldPassword), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            oldPasswordText.perform(scrollTo(), replaceText("Test1z3"), closeSoftKeyboard())

            val newPasswordText = onView(allOf(withId(R.id.newPassword), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            newPasswordText.perform(scrollTo(), replaceText("Test1"), closeSoftKeyboard())

            val changePasswordButton2 = onView(allOf(withId(R.id.changePasswordButton), withText("Change My Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changePasswordButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val passwordPage = onView(allOf(withId(R.id.title), withText("Change your Password"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
            passwordPage.check(matches(withText("Change your Password")))

            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }
    }

    @Test
    fun modifyPasswordEmptyCurrentPasswordTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(emailString, oldPassword)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_password_button)).check(matches(isClickable()))
            onView(withId(R.id.change_password_button)).check(matches(isEnabled()))

            val changePasswordButton = onView(allOf(withId(R.id.change_password_button), withText("Change Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            changePasswordButton.perform(scrollTo(), click())

            val newPasswordText = onView(allOf(withId(R.id.newPassword), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            newPasswordText.perform(scrollTo(), replaceText(newPassword), closeSoftKeyboard())

            val changePasswordButton2 = onView(allOf(withId(R.id.changePasswordButton), withText("Change My Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changePasswordButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val passwordPage = onView(allOf(withId(R.id.title), withText("Change your Password"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
            passwordPage.check(matches(withText("Change your Password")))

            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }
    }

    @Test
    fun modifyPasswordEmptyNewPasswordTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(emailString, oldPassword)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_password_button)).check(matches(isClickable()))
            onView(withId(R.id.change_password_button)).check(matches(isEnabled()))

            val changePasswordButton = onView(allOf(withId(R.id.change_password_button), withText("Change Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            changePasswordButton.perform(scrollTo(), click())

            val oldPasswordText = onView(allOf(withId(R.id.oldPassword), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
            oldPasswordText.perform(scrollTo(), replaceText("Test1z3"), closeSoftKeyboard())

            val changePasswordButton2 = onView(allOf(withId(R.id.changePasswordButton), withText("Change My Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changePasswordButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val passwordPage = onView(allOf(withId(R.id.title), withText("Change your Password"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
            passwordPage.check(matches(withText("Change your Password")))

            mActivityTestRule.finishActivity()
            Firebase.auth.signOut()
        } else {
            Assert.fail()
        }
    }

    @Test
    fun modifyPasswordEmptyFieldsTest() {
        val login = Firebase.auth.signInWithEmailAndPassword(emailString, oldPassword)
        Thread.sleep(1000)

        if (login.isSuccessful) {
            mActivityTestRule.launchActivity(Intent())
            onView(withId(R.id.change_password_button)).check(matches(isClickable()))
            onView(withId(R.id.change_password_button)).check(matches(isEnabled()))

            val changePasswordButton = onView(allOf(withId(R.id.change_password_button), withText("Change Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
            changePasswordButton.perform(scrollTo(), click())

            val changePasswordButton2 = onView(allOf(withId(R.id.changePasswordButton), withText("Change My Password"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
            changePasswordButton2.perform(scrollTo(), click())

            Thread.sleep(1000)

            val passwordPage = onView(allOf(withId(R.id.title), withText("Change your Password"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
            passwordPage.check(matches(withText("Change your Password")))

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

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
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdiot.harty.profile.ProfileActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class AccountDeletionTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ProfileActivity::class.java, true, false)

    private var nameString = "New Testy"
    private var emailString = "test1@test.com"
    private var password = "Test123"

    @Test
    fun deleteAccountSuccessTest() {
        val signUp = Firebase.auth.createUserWithEmailAndPassword(emailString, password)
        Thread.sleep(1000)

        if (signUp.isSuccessful) {
            val login = Firebase.auth.signInWithEmailAndPassword(emailString, password)
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nameString).build()
            FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
            Thread.sleep(1000)
            if (login.isSuccessful) {
                mActivityTestRule.launchActivity(Intent())
                val deleteAccountButton = onView(allOf(withId(R.id.delete_account_button), withText("Delete Account"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
                deleteAccountButton.perform(scrollTo(), click())

                val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
                emailText.perform(scrollTo(), replaceText(emailString), closeSoftKeyboard())

                val passwordText = onView(allOf(withId(R.id.password), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
                passwordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

                val deleteAccountButton2 = onView(allOf(withId(R.id.delete_account_button), withText("delete my account \uD83D\uDE15"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
                deleteAccountButton2.perform(scrollTo(), click())
                mActivityTestRule.finishActivity()

                Thread.sleep(1000)

                val newLogin = Firebase.auth.signInWithEmailAndPassword(emailString, password)
                if (newLogin.isSuccessful) {
                    Assert.fail()
                }
            } else {
                Assert.fail()
            }
        } else {
            Assert.fail()
        }
    }

    @Test
    fun deleteAccountFailWrongEmailTest() {
        val signUp = Firebase.auth.createUserWithEmailAndPassword(emailString, password)
        Thread.sleep(1000)

        if (signUp.isSuccessful) {
            val login = Firebase.auth.signInWithEmailAndPassword(emailString, password)
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nameString).build()
            FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
            Thread.sleep(1000)

            if (login.isSuccessful) {
                mActivityTestRule.launchActivity(Intent())
                val deleteAccountButton = onView(allOf(withId(R.id.delete_account_button), withText("Delete Account"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
                deleteAccountButton.perform(scrollTo(), click())

                val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
                emailText.perform(scrollTo(), replaceText("test2@test.com"), closeSoftKeyboard())

                val passwordText = onView(allOf(withId(R.id.password), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
                passwordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

                val deleteAccountButton2 = onView(allOf(withId(R.id.delete_account_button), withText("delete my account \uD83D\uDE15"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
                deleteAccountButton2.perform(scrollTo(), click())

                Thread.sleep(1000)

                val deleteTitle = onView(allOf(withId(R.id.title), withText("Delete My Account"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
                deleteTitle.check(matches(withText("Delete My Account")))

                mActivityTestRule.finishActivity()
                Firebase.auth.signOut()
                Thread.sleep(1000)

                val newLogin = Firebase.auth.signInWithEmailAndPassword(emailString, password)
                Thread.sleep(1000)
                if (newLogin.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser!!.delete()
                } else {
                    Assert.fail()
                }
            } else {
                Assert.fail()
            }
        } else {
            Assert.fail()
        }
    }

    @Test
    fun deleteAccountFailWrongPasswordTest() {
        val signUp = Firebase.auth.createUserWithEmailAndPassword(emailString, password)
        Thread.sleep(1000)

        if (signUp.isSuccessful) {
            val login = Firebase.auth.signInWithEmailAndPassword(emailString, password)
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nameString).build()
            FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
            Thread.sleep(1000)

            if (login.isSuccessful) {
                mActivityTestRule.launchActivity(Intent())
                val deleteAccountButton = onView(allOf(withId(R.id.delete_account_button), withText("Delete Account"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
                deleteAccountButton.perform(scrollTo(), click())

                val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
                emailText.perform(scrollTo(), replaceText(emailString), closeSoftKeyboard())

                val passwordText = onView(allOf(withId(R.id.password), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
                passwordText.perform(scrollTo(), replaceText("Test1z3"), closeSoftKeyboard())

                val deleteAccountButton2 = onView(allOf(withId(R.id.delete_account_button), withText("delete my account \uD83D\uDE15"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
                deleteAccountButton2.perform(scrollTo(), click())

                Thread.sleep(1000)

                val deleteTitle = onView(allOf(withId(R.id.title), withText("Delete My Account"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
                deleteTitle.check(matches(withText("Delete My Account")))

                mActivityTestRule.finishActivity()
                Firebase.auth.signOut()
                Thread.sleep(1000)

                val newLogin = Firebase.auth.signInWithEmailAndPassword(emailString, password)
                Thread.sleep(1000)
                if (newLogin.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser!!.delete()
                } else {
                    Assert.fail()
                }
            } else {
                Assert.fail()
            }
        } else {
            Assert.fail()
        }
    }

    @Test
    fun deleteAccountFailEmptyEmailTest() {
        val signUp = Firebase.auth.createUserWithEmailAndPassword(emailString, password)
        Thread.sleep(1000)

        if (signUp.isSuccessful) {
            val login = Firebase.auth.signInWithEmailAndPassword(emailString, password)
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nameString).build()
            FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
            Thread.sleep(1000)

            if (login.isSuccessful) {
                mActivityTestRule.launchActivity(Intent())
                val deleteAccountButton = onView(allOf(withId(R.id.delete_account_button), withText("Delete Account"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
                deleteAccountButton.perform(scrollTo(), click())

                val passwordText = onView(allOf(withId(R.id.password), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 4)))
                passwordText.perform(scrollTo(), replaceText(password), closeSoftKeyboard())

                val deleteAccountButton2 = onView(allOf(withId(R.id.delete_account_button), withText("delete my account \uD83D\uDE15"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
                deleteAccountButton2.perform(scrollTo(), click())

                Thread.sleep(1000)

                val deleteTitle = onView(allOf(withId(R.id.title), withText("Delete My Account"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
                deleteTitle.check(matches(withText("Delete My Account")))

                mActivityTestRule.finishActivity()
                Firebase.auth.signOut()
                Thread.sleep(1000)

                val newLogin = Firebase.auth.signInWithEmailAndPassword(emailString, password)
                Thread.sleep(1000)
                if (newLogin.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser!!.delete()
                } else {
                    Assert.fail()
                }
            } else {
                Assert.fail()
            }
        } else {
            Assert.fail()
        }
    }

    @Test
    fun deleteAccountFailEmptyPasswordTest() {
        val signUp = Firebase.auth.createUserWithEmailAndPassword(emailString, password)
        Thread.sleep(1000)

        if (signUp.isSuccessful) {
            val login = Firebase.auth.signInWithEmailAndPassword(emailString, password)
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nameString).build()
            FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
            Thread.sleep(1000)

            if (login.isSuccessful) {
                mActivityTestRule.launchActivity(Intent())
                val deleteAccountButton = onView(allOf(withId(R.id.delete_account_button), withText("Delete Account"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
                deleteAccountButton.perform(scrollTo(), click())

                val emailText = onView(allOf(withId(R.id.emailInput), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 2)))
                emailText.perform(scrollTo(), replaceText(emailString), closeSoftKeyboard())

                val deleteAccountButton2 = onView(allOf(withId(R.id.delete_account_button), withText("delete my account \uD83D\uDE15"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
                deleteAccountButton2.perform(scrollTo(), click())

                Thread.sleep(1000)

                val deleteTitle = onView(allOf(withId(R.id.title), withText("Delete My Account"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
                deleteTitle.check(matches(withText("Delete My Account")))

                mActivityTestRule.finishActivity()
                Firebase.auth.signOut()
                Thread.sleep(1000)

                val newLogin = Firebase.auth.signInWithEmailAndPassword(emailString, password)
                Thread.sleep(1000)
                if (newLogin.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser!!.delete()
                } else {
                    Assert.fail()
                }
            } else {
                Assert.fail()
            }
        } else {
            Assert.fail()
        }
    }

    @Test
    fun deleteAccountFailEmptyFieldsTest() {
        val signUp = Firebase.auth.createUserWithEmailAndPassword(emailString, password)
        Thread.sleep(1000)

        if (signUp.isSuccessful) {
            val login = Firebase.auth.signInWithEmailAndPassword(emailString, password)
            val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(nameString).build()
            FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdates)
            Thread.sleep(1000)

            if (login.isSuccessful) {
                mActivityTestRule.launchActivity(Intent())
                val deleteAccountButton = onView(allOf(withId(R.id.delete_account_button), withText("Delete Account"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 8)))
                deleteAccountButton.perform(scrollTo(), click())

                val deleteAccountButton2 = onView(allOf(withId(R.id.delete_account_button), withText("delete my account \uD83D\uDE15"), childAtPosition(childAtPosition(withId(R.id.scrollable), 0), 6)))
                deleteAccountButton2.perform(scrollTo(), click())

                Thread.sleep(1000)

                val deleteTitle = onView(allOf(withId(R.id.title), withText("Delete My Account"), withParent(withParent(withId(R.id.frame_layout))), isDisplayed()))
                deleteTitle.check(matches(withText("Delete My Account")))

                mActivityTestRule.finishActivity()
                Firebase.auth.signOut()
                Thread.sleep(1000)

                val newLogin = Firebase.auth.signInWithEmailAndPassword(emailString, password)
                Thread.sleep(1000)
                if (newLogin.isSuccessful) {
                    FirebaseAuth.getInstance().currentUser!!.delete()
                } else {
                    Assert.fail()
                }
            } else {
                Assert.fail()
            }
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
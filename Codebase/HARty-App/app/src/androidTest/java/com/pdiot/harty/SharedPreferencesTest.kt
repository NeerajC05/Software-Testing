package com.pdiot.harty

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.pdiot.harty.settings.SettingsActivity
import com.pdiot.harty.utils.Constants
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class SharedPreferencesTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SettingsActivity::class.java, true, false)

    @Test
    fun setSharedPreferencesTest() {
        val targetContext = getInstrumentation().targetContext
        val preferencesEditor = targetContext.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE).edit()

        preferencesEditor.putString(Constants.TOTAL_STEPS, "9876") //Change from default 2500 to 9876
        preferencesEditor.commit()
        mActivityTestRule.launchActivity(Intent())

        Espresso.onView(ViewMatchers.withId(R.id.power)).perform(ViewActions.scrollTo()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Thread.sleep(2000)

        val stepTargetNum = Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.totalSteps), ViewMatchers.withText("9876"), ViewMatchers.withParent(ViewMatchers.withParent(IsInstanceOf.instanceOf(ViewGroup::class.java))), ViewMatchers.isDisplayed()))
        stepTargetNum.check(ViewAssertions.matches(ViewMatchers.withText("9876")))
        mActivityTestRule.finishActivity()
    }

    @Test
    fun editSharedPreferencesTest() {
        val targetContext = getInstrumentation().targetContext
        val preferencesEditor = targetContext.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)

        mActivityTestRule.launchActivity(Intent())
        Espresso.onView(ViewMatchers.withId(R.id.power)).perform(ViewActions.scrollTo()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        val stepTargetText = Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.totalSteps), ViewMatchers.withText("2500"), childAtPosition(childAtPosition(ViewMatchers.withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")), 1), 0), ViewMatchers.isDisplayed()))
        stepTargetText.perform(ViewActions.replaceText("7586"))

        val stepTargetText2 = Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.totalSteps), ViewMatchers.withText("7586"), childAtPosition(childAtPosition(ViewMatchers.withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")), 1), 0), ViewMatchers.isDisplayed()))
        stepTargetText2.perform(ViewActions.closeSoftKeyboard())

        val setButton = Espresso.onView(Matchers.allOf(ViewMatchers.withId(R.id.recalculateButton), ViewMatchers.withText("SET"), childAtPosition(childAtPosition(ViewMatchers.withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")), 1), 2), ViewMatchers.isDisplayed()))
        setButton.perform(ViewActions.click())

        Thread.sleep(1000)

        mActivityTestRule.finishActivity()

        val stepTarget = preferencesEditor.getString(Constants.TOTAL_STEPS, "") //Default is 2500
        Assert.assertTrue(stepTarget == "7586")
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

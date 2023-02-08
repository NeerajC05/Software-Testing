package com.pdiot.harty

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pdiot.harty.profile.HistoricData
import com.pdiot.harty.profile.ProfileActivity
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@LargeTest
@RunWith(AndroidJUnit4::class)
class DemandTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(ProfileActivity::class.java, true, false)

    @Test
    fun testDemand() {
        mActivityTestRule.launchActivity(Intent())
        val NUM_CALLS = 6000
        val latch = CountDownLatch(NUM_CALLS)
        val executor = Executors.newFixedThreadPool(NUM_CALLS)
        for (i in 0 until NUM_CALLS) {
            executor.submit {

                //Your code to make a concurrent call to the Firebase authentication API
                val data = HistoricData("0", "0", 0, 0, 0, 0, 0, 0, 0, 0)
                Firebase.database.reference.child("Demand-Test").child(i.toString()).setValue(data)
                latch.countDown()
            }
        }
        latch.await()
        mActivityTestRule.finishActivity()
    }
}
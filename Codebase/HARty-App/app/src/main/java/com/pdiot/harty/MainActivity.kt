package com.pdiot.harty

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.net.ConnectivityManager
import android.provider.Settings
import android.util.Log
import android.view.ViewStub
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.pdiot.harty.onboarding.OnBoardingActivity
import com.pdiot.harty.profile.HistoricData
import com.pdiot.harty.profile.ProfileActivity
import com.pdiot.harty.settings.BluetoothSpeckService
import com.pdiot.harty.settings.SettingsActivity
import com.pdiot.harty.utils.Constants
import com.pdiot.harty.utils.MinutesHelper
import com.pdiot.harty.utils.RESpeckLiveData
import com.pdiot.harty.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.lang.Math.sqrt
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent
import android.graphics.Color
import android.os.*
import androidx.core.app.NotificationManagerCompat

/* This Kotlin class allows manages the profile page of the application. */
class MainActivity : AppCompatActivity() {

    //Permissions
    lateinit var permissionAlertDialog: AlertDialog.Builder
    private lateinit var bluetoothSetting : TextView
    private lateinit var wifiSetting : TextView
    private lateinit var respeckStatus : TextView
    private lateinit var predictionText : TextView

    val permissionsForRequest = arrayListOf<String>()
    var isUserFirstTime = false

    var locationPermissionGranted = false
    var bluetoothPermissionGranted = false
    var cameraPermissionGranted = false
    var notificationPermissionGranted = false

    private var previousMagnitude = 0.0
    private var previousActivity = ""
    private var notificationTimer = 0
    private lateinit var stepCounter : TextView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var sittingTime : TextView
    private lateinit var standingTime : TextView
    private lateinit var runningTime : TextView
    private lateinit var walkingTime : TextView
    private lateinit var lyingTime : TextView
    private lateinit var stairsTime : TextView
    private lateinit var generalTime : TextView
    private lateinit var sittingStandingTime : TextView
    private lateinit var activity : String
    private lateinit var progressCircular : CircularProgressBar
    private lateinit var viewStub : ViewStub


    //Broadcast Receiver
    val filter = IntentFilter()
    lateinit var respeckLiveUpdateReceiver: BroadcastReceiver
    var started = false;
    lateinit var looperRespeck: Looper

    lateinit var interpreter: Interpreter

    val filterTestRespeck = IntentFilter(Constants.ACTION_RESPECK_LIVE_BROADCAST)

    //Notifications
    private val CHANNEL_ID = "channelID"
    private val CHANNEL_NAME = "channelName"
    private val NOTIF_ID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Refreshes the page
        swipeContainer.setOnRefreshListener {
            reset()
        }

        permissionAlertDialog = AlertDialog.Builder(this)
        val sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)

        performOnboarding()

        setupNavigation()

        setupPermissions()

        dashboard(sharedPreferences)

        setupBluetoothService(sharedPreferences)

        mainAlgo(sharedPreferences)
    }

    //Sets up the dashboard information
    @SuppressLint("SimpleDateFormat")
    private fun dashboard(sharedPreferences : SharedPreferences) {
        bluetoothSetting = findViewById(R.id.bluetooth_setting)
        respeckStatus = findViewById(R.id.status_respeck)
        wifiSetting = findViewById(R.id.wifi_setting)

        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val cm = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo


        if (mBluetoothAdapter.isEnabled) {
            bluetoothSetting.text = "Phone Bluetooth Status : ON"
        } else {
            bluetoothSetting.text = "Phone Bluetooth Status : OFF"
        }

        if (nInfo != null && nInfo.isAvailable && nInfo.isConnected) {
            wifiSetting.text = "Phone Wifi Status : ON"
        } else {
            wifiSetting.text = "Phone Wifi Status : OFF"
        }


        if (sharedPreferences.contains(Constants.RESPECK_STATUS)) {
            respeckStatus.text = sharedPreferences.getString(Constants.RESPECK_STATUS, "")
            if (respeckStatus.text.equals("Connected")) {
                respeckStatus.setTextColor(resources.getColor(R.color.green))
            } else {
                respeckStatus.setTextColor(resources.getColor(R.color.red))
            }
        } else {
            respeckStatus.text = "Disconnected"
        }
    }

    //Sets the required navigation for the page
    private fun setupNavigation() {
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavView.selectedItemId = R.id.home

        bottomNavView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.settings -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            true
        }
    }

    //Reset the dashboard page
    private fun reset() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    //Performs the onboarding sequence
    private fun performOnboarding() {
        //Check whether the onboarding screen should be shown
        val sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        if (sharedPreferences.contains(Constants.PREF_USER_FIRST_TIME)) {
            isUserFirstTime = false
        }
        else {
            isUserFirstTime = true
            sharedPreferences.edit().putBoolean(Constants.PREF_USER_FIRST_TIME, false).apply()
            val introIntent = Intent(this, OnBoardingActivity::class.java)
            startActivity(introIntent)
        }
    }

    //Manages the permission requests
    private fun setupPermissions() {

        //Bluetooth permission
        Log.i("Permissions", "Bluetooth permission = " + bluetoothPermissionGranted)
        if (ActivityCompat.checkSelfPermission(applicationContext,
                Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsForRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            bluetoothPermissionGranted = true
        }

        //Location permission
        Log.i("Permissions", "Location permission = " + locationPermissionGranted)
        if (ActivityCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsForRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsForRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        else {
            locationPermissionGranted = true
        }

        //Camera permission
        Log.i("Permissions", "Camera permission = " + cameraPermissionGranted)
        if (ActivityCompat.checkSelfPermission(applicationContext,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Permissions", "Camera permission = " + cameraPermissionGranted)
            permissionsForRequest.add(Manifest.permission.CAMERA)
        }
        else {
            cameraPermissionGranted = true
        }

        //Location permission
        Log.i("Permissions", "Notifications permission = " + notificationPermissionGranted)
        if (ActivityCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            permissionsForRequest.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        }
        else {
            notificationPermissionGranted = true
        }

        if (permissionsForRequest.size >= 1) {
            ActivityCompat.requestPermissions(this,
                permissionsForRequest.toTypedArray(),
                Constants.REQUEST_CODE_PERMISSIONS)
        }
    }

    //The main algorithm for the step counter and activity recognition
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun mainAlgo(sharedPreferences: SharedPreferences) {
        //Handles the UI states
        if (sharedPreferences.contains(Constants.ALGORITHM)) {
            if (sharedPreferences.getString(Constants.ALGORITHM, "").equals("All Features")) {
                mainAlgoAll(sharedPreferences)
            } else {
                mainAlgoEssential(sharedPreferences)
            }
        } else {
            viewStub = findViewById(R.id.viewStubNoAlgo)
            viewStub.inflate()
        }
    }

    //The main algorithm used when detecting essential features
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun mainAlgoEssential(sharedPreferences: SharedPreferences) {
        val input = Array(50) {FloatArray(6)}
        var counter = 0
        var alertDuration = 0
        val formatter = SimpleDateFormat("yyyy-MM-dd")

        createNotifChannel()

        database = Firebase.database.reference
        auth = Firebase.auth
        stepCounter = findViewById(R.id.stepsTaken)
        progressCircular = findViewById(R.id.progress_circular)
        activity = "Predicting..."

        filter.addAction(Constants.ACTION_RESPECK_CONNECTED)
        filter.addAction(Constants.ACTION_RESPECK_DISCONNECTED)

        viewStub = findViewById(R.id.viewStubEssential)
        viewStub.inflate()

        sittingStandingTime = findViewById(R.id.sittingStandingTime)
        walkingTime = findViewById(R.id.walkingTime)
        runningTime = findViewById(R.id.runningTime)
        lyingTime = findViewById(R.id.lyingTime)

        sittingStandingTime.text = setText(sharedPreferences, Constants.SITTING_STANDING_TIME)
        walkingTime.text = setText(sharedPreferences, Constants.WALKING_TIME)
        runningTime.text = setText(sharedPreferences, Constants.RUNNING_TIME)
        lyingTime.text = setText(sharedPreferences, Constants.DUMMY_TIME)

        predictionText = findViewById(R.id.predictionText)
        interpreter = Interpreter(loadModelFile(sharedPreferences),null)

        stepCounter.text = if (sharedPreferences.contains(Constants.STEPS)) sharedPreferences.getString(Constants.STEPS, "0") else "0"
        progressCircular.progressMax = if (sharedPreferences.contains(Constants.TOTAL_STEPS)) sharedPreferences.getString(Constants.TOTAL_STEPS, "0").toString().toFloat() else 2500F

        //Retrieves the alert duration
        if (sharedPreferences.contains(Constants.ALERT_DURATION)) {
            val durationString = sharedPreferences.getString(Constants.ALERT_DURATION, "").toString()
            val pattern = "\\d+".toRegex()
            alertDuration = (pattern.find(durationString)?.value.toString().toInt()) * 60
        }

        progressCircular.apply {
            setProgressWithAnimation(stepCounter.text.toString().toFloat(),20)
        }

        try {
            respeckLiveUpdateReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val currentUser = auth.currentUser
                    val storedDate = sharedPreferences.getString(Constants.DAY_STEPS, "")
                    val currentDate = formatter.format(Date()).toString()
                    var steps = sharedPreferences.getString(Constants.STEPS, "0").toString().toInt()
                    var sittingStandingTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.SITTING_STANDING_TIME, "0:00"))
                    var walkingTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.WALKING_TIME, "0:00"))
                    var lyingTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.DUMMY_TIME, "0:00"))
                    var runningTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.RUNNING_TIME, "0:00"))

                    if (currentDate == storedDate) {
                        //Determines the current activity and the steps taken
                        Log.i("thread", "I am running on thread = " + Thread.currentThread().name)
                        val action = intent.action
                        if (action == Constants.ACTION_RESPECK_LIVE_BROADCAST) {
                            val liveData = intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData
                            Log.d("Live", "onReceive: liveData = " + liveData)

                            val x = liveData.accelX
                            val y = liveData.accelY
                            val z = liveData.accelZ
                            val gyro_x = liveData.gyro.x
                            val gyro_y = liveData.gyro.y
                            val gyro_z = liveData.gyro.z

                            val magnitude = sqrt(x*x.toDouble() + y*y.toDouble() + z*z.toDouble())

                            //Determines the current activity and increments the time for the recognized activity
                            if (counter <= 49) {
                                input[counter] = floatArrayOf(x, y, z, gyro_x, gyro_y, gyro_z)
                                counter++
                            } else {
                                interpreter = Interpreter(loadModelFile(sharedPreferences),null)
                                val activityRecognized = doInference(sharedPreferences, input)

                                if (activityRecognized.name.contains("Sitting/Standing")) {
                                    sittingStandingTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.SITTING_STANDING_TIME, MinutesHelper.convertToString(sittingStandingTimeValue)).apply()
                                } else if (activityRecognized.name.contains("Running")) {
                                    runningTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.RUNNING_TIME, MinutesHelper.convertToString(runningTimeValue)).apply()
                                } else if (activityRecognized.name.contains("Walking")) {
                                    walkingTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.WALKING_TIME, MinutesHelper.convertToString(walkingTimeValue)).apply()
                                } else if (activityRecognized.name.contains("Lying down")) {
                                    lyingTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.DUMMY_TIME, MinutesHelper.convertToString(lyingTimeValue)).apply()
                                }

                                activity = activityRecognized.name
                                counter = 0

                                //Manage reminder notifications
                                if (sharedPreferences.contains(Constants.ALERT_SWITCH)) {
                                    if (sharedPreferences.getString(Constants.ALERT_SWITCH, "").equals("on")) {
                                        notificationTimer += if ((previousActivity == activity)) 1 else 0
                                        if (notificationTimer == alertDuration) {
                                            notificationTimer = 0
                                            callNotif(activity)
                                        }
                                    }
                                }
                            }

                            //Manages step counter
                            if ((previousMagnitude - magnitude) > 0.45) {
                                steps += 1
                            }

                            //Updates the live UI changes
                            runOnUiThread {
                                predictionText.text = activity
                                sittingStandingTime.text = MinutesHelper.convertToString(sittingStandingTimeValue)
                                walkingTime.text = MinutesHelper.convertToString(walkingTimeValue)
                                runningTime.text = MinutesHelper.convertToString(runningTimeValue)
                                lyingTime.text = MinutesHelper.convertToString(lyingTimeValue)
                                stepCounter.text = steps.toString()

                                progressCircular.apply {
                                    setProgressWithAnimation(stepCounter.text.toString().toFloat())
                                }
                            }

                            //Updates state
                            sharedPreferences.edit().putString(Constants.STEPS, steps.toString()).apply()
                            sharedPreferences.edit().putString(Constants.DAY_STEPS, currentDate).apply()
                            previousMagnitude = magnitude
                            previousActivity = activity
                        }

                    } else if (currentDate != storedDate) {
                        //Uploads historic data to the server
                        if (currentUser != null) {
                            val data = HistoricData(
                                storedDate,
                                steps.toString(),
                                0,
                                0,
                                runningTimeValue,
                                walkingTimeValue,
                                lyingTimeValue,
                                0,
                                0,
                                sittingStandingTimeValue
                            )
                            if (storedDate != null) {
                                database.child(currentUser.uid).child(storedDate).setValue(data)
                            }
                        }


                        runOnUiThread { stepCounter.text = "0" }

                        //Updates state
                        sharedPreferences.edit().putString(Constants.STEPS, "0").apply()
                        sharedPreferences.edit().putString(Constants.SITTING_STANDING_TIME, "0:00")
                            .apply()
                        sharedPreferences.edit().putString(Constants.RUNNING_TIME, "0:00").apply()
                        sharedPreferences.edit().putString(Constants.WALKING_TIME, "0:00").apply()
                        sharedPreferences.edit().putString(Constants.DUMMY_TIME, "0:00").apply()

                        sharedPreferences.edit().putString(Constants.DAY_STEPS, currentDate).apply()
                    }
                }
            }
            //Register receiver on another thread
            val handlerThreadRespeck = HandlerThread("bgThreadRespeckLive")
            handlerThreadRespeck.start()
            looperRespeck = handlerThreadRespeck.looper
            val handlerRespeck = Handler(looperRespeck)
            this.registerReceiver(respeckLiveUpdateReceiver, filterTestRespeck, null, handlerRespeck)
            started = true;
        } catch (ex : Exception) {
            Toast.makeText(this, "Waiting for Respeck to start broadcasting data.", Toast.LENGTH_SHORT).show()
            connectionUpdate(sharedPreferences, "Disconnected", false, false, "LINK", true, true, true)
        }
    }

    //The main algorithm used when detecting all features
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun mainAlgoAll(sharedPreferences: SharedPreferences) {
        val input = Array(50) {FloatArray(6)}
        var counter = 0
        var alertDuration = 0
        val formatter = SimpleDateFormat("yyyy-MM-dd")

        createNotifChannel()

        database = Firebase.database.reference
        auth = Firebase.auth
        stepCounter = findViewById(R.id.stepsTaken)
        progressCircular = findViewById(R.id.progress_circular)
        activity = "Predicting..."

        filter.addAction(Constants.ACTION_RESPECK_CONNECTED)
        filter.addAction(Constants.ACTION_RESPECK_DISCONNECTED)

        //Handles the UI states
        viewStub = findViewById(R.id.viewStubAll)
        viewStub.inflate()

        sittingTime = findViewById(R.id.sittingTime)
        standingTime = findViewById(R.id.standingTime)
        runningTime = findViewById(R.id.runningTime)
        walkingTime = findViewById(R.id.walkingTime)
        lyingTime = findViewById(R.id.lyingTime)
        stairsTime = findViewById(R.id.stairsTime)
        generalTime = findViewById(R.id.generalTime)

        sittingTime.text = setText(sharedPreferences, Constants.SITTING_TIME)
        standingTime.text = setText(sharedPreferences, Constants.STANDING_TIME)
        runningTime.text = setText(sharedPreferences, Constants.RUNNING_TIME)
        walkingTime.text = setText(sharedPreferences, Constants.WALKING_TIME)
        lyingTime.text = setText(sharedPreferences, Constants.DUMMY_TIME)
        stairsTime.text = setText(sharedPreferences, Constants.STAIRS_TIME)
        generalTime.text = setText(sharedPreferences, Constants.GENERAL_TIME)

        predictionText = findViewById(R.id.predictionText)
        interpreter = Interpreter(loadModelFile(sharedPreferences),null)

        stepCounter.text = if (sharedPreferences.contains(Constants.STEPS)) sharedPreferences.getString(Constants.STEPS, "0") else "0"
        progressCircular.progressMax = if (sharedPreferences.contains(Constants.TOTAL_STEPS)) sharedPreferences.getString(Constants.TOTAL_STEPS, "0").toString().toFloat() else 2500F

        //Retrieves the alert duration
        if (sharedPreferences.contains(Constants.ALERT_DURATION)) {
            val durationString = sharedPreferences.getString(Constants.ALERT_DURATION, "").toString()
            val pattern = "\\d+".toRegex()
            alertDuration = (pattern.find(durationString)?.value.toString().toInt()) * 60
        }

        progressCircular.apply {
            setProgressWithAnimation(stepCounter.text.toString().toFloat(),20)
        }

        try {
            respeckLiveUpdateReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val currentUser = auth.currentUser
                    val storedDate = sharedPreferences.getString(Constants.DAY_STEPS, "")
                    val currentDate = formatter.format(Date()).toString()
                    var steps = sharedPreferences.getString(Constants.STEPS, "0").toString().toInt()
                    var sittingTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.SITTING_TIME, "0:00"))
                    var standingTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.STANDING_TIME, "0:00"))
                    var runningTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.RUNNING_TIME, "0:00"))
                    var walkingTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.WALKING_TIME, "0:00"))
                    var lyingTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.DUMMY_TIME, "0:00"))
                    var stairsTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.STAIRS_TIME, "0:00"))
                    var generalTimeValue = MinutesHelper.convertToSeconds(sharedPreferences.getString(Constants.GENERAL_TIME, "0:00"))

                    if (currentDate == storedDate) {
                        //Determines the current activity and the steps taken
                        Log.i("thread", "I am running on thread = " + Thread.currentThread().name)
                        val action = intent.action
                        if (action == Constants.ACTION_RESPECK_LIVE_BROADCAST) {
                            val liveData = intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData
                            Log.d("Live", "onReceive: liveData = " + liveData)

                            val x = liveData.accelX
                            val y = liveData.accelY
                            val z = liveData.accelZ
                            val gyro_x = liveData.gyro.x
                            val gyro_y = liveData.gyro.y
                            val gyro_z = liveData.gyro.z

                            val magnitude = sqrt(x*x.toDouble() + y*y.toDouble() + z*z.toDouble())

                            //Determines the current activity and increments the time for the recognized activity
                            if (counter <= 49) {
                                input[counter] = floatArrayOf(x, y, z, gyro_x, gyro_y, gyro_z)
                                counter++
                            } else {
                                interpreter = Interpreter(loadModelFile(sharedPreferences),null)
                                val activityRecognized = doInference(sharedPreferences, input)

                                if (activityRecognized.name.contains("Sitting")) {
                                    sittingTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.SITTING_TIME, MinutesHelper.convertToString(sittingTimeValue)).apply()
                                } else if (activityRecognized.name.contains("Standing")) {
                                    standingTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.STANDING_TIME, MinutesHelper.convertToString(standingTimeValue)).apply()
                                } else if (activityRecognized.name.contains("Running")) {
                                    runningTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.RUNNING_TIME, MinutesHelper.convertToString(runningTimeValue)).apply()
                                } else if (activityRecognized.name.contains("Walking")) {
                                    walkingTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.WALKING_TIME, MinutesHelper.convertToString(walkingTimeValue)).apply()
                                } else if (activityRecognized.name.contains("Desk Work") || activityRecognized.name.contains("Movement")) {
                                    generalTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.GENERAL_TIME, MinutesHelper.convertToString(generalTimeValue)).apply()
                                } else if (activityRecognized.name.contains("stairs")) {
                                    stairsTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.STAIRS_TIME, MinutesHelper.convertToString(stairsTimeValue)).apply()
                                } else if (activityRecognized.name.contains("Lying down")) {
                                    lyingTimeValue += 1
                                    sharedPreferences.edit().putString(Constants.DUMMY_TIME, MinutesHelper.convertToString(lyingTimeValue)).apply()
                                }

                                activity = activityRecognized.name
                                counter = 0

                                //Manage reminder notifications
                                if (sharedPreferences.contains(Constants.ALERT_SWITCH)) {
                                    if (sharedPreferences.getString(Constants.ALERT_SWITCH, "").equals("on")) {
                                        notificationTimer += if ((previousActivity == activity)) 1 else 0
                                        if (notificationTimer == alertDuration) {
                                            notificationTimer = 0
                                            callNotif(activity)
                                        }
                                    }
                                }
                            }

                            //Manages step counter
                            if ((previousMagnitude - magnitude) > 0.45) {
                                steps += 1
                            }

                            //Updates the live UI changes
                            runOnUiThread {
                                predictionText.text = activity
                                sittingTime.text = MinutesHelper.convertToString(sittingTimeValue)
                                standingTime.text = MinutesHelper.convertToString(standingTimeValue)
                                runningTime.text = MinutesHelper.convertToString(runningTimeValue)
                                walkingTime.text = MinutesHelper.convertToString(walkingTimeValue)
                                generalTime.text = MinutesHelper.convertToString(generalTimeValue)
                                stairsTime.text = MinutesHelper.convertToString(stairsTimeValue)
                                lyingTime.text = MinutesHelper.convertToString(lyingTimeValue)
                                stepCounter.text = steps.toString()
                                progressCircular.apply {
                                    setProgressWithAnimation(stepCounter.text.toString().toFloat())
                                }
                            }

                            //Updates state
                            sharedPreferences.edit().putString(Constants.STEPS, steps.toString()).apply()
                            sharedPreferences.edit().putString(Constants.DAY_STEPS, currentDate).apply()
                            previousMagnitude = magnitude
                            previousActivity = activity
                        }

                    } else if (currentDate != storedDate) {
                        //Uploads historic data to the server
                        if (currentUser != null) {
                            val data = HistoricData(
                                storedDate, steps.toString(), sittingTimeValue, standingTimeValue,
                                runningTimeValue, walkingTimeValue, lyingTimeValue, stairsTimeValue,
                                generalTimeValue, 0
                            )
                            if (storedDate != null) {
                                database.child(currentUser.uid).child(storedDate).setValue(data)
                            }
                        }


                        runOnUiThread { stepCounter.text = "0" }

                        //Updates state
                        sharedPreferences.edit().putString(Constants.STEPS, "0").apply()
                        sharedPreferences.edit().putString(Constants.SITTING_TIME, "0:00").apply()
                        sharedPreferences.edit().putString(Constants.STANDING_TIME, "0:00").apply()
                        sharedPreferences.edit().putString(Constants.RUNNING_TIME, "0:00").apply()
                        sharedPreferences.edit().putString(Constants.WALKING_TIME, "0:00").apply()
                        sharedPreferences.edit().putString(Constants.GENERAL_TIME, "0:00").apply()
                        sharedPreferences.edit().putString(Constants.STAIRS_TIME, "0:00").apply()
                        sharedPreferences.edit().putString(Constants.DUMMY_TIME, "0:00").apply()

                        sharedPreferences.edit().putString(Constants.DAY_STEPS, currentDate).apply()
                    }
                }
            }
            //Register receiver on another thread
            val handlerThreadRespeck = HandlerThread("bgThreadRespeckLive")
            handlerThreadRespeck.start()
            looperRespeck = handlerThreadRespeck.looper
            val handlerRespeck = Handler(looperRespeck)
            this.registerReceiver(respeckLiveUpdateReceiver, filterTestRespeck, null, handlerRespeck)
            started = true;
        } catch (ex : Exception) {
            Toast.makeText(this, "Waiting for Respeck to start broadcasting data.", Toast.LENGTH_SHORT).show()
            connectionUpdate(sharedPreferences, "Disconnected", false, false, "LINK", true, true, true)
        }
    }


    //Starts the connection service to the Respeck
    private fun setupBluetoothService(sharedPreferences: SharedPreferences) {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!mBluetoothAdapter.isEnabled) {
            connectionUpdate(sharedPreferences, "Disconnected", false, false, "LINK", true, true, true)
        }
        val isServiceRunning = Utils.isServiceRunning(BluetoothSpeckService::class.java, applicationContext)
        Log.i("debug", "isServiceRunning = " + isServiceRunning)

        if (sharedPreferences.contains(Constants.RESPECK_MAC_ADDRESS_PREF) && (sharedPreferences.getString(Constants.LAST_SENSOR_USED,"").equals("Respeck"))) {
            Log.i(
                "sharedpref",
                "Already saw a respeckID, starting service and attempting to reconnect"
            )

            //Launch service to reconnect
            //Start the bluetooth service if it's not already running
            if (!isServiceRunning) {
                Log.i("service", "Starting BLT service")
                val simpleIntent = Intent(this, BluetoothSpeckService::class.java)
                this.startService(simpleIntent)
                Toast.makeText(this, "Restarting connection with Respeck...", Toast.LENGTH_SHORT).show()
                connectionUpdate(sharedPreferences, "Connected", true, true, "RELINK", false, false, false)
            }
        } else {
            connectionUpdate(sharedPreferences, "Disconnected", false, false, "LINK", true, true, true)
        }
    }

    //Updates the shared preferences file with background information
    private fun connectionUpdate(sharedPreferences: SharedPreferences, statusText : String, disconnectEnabled : Boolean, disconnectClicked : Boolean, buttonText : String,
                                 idEnabled : Boolean, selectEnabled : Boolean, selectClickable : Boolean) {
        sharedPreferences.edit().putString(Constants.RESPECK_STATUS, statusText).apply()
        sharedPreferences.edit().putString(Constants.DISCONNECT_RESPECK_ENABLED, disconnectEnabled.toString()).apply()
        sharedPreferences.edit().putString(Constants.DISCONNECT_RESPECK_CLICKABLE, disconnectClicked.toString()).apply()
        sharedPreferences.edit().putString(Constants.CONNECT_RESPECK_BUTTON_TEXT, buttonText).apply()
        sharedPreferences.edit().putString(Constants.RESPECK_ID_ENABLED, idEnabled.toString()).apply()
        sharedPreferences.edit().putString(Constants.SELECT_ALGO_CLICKABLE, selectClickable.toString()).apply()
        sharedPreferences.edit().putString(Constants.SELECT_ALGO_ENABLED, selectEnabled.toString()).apply()
    }

    //Loads the relevant ML algorithm file
    private fun loadModelFile(sharedPreferences: SharedPreferences): MappedByteBuffer {
        val algo = sharedPreferences.getString(Constants.ALGORITHM, "")
        val assetFileDescriptor : AssetFileDescriptor
        if (algo.equals("Essential Features")) {
            assetFileDescriptor = this.assets.openFd("essential-features.tflite")
        } else {
            assetFileDescriptor = this.assets.openFd("all-features.tflite")
        }

        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffSet = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffSet,length)
    }

    //This Kotlin data class is used to aid the activity recognised
    private data class RecognizedActivity (
        val name: String,
        val probability: Float
    ) {
        override fun toString() =
            "$name ${probability}%"
    }

    //Retrieves the text from the shared preferences file
    private fun setText(sharedPreferences: SharedPreferences, stringVar : String): String? {
        return if (sharedPreferences.contains(stringVar)) sharedPreferences.getString(stringVar, "0:00").toString() else "0:00"
    }

    //Creates a notification channel
    private fun createNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    //Creates the movement reminder
    private fun callNotif(activity : String) {
        val intent= Intent(intent)
        val pendingIntent = TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notif = NotificationCompat.Builder(applicationContext,CHANNEL_ID)
            .setContentTitle("Harty - Movement Reminder")
            .setContentText("We have noticed you have been ${activity.lowercase()} for a while! Move about and refresh your body ❤️")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()


        val notifManger = NotificationManagerCompat.from(this)
        notifManger.notify(NOTIF_ID,notif)
    }

    //Gets the current activity being performed
    private fun doInference(sharedPreferences: SharedPreferences, input:Array<FloatArray>) : RecognizedActivity {

        val algo = sharedPreferences.getString(Constants.ALGORITHM, "")
        val output : Array<FloatArray>
        val labels : ArrayList<String>
        if (algo.equals("All Features")) {
            output = Array(1) {FloatArray(14)}
            labels = arrayListOf<String>("Standing", "Movement", "Climbing stairs", "Descending stairs", "Desk work", "Lying down left", "Lying down on back", "Lying down on stomach", "Lying down right", "Running", "Sitting bent backward", "Sitting bent forward", "Sitting", "Walking at normal speed")
        } else {
            output = Array(1) {FloatArray(4)}
            labels = arrayListOf<String>("Sitting/Standing", "Walking at normal speed", "Lying down", "Running")
        }

        interpreter.run(input,output)
        val activities = mutableListOf<RecognizedActivity>()
        labels.forEachIndexed { index, label ->
            val probability = output[0][index]
            activities.add(RecognizedActivity(label, probability)) }
        val orderedActivities = activities.sortedByDescending { it.probability }

        return orderedActivities[0]
    }

    override fun onDestroy() {
        super.onDestroy()
        if (started) {
            unregisterReceiver(respeckLiveUpdateReceiver)
            looperRespeck.quit()
        }
        System.exit(0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if(grantResults.isNotEmpty()) {
                for (i in grantResults.indices) {
                    when(permissionsForRequest[i]) {
                        Manifest.permission.ACCESS_COARSE_LOCATION -> locationPermissionGranted = true
                        Manifest.permission.ACCESS_FINE_LOCATION -> locationPermissionGranted = true
                        Manifest.permission.CAMERA -> cameraPermissionGranted = true
                        Manifest.permission.BLUETOOTH_CONNECT -> bluetoothPermissionGranted = true
                        Manifest.permission.ACCESS_NOTIFICATION_POLICY -> notificationPermissionGranted = true
                    }

                }
            }
        }

        //Count how many permissions need granting
        var numberOfPermissionsUngranted = 0
        if (!locationPermissionGranted) numberOfPermissionsUngranted++
        if (!cameraPermissionGranted) numberOfPermissionsUngranted++
        if (!bluetoothPermissionGranted) numberOfPermissionsUngranted++
        if (!notificationPermissionGranted) numberOfPermissionsUngranted++

        //Show a general message if we need multiple permissions
        if (numberOfPermissionsUngranted == 4) {
            val generalSnackbar = Snackbar
                .make(window.decorView.rootView, "Several permissions needed for app to work.", Snackbar.LENGTH_LONG)
                .setAction("SETTINGS") {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
                .show()
        }
    }
}
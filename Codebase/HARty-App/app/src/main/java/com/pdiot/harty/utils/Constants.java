package com.pdiot.harty.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    // Respeck extras
    public static final int NUMBER_OF_SAMPLES_PER_BATCH = 32;
    public static final float SAMPLING_FREQUENCY = 12.7f;
    public static final int AVERAGE_TIME_DIFFERENCE_BETWEEN_RESPECK_PACKETS = (int) Math.round(
            NUMBER_OF_SAMPLES_PER_BATCH / SAMPLING_FREQUENCY * 1000.);
    public static final int MAXIMUM_MILLISECONDS_DEVIATION_ACTUAL_AND_CORRECTED_TIMESTAMP = 400;

    public static final String ACTION_RESPECK_CONNECTED = "com.pdiot.harty.RESPECK_CONNECTED";
    public static final String ACTION_RESPECK_DISCONNECTED = "com.pdiot.harty.RESPECK_DISCONNECTED";

    public static final String PREFERENCES_FILE = "com.pdiot.harty.PREFERENCE_FILE";
    public static final String RESPECK_MAC_ADDRESS_PREF = "respeck_id_pref";
    public static final String RESPECK_VERSION = "respeck_version";
    public static final String THINGY_MAC_ADDRESS_PREF = "thingy_id_pref";
    public static final String DISCONNECT_RESPECK_ENABLED = "disconnect-respeck-enabled";
    public static final String DISCONNECT_RESPECK_CLICKABLE = "disconnect-respeck-clickable";
    public static final String CONNECT_RESPECK_ENABLED = "connect-respeck-enabled";
    public static final String CONNECT_RESPECK_CLICKABLE = "connect-respeck-clickable";
    public static final String RESPECK_STATUS = "respeck-status";
    public static final String CONNECT_RESPECK_BUTTON_TEXT = "respeck-button-text";
    public static final String RESPECK_ID_ENABLED = "respeck-id";
    public static final String STEPS = "steps-taken";
    public static final String TOTAL_STEPS = "total-steps";
    public static final String DAY_STEPS = "day";
    public static final String SELECT_ALGO_ENABLED = "select-algo-enabled";
    public static final String SELECT_ALGO_CLICKABLE = "select-algo-clickable";
    public static final String LAST_SENSOR_USED = "last-sensor-used";

    public static final String SITTING_STANDING_TIME = "sitting-standing-time";
    public static final String SITTING_TIME = "sitting-time";
    public static final String STANDING_TIME = "standing-time";
    public static final String RUNNING_TIME = "running-time";
    public static final String WALKING_TIME = "walking-time";
    public static final String DUMMY_TIME = "dummy-time";
    public static final String STAIRS_TIME = "stairs-time";
    public static final String GENERAL_TIME = "general-time";
    public static final String ALGORITHM = "algo";
    public static final String ALERT_SWITCH = "alert-switch";
    public static final String ALERT_DURATION = "alert-duration";


    //The REQUEST_ENABLE_BT constant passed to startActivityForResult(android.content.Intent, int)
    // is a locally-defined integer (which must be greater than 0) that the system passes back
    // to you in your onActivityResult(int, int, android.content.Intent) implementation as the requestCode parameter.

    public static final int REQUEST_CODE_PERMISSIONS = 1;

    public static final String PREF_USER_FIRST_TIME = "user_first_time";

    // Broadcast strings
    public static final String ACTION_RESPECK_RECORDING_PAUSE = "com.pdiot.respeck.ACTION_RESPECK_RECORDING_PAUSE";
    public static final String ACTION_RESPECK_RECORDING_CONTINUE = "com.pdiot.respeck.ACTION_RESPECK_RECORDING_CONTINUE";
    public static final String RESPECK_USE_IMU_CHARACTERISTIC = "respeck_char_imu";
    public static final String ACTION_SPECK_BLUETOOTH_SERVICE_SCAN_DEVICES = "com.pdiot.airrespeck.ACTION_SPECK_BLUETOOTH_SERVICE_SCAN_DEVICES";


    public final static String RESPECK_LIVE_CHARACTERISTIC = "00002010-0000-1000-8000-00805f9b34fb";
    public final static String RESPECK_LIVE_V4_CHARACTERISTIC = "00001524-1212-efde-1523-785feabcd125";
    // https://github.com/specknet/respeckmodeltesting/blob/two_characteristics/app/src/main/java/com/specknet/respeckmodeltesting/utils/Constants.java#L60
    public final static String RESPECK_IMU_CHARACTERISTIC_UUID = "00001527-1212-efde-1523-785feabcd125"; // accel + gyro + mag

    // Bluetooth connection timeout: how long to wait after loosing connection before trying reconnect
    public static final int RECONNECTION_TIMEOUT_MILLIS = 10000;
    public static final long RESPECK_CHARACTERISTIC_CHANGE_TIMEOUT_MS = 4000; // 4 seconds
    public static final String CSV_DELIMITER = ","; // yes it's just a comma :)

    // Information for config content provider
    public static class Config {
        public static final String RESPECK_UUID = "RESpeckUUID";
        public static final String THINGY_UUID = "ThingyUUID";
    }

    public static final String RESPECK_DATA_DIRECTORY_NAME = "/RESpeck/";
    public static final String RESPECK_IMU_DATA_DIRECTORY_NAME = "/RESpeck-IMU/";
    public static final long NUMBER_OF_MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
    public static final String RESPECK_LIVE_DATA = "respeck_live_data";
    public static final String ACTION_RESPECK_LIVE_BROADCAST =
            "com.pdiot.respeck.RESPECK_LIVE_BROADCAST";
    public static final float MINUTES_FOR_MEDIAN_CALC = 500;
    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public static final String THINGY_MOTION_CHARACTERISTIC = "ef680406-9b35-4933-9b10-52ffa9740042";

    public static final String ACTION_THINGY_BROADCAST = "com.pdiot.harty.THINGY_BROADCAST";
    public static final String ACTION_THINGY_CONNECTED = "com.pdiot.harty.THINGY_CONNECTED";
    public static final String ACTION_THINGY_DISCONNECTED = "com.pdiot.harty.THINGY_DISCONNECTED";
    public static final String THINGY_LIVE_DATA = "thingy_live_data";



}

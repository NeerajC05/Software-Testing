package com.pdiot.harty.utils;

import android.util.Patterns;

import androidx.core.util.PatternsCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean validatePassword(String password) {
        return password.length() >= 6;
    }

    public static boolean validateEmail(String email) {
        return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validateMAC(String macAddress) {
        String regex = "^([0-9A-Fa-f]{2}[:-])"
                + "{5}([0-9A-Fa-f]{2})|"
                + "([0-9a-fA-F]{4}\\."
                + "[0-9a-fA-F]{4}\\."
                + "[0-9a-fA-F]{4})$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(macAddress);
        return m.matches();
    }
}

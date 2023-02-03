package com.pdiot.harty.utils;

/* This Java class is used to convert minutes to seconds and vice versa in a specific format. */
public class MinutesHelper {

    public static Integer convertToSeconds(String time) {
        String[] timeList = time.split(":");
        int totalTime = (Integer.parseInt(timeList[0]) * 60) + (Integer.parseInt(timeList[1]));
        return totalTime;
    }

    public static String convertToString(int args)
    {
        int seconds = args;
        int S = seconds % 60;
        int H = seconds / 60;
        int M = H % 60;
        H = H / 60;

        if(S <10 & M <10) {
            String Min = "0"+M;
            String Sec = "0"+S;
            return Min+":"+Sec;
        }
        else  if(S <10 & M >= 10) {
            String Sec = "0"+S;
            return M+":"+Sec;
        }
        else  if(S >= 10 & M< 10) {
            String Min = "0"+M;
            return Min+":"+S;
        }
        else{
            return M+":"+S;
        }
    }
}

package com.tecnovajet.iposti;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static boolean isOnline(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static int formatDayOfWeek(int day){

        day = day-1;
        if (day == 0)
            day = 7;

        return day;
    }

    public static Date stringToTime(String time){
        DateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ITALIAN);
        try {
            return sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date stringToTime1(String time){
        DateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
        try {
            return sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String timeToString(Date time){
        DateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
        try {
            return sdf.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMonth(Date date){
        DateFormat sdf = new SimpleDateFormat("MMMM", Locale.ITALIAN);
        try {
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateForDb(Date date){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
        try {
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseDateForDb(String date){
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
        try {
            return sdf.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatDateForDb(Date date){
        DateFormat sdf = new SimpleDateFormat("dd MMMM", Locale.ITALIAN);
        try {
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

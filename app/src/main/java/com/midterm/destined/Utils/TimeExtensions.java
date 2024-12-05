package com.midterm.destined.Utils;

import android.util.Log;
import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeExtensions {
    public static int calculateAge(String dateOfBirth) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);
        LocalDate currentDate = LocalDate.now();
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();

        }
        else{
            return 0;
        }
    }


   public static int getBirthYear(String dob) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("d/M/yyyy").parse(dob));
            return calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            Log.e("SearchPresenter", "Invalid DOB format: " + dob);
            return -1;
        }
    }
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return sdf.format(new Date());
    }

    public static Pair<Integer, Integer> parseAgeRange(String detail){
        if (detail.startsWith("<")) {
            int maxAge = Integer.parseInt(detail.substring(1).trim());
            return new Pair<>(0, maxAge - 1);
        } else if (detail.endsWith("+")) {
            int minAge = Integer.parseInt(detail.substring(0, detail.length() - 1).trim());
            return new Pair<>(minAge, -1);
        } else if (detail.contains("-")) {
            String[] parts = detail.split("-");
            if (parts.length == 2) {
                int minAge = Integer.parseInt(parts[0].trim());
                int maxAge = Integer.parseInt(parts[1].trim());
                return new Pair<>(minAge, maxAge);
            }
        }
        return null;
    }

}

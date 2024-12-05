package com.midterm.destined.Utils;

import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

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

    public static void setChatTimestamp(TextView tvTimestamp, String timestamp) {
        // Định dạng đầu vào (D/MM/yyyy HH:MM)
        SimpleDateFormat inputFormat = new SimpleDateFormat("d/MM/yyyy HH:mm");
        // Các định dạng đầu ra
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dayMonthFormat = new SimpleDateFormat("d/MM");
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("d/MM/yyyy");

        try {
            // Chuyển chuỗi thời gian thành Date
            Date messageDate = inputFormat.parse(timestamp);

            // Lấy ngày hôm nay và năm hiện tại
            Calendar now = Calendar.getInstance();
            Calendar messageCalendar = Calendar.getInstance();
            messageCalendar.setTime(messageDate);

            if (isSameDay(now, messageCalendar)) {
                // Nếu là hôm nay, chỉ hiển thị giờ phút
                tvTimestamp.setText(timeFormat.format(messageDate));
            } else if (isSameYear(now, messageCalendar)) {
                // Nếu cùng năm nhưng không cùng ngày, hiển thị ngày tháng
                tvTimestamp.setText(dayMonthFormat.format(messageDate));
            } else {
                // Nếu khác năm, hiển thị đầy đủ ngày tháng năm
                tvTimestamp.setText(fullDateFormat.format(messageDate));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            tvTimestamp.setText(timestamp); // Hiển thị chuỗi gốc nếu gặp lỗi
        }
    }

    // Kiểm tra nếu cùng ngày
    private static boolean isSameDay(Calendar now, Calendar messageCalendar) {
        return now.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == messageCalendar.get(Calendar.DAY_OF_YEAR);
    }

    // Kiểm tra nếu cùng năm
    private static boolean isSameYear(Calendar now, Calendar messageCalendar) {
        return now.get(Calendar.YEAR) == messageCalendar.get(Calendar.YEAR);
    }


}

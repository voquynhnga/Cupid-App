package com.midterm.destined.Utils;

import android.util.Pair;

public class DistanceExtension {
    public static Pair<String, Integer> parseDistance(String detail) {
        detail = detail.toLowerCase().replace("km", "").trim();
        String operator = "";
        int value = 0;

        if (detail.startsWith("<")) {
            operator = "<";
            value = Integer.parseInt(detail.substring(1).trim());
        } else if (detail.startsWith(">")) {
            operator = ">";
            value = Integer.parseInt(detail.substring(1).trim());
        } else {
            throw new IllegalArgumentException("Invalid distance format. Use <value>km or >value>km");
        }

        return new Pair<>(operator, value);
    }

}

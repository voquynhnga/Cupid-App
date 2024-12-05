package com.midterm.destined.Utils;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midterm.destined.Models.GPSAddress;

public class CalculateCoordinates {

    public static void calculateDistance(String uid1, String uid2, DistanceCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(uid1).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful() && task1.getResult() != null) {
                DocumentSnapshot user1Doc = task1.getResult();
                GPSAddress location1 = user1Doc.get("location", GPSAddress.class);

                firestore.collection("users").document(uid2).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful() && task2.getResult() != null) {
                        DocumentSnapshot user2Doc = task2.getResult();
                        // Lấy thông tin latitude và longitude từ field "location" của user 2
                        GPSAddress location2 = user2Doc.get("location", GPSAddress.class);

                        if (location1 != null && location2 != null) {
                            double distance = calculateHaversine(
                                    location1.getLatitude(), location1.getLongitude(),
                                    location2.getLatitude(), location2.getLongitude()
                            );
                            callback.onDistanceCalculated(distance);
                        } else {
                            callback.onDistanceCalculated(-1); // Trả về -1 nếu không lấy được location
                        }
                    } else {
                        callback.onDistanceCalculated(-1); // Lỗi khi lấy user2
                    }
                });
            } else {
                callback.onDistanceCalculated(-1); // Lỗi khi lấy user1
            }
        });
    }



    private static double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính Trái Đất (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Khoảng cách tính bằng km
    }

    public interface DistanceCallback {
        void onDistanceCalculated(double distance);
    }
}

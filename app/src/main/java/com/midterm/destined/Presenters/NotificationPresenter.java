package com.midterm.destined.Presenters;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.midterm.destined.Models.Notification;
import com.midterm.destined.Models.UserReal;
import com.midterm.destined.Utils.DB;
import com.midterm.destined.Views.Homepage.Notifications.NotificationsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationPresenter {
    private final NotificationsView view;
    private HashMap<String, Notification> matchNotificationsMap = new HashMap<>();
    private HashMap<String, Notification> chatNotificationsMap = new HashMap<>();

    public NotificationPresenter(NotificationsView view) {
        this.view = view;
    }


    public void fetchNotifications() {
        String currentUserId = DB.getCurrentUser().getUid();

        fetchMatchNotifications(currentUserId, () -> {
            fetchChatNotifications(currentUserId, () -> {
                updateView();
            });
        });
    }

    private void fetchMatchNotifications(String currentUserId, Runnable onComplete) {
        FirebaseFirestore.getInstance()
                .collection("matches")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        return;
                    }

                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String documentId = document.getId();
                            String timestamp = document.getString("timestamp");

                            if (timestamp == null) continue;

                            String[] userIds = documentId.split("_");
                            if (userIds.length != 2) continue;

                            String userId1 = userIds[0];
                            String userId2 = userIds[1];
                            String otherUserId;

                            if (currentUserId.equals(userId1)) {
                                otherUserId = userId2;
                            } else if (currentUserId.equals(userId2)) {
                                otherUserId = userId1;
                            } else {
                                continue;
                            }

                            getFullName(otherUserId, fullName -> {
                                String content = "You and " + fullName + " are matched ";
                                Notification notification = new Notification(timestamp, content,1,null);

                                matchNotificationsMap.put(documentId, notification);

                                updateView();
                            });
                        }
                    }

                    onComplete.run();
                });
    }


//    private void fetchChatNotifications(String currentUserId, Runnable onComplete) {
//        DB.getChatsRef().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                final int totalNotifications = (int) dataSnapshot.getChildrenCount();
//                int[] remainingNotifications = {totalNotifications};
//
//                for (DataSnapshot chatNode : dataSnapshot.getChildren()) {
//                    String chatId = chatNode.getKey();
//                    if (chatId == null || (!chatId.contains(currentUserId))) continue;
//
//                    if (chatNotificationsMap.containsKey(chatId)) continue;
//
//                    DataSnapshot lastMessageSnapshot = chatNode.child("lastMessage");
//                    if (!lastMessageSnapshot.exists()) continue;
//
//                    String sender = lastMessageSnapshot.child("sender").getValue(String.class);
//                    String timestamp = lastMessageSnapshot.child("time").getValue(String.class);
//
//                    if (sender == null || timestamp == null || sender.equals(currentUserId)) {
//                        continue;
//                    }
//
//                    getFullName(sender, fullName -> {
//                        String content = "You have just received a message from " + fullName;
//                        Notification chatNotification = new Notification(timestamp, content, 2, sender);
//                        chatNotificationsMap.put(chatId, chatNotification);
//
//                        updateView();
//
//                        remainingNotifications[0]--;
//
//                        if (remainingNotifications[0] == 0) {
//                            onComplete.run();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("fetchChatNotifications", "Error: " + databaseError.getMessage());
//            }
//        });
//    }

    private void fetchChatNotifications(String currentUserId, Runnable onComplete) {
        DB.getChatsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final int totalNotifications = (int) dataSnapshot.getChildrenCount();
                int[] remainingNotifications = {totalNotifications};

                for (DataSnapshot chatNode : dataSnapshot.getChildren()) {
                    String chatId = chatNode.getKey();
                    if (chatId == null || (!chatId.contains(currentUserId))) continue;

                    if (chatNotificationsMap.containsKey(chatId)) continue;

                    DataSnapshot lastMessageSnapshot = chatNode.child("lastMessage");
                    if (!lastMessageSnapshot.exists()) continue;

                    String sender = lastMessageSnapshot.child("sender").getValue(String.class);
                    String timestamp = lastMessageSnapshot.child("time").getValue(String.class);

                    if (sender == null || timestamp == null || sender.equals(currentUserId)) {
                        continue;
                    }

                    getUserRealInfo(sender, userReal -> {
                        String content = "You have not replied to a message from " + userReal.getFullName();
                        Notification chatNotification = new Notification(timestamp, content, 2, userReal);
                        chatNotificationsMap.put(chatId, chatNotification);

                        updateView();

                        remainingNotifications[0]--;

                        if (remainingNotifications[0] == 0) {
                            onComplete.run();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("fetchChatNotifications", "Error: " + databaseError.getMessage());
            }
        });
    }

    private void getUserRealInfo(String userId, UserRealCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserReal userReal = documentSnapshot.toObject(UserReal.class);
                        if (userReal != null) {
                            callback.onUserRealInfoRetrieved(userReal);
                            return;
                        }
                    }
                    callback.onUserRealInfoRetrieved(null);
                })
                .addOnFailureListener(e -> {
                    callback.onUserRealInfoRetrieved(null);
                });
    }

    public interface UserRealCallback {
        void onUserRealInfoRetrieved(UserReal userReal);
    }



    private void updateView() {
        List<Notification> combinedNotifications = new ArrayList<>();

        combinedNotifications.addAll(matchNotificationsMap.values());

        combinedNotifications.addAll(chatNotificationsMap.values());

        view.updateNotifications(combinedNotifications);
    }




    public void getFullName(String otherUserId, FullNameCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(otherUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        if (fullName != null && !fullName.isEmpty()) {
                            callback.onFullNameRetrieved(fullName);
                            return;
                        }
                    }
                    callback.onFullNameRetrieved("Unknown");
                })
                .addOnFailureListener(e -> {
                    callback.onFullNameRetrieved("Unknown");
                });
    }






}

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
import com.midterm.destined.Views.Homepage.Notifications.NotificationsView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationPresenter {
    private final NotificationsView view;
    // Đưa matchNotificationsMap và chatNotificationsMap lên làm biến thành viên
    private HashMap<String, Notification> matchNotificationsMap = new HashMap<>();
    private HashMap<String, Notification> chatNotificationsMap = new HashMap<>();

    public NotificationPresenter(NotificationsView view) {
        this.view = view;
    }


    public void fetchNotifications() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Lấy thông báo từ matches
        fetchMatchNotifications(currentUserId, () -> {
            // Khi đã xong matches, tiếp tục lấy từ chats
            fetchChatNotifications(currentUserId, () -> {
                // Sau khi cả hai thông báo đã được lấy xong, cập nhật giao diện
                updateView();
            });
        });
    }

    private void fetchMatchNotifications(String currentUserId, Runnable onComplete) {
        FirebaseFirestore.getInstance()
                .collection("matches")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        view.showError("Không thể lắng nghe thay đổi dữ liệu: " + e.getMessage());
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
                                Notification notification = new Notification(timestamp, content,1);

                                // Cập nhật thông báo match vào map
                                matchNotificationsMap.put(documentId, notification);

                                // Cập nhật giao diện với thông báo match
                                updateView();
                            });
                        }
                    }

                    // Gọi callback khi xong
                    onComplete.run();
                });
    }


    private void fetchChatNotifications(String currentUserId, Runnable onComplete) {
        DatabaseReference chatsRef = FirebaseDatabase.getInstance().getReference("chats");

        // Lắng nghe thay đổi thời gian thực
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Duyệt qua các thông báo chat
                for (DataSnapshot chatNode : dataSnapshot.getChildren()) {
                    String chatId = chatNode.getKey();

                    if (chatId == null || (!chatId.contains(currentUserId))) continue;

                    // Kiểm tra nếu thông báo chat đã tồn tại
                    if (chatNotificationsMap.containsKey(chatId)) continue;

                    // Lấy thông tin lastMessage
                    DataSnapshot lastMessageSnapshot = chatNode.child("lastMessage");
                    if (!lastMessageSnapshot.exists()) continue;

                    String sender = lastMessageSnapshot.child("sender").getValue(String.class);
                    String timestamp = lastMessageSnapshot.child("time").getValue(String.class);

                    if (sender == null || timestamp == null || sender.equals(currentUserId)) {
                        continue; // Bỏ qua nếu sender là chính người dùng hiện tại
                    }

                    // Lấy tên người gửi
                    getFullName(sender, fullName -> {
                        String content =  "You have just received a message from " + fullName; // Sử dụng tên người gửi thay vì nội dung tin nhắn

                        // Thêm hoặc cập nhật thông báo chat
                        Notification chatNotification = new Notification(timestamp, content,2);
                        chatNotificationsMap.put(chatId, chatNotification);

                        // Cập nhật giao diện với thông báo chat
                        updateView();

                        // Gọi callback sau khi thông báo chat đã được thêm vào map
                        onComplete.run();
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("fetchChatNotifications", "Lỗi: " + databaseError.getMessage());
            }
        });
    }



    private void updateView() {
        List<Notification> combinedNotifications = new ArrayList<>();

        // Thêm thông báo từ matches
        combinedNotifications.addAll(matchNotificationsMap.values());

        // Thêm thông báo từ chats
        combinedNotifications.addAll(chatNotificationsMap.values());

        // Cập nhật giao diện
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
                            Log.d("getFullName", "Lấy fullName thành công: " + fullName);
                            callback.onFullNameRetrieved(fullName);
                            return;
                        }
                    }
                    Log.d("getFullName", "Document không tồn tại hoặc fullName không hợp lệ.");
                    callback.onFullNameRetrieved("Unknown");
                })
                .addOnFailureListener(e -> {
                    Log.e("getFullName", "Lỗi khi lấy fullName: " + e.getMessage());
                    callback.onFullNameRetrieved("Unknown");
                });
    }






}

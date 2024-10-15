//package com.midterm.destined;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class ChatActivityDetail extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat_detail);
//
//        TextView tvConversationName = findViewById(R.id.tvConversationName);
////        ListView listViewMessages = findViewById(R.id.listViewMessages);
//
//        // Lấy tên của cuộc trò chuyện từ Intent
//        String conversationName = getIntent().getStringExtra("conversationName");
//        tvConversationName.setText(conversationName);
//
//        // Dữ liệu mẫu cho tin nhắn
//        String[] messages = {"Xin chào", "Chào bạn", "Bạn khỏe không?"};
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
////        listViewMessages.setAdapter(adapter);
//
//        // Xử lý sự kiện gửi tin nhắn
//        Button btnSend = findViewById(R.id.btnSend);
//        EditText etMessage = findViewById(R.id.etMessage);
//
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = etMessage.getText().toString();
//                if (!message.isEmpty()) {
//                    // Gửi tin nhắn (logic gửi tin nhắn sẽ được xử lý ở đây)
//                    etMessage.setText(""); // Xóa nội dung sau khi gửi
//                }
//            }
//        });
//    }
//}

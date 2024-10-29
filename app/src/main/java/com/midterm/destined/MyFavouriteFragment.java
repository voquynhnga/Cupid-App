package com.midterm.destined;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.midterm.destined.databinding.FragmentMyFavouriteBinding;
import com.midterm.destined.model.UserReal;

import java.util.ArrayList;
import java.util.List;

public class MyFavouriteFragment extends Fragment {

    private FragmentMyFavouriteBinding binding;
    private ArrayList<UserReal> arrayFavourite;
    private FavouriteAdapter favouriteAdapter;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
         }
        arrayFavourite = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fetchFavouriteUsers();

    }


    private void fetchFavouriteUsers() {
        String currentUserId = auth.getCurrentUser().getUid(); // Lấy UID người dùng đang đăng nhập
        firestore.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> favouriteCardList = (List<String>) documentSnapshot.get("favoritedCardList");
                        if (favouriteCardList != null) {
                            // Duyệt qua từng UID trong danh sách yêu thích
                            for (String uid : favouriteCardList) {
                                // Lấy thông tin người dùng từ UID
                                firestore.collection("users").document(uid)
                                        .get()
                                        .addOnSuccessListener(userSnapshot -> {
                                            if (userSnapshot.exists()) {
                                                // Lấy đối tượng UserReal từ Firestore
                                                UserReal user = userSnapshot.toObject(UserReal.class);
                                                if (user != null) {
                                                    arrayFavourite.add(user); // Thêm đối tượng UserReal vào danh sách
                                                    favouriteAdapter.notifyDataSetChanged(); // Cập nhật adapter
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            // Xử lý lỗi nếu cần
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi nếu cần
                });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_my_favourite, container, false);
        binding = FragmentMyFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Setup RecyclerView và Adapter tại đây khi view đã được tạo


        binding.rvFavourites.setLayoutManager(new GridLayoutManager(getContext(), 2));
        favouriteAdapter = new FavouriteAdapter(arrayFavourite);
        binding.rvFavourites.setAdapter(favouriteAdapter);

        favouriteAdapter.notifyDataSetChanged();
    }
}
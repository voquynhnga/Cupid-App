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

import com.midterm.destined.databinding.FragmentMyFavouriteBinding;

import java.util.ArrayList;

public class MyFavouriteFragment extends Fragment {

    private FragmentMyFavouriteBinding binding;
    private ArrayList<String> arrayFavourite;
    private FavouriteAdapter favouriteAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
         }
        arrayFavourite = new ArrayList<>();
        arrayFavourite.add("Nguyen Quang Kien");
        arrayFavourite.add("John Terry");
        arrayFavourite.add("Stone");
        arrayFavourite.add("Rock");
        arrayFavourite.add("Ha Tran");
        arrayFavourite.add("SnapDog");
        arrayFavourite.add("Jack 100M");
        arrayFavourite.add("Switf");
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
        binding.rvFavourites.setLayoutManager(new GridLayoutManager(getContext(),2));
        favouriteAdapter = new FavouriteAdapter(arrayFavourite);
        binding.rvFavourites.setAdapter(favouriteAdapter);

        // Cập nhật dữ liệu nếu cần thiết
        favouriteAdapter.notifyDataSetChanged();

    }
}
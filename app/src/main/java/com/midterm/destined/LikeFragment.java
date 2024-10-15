//package com.midterm.destined;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.viewpager2.widget.ViewPager2;
//
//public class LikeFragment extends Fragment {
//
//    LikeAdapter demoCollectionPagerAdapter;
//    ViewPager2 viewPager;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.pager, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        demoCollectionPagerAdapter = new LikeAdapter(this);
//        viewPager = view.findViewById(R.id.pager);
//        viewPager.setAdapter(demoCollectionPagerAdapter);
//
//        // Apply a custom PageTransformer to achieve the Tinder-like swipe effect
//        viewPager.setPageTransformer(new CardStackPageTransformer());
//    }
//}

//package com.midterm.destined;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.viewpager2.adapter.FragmentStateAdapter;
//
//import com.midterm.destined.model.User;
//
//import java.util.List;
//
//public class CardAdapter extends FragmentStateAdapter {
//
//    private List<User> profiles;
//
//    public CardAdapter(Fragment fragment, List<User> profiles) {
//        super(fragment);
//        this.profiles = profiles;
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return CardFragment.newInstance(profiles.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return profiles.size();
//    }
//}

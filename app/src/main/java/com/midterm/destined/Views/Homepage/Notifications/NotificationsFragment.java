package com.midterm.destined.Views.Homepage.Notifications;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.midterm.destined.Adapters.NotificationAdapter;
import com.midterm.destined.Models.Notification;
import com.midterm.destined.Presenters.NotificationPresenter;
import com.midterm.destined.R;
import com.midterm.destined.databinding.FragmentHomepageBinding;
import com.midterm.destined.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment implements NotificationsView{

    private FragmentNotificationsBinding binding;
    private NotificationAdapter adapter;
    private NotificationPresenter presenter;
    private List<Notification> notificationList;
    private ImageView btnBack;
    LinearLayoutManager layoutManager;
    boolean hasMod2 = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        presenter = new NotificationPresenter(this);

        presenter.fetchNotifications();


        layoutManager = new LinearLayoutManager(getContext());
        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(getContext(), notificationList);
        binding.notificationsRecyclerView.setAdapter(adapter);

        scrollToTop();



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnBack = view.findViewById(R.id.btn_backNotification);

        btnBack.setOnClickListener(v->{

            Bundle bundle = new Bundle();
            bundle.putBoolean("New notification", hasMod2);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_global_HomepageFragment, bundle);

        });
    }

    @Override
    public void scrollToTop() {
        if (layoutManager != null && adapter.getItemCount() > 0) {
           binding.notificationsRecyclerView.scrollToPosition(0);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void updateNotifications(List<Notification> notifications) {


        for (Notification notification : notifications) {
            if (notification.getMod() == 2) {
                hasMod2 = true;
                break;
            }
        }


        adapter.updateData(notifications);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }



}
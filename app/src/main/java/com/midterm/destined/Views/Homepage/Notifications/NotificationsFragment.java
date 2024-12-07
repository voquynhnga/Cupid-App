package com.midterm.destined.Views.Homepage.Notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        binding.notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(getContext(), notificationList);
        binding.notificationsRecyclerView.setAdapter(adapter);



        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void updateNotifications(List<Notification> notifications) {
        adapter.updateData(notifications);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
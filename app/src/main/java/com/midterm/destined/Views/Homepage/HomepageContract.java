package com.midterm.destined.Views.Homepage;

import com.midterm.destined.Views.Homepage.Card.CardFragment;

public interface HomepageContract {
    interface View {
        void showCards(); // Hiển thị CardFragment
        void performLikeAction(); // Thực hiện hành động Like
        void performDislikeAction(); // Thực hiện hành động Dislike
        void navigateToSearch(); // Điều hướng tới SearchFragment
    }

    interface Presenter {
        void onLikeButtonClicked(); // Xử lý khi nhấn nút Like
        void onDislikeButtonClicked(); // Xử lý khi nhấn nút Dislike
        void onSearchButtonClicked(); // Xử lý khi nhấn nút tìm kiếm
    }
}

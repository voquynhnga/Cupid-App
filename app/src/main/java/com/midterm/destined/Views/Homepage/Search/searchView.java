package com.midterm.destined.Views.Homepage.Search;

import com.midterm.destined.Models.UserReal;

import java.util.List;

public interface searchView {

    void updateSearchResults(List<UserReal> users);


    void showError(String message);

    void setLoading(boolean isLoading);
}

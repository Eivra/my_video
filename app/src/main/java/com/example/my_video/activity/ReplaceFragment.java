package com.example.my_video.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my_video.basePage.BasePage;

public class ReplaceFragment extends Fragment {

    private BasePage basePage;

    public ReplaceFragment(BasePage pager) {
        this.basePage=pager;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return basePage.rootView;
    }
}


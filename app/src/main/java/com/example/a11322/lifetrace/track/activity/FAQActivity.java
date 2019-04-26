package com.example.a11322.lifetrace.track.activity;

import android.os.Bundle;
import android.view.View;

import com.example.a11322.lifetrace.R;


public class FAQActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.fag_title);
        setOptionsButtonInVisible();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_faq;
    }
}

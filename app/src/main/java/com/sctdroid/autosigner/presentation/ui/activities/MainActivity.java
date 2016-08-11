package com.sctdroid.autosigner.presentation.ui.activities;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sctdroid.autosigner.R;
import com.sctdroid.autosigner.presentation.presenters.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainPresenter.View, android.view.View.OnClickListener {

    TextView ball;
    float y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void onClick(android.view.View view) {

    }
}

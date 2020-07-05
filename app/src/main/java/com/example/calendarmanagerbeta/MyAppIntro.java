package com.example.calendarmanagerbeta;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.SlidePolicy;

public class MyAppIntro extends AppIntro implements SlidePolicy {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.activity_my_app_intro));
        setSystemBackButtonLocked(true);
        setColorDoneText(Color.BLACK);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment){
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment){
        setSystemBackButtonLocked(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean isPolicyRespected() {
        return false;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {

    }
}
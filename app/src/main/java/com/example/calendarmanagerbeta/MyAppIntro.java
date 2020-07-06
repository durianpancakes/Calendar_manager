package com.example.calendarmanagerbeta;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.SlidePolicy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyAppIntro extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(IntroFragment.createInstance(R.layout.activity_my_app_intro));
        setSystemBackButtonLocked(true);
        setColorDoneText(Color.BLACK);
        setButtonsEnabled(false);
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
}
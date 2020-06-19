package com.rutkovski.interval.screens;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.rutkovski.interval.R;

public class TrainingActivity extends AppCompatActivity {
    private TextView textViewTimer;
    private TextView textViewAttempt;
    private ConstraintLayout constraintLayout;
    private TextView textViewWork;
    private Button buttonEndWork;
    private Button buttonEndRelax;
    private TrainingViewModel trainingViewModel;
    private SoundPool soundPool;
    private int soundIdEndTimer;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        trainingViewModel = ViewModelProviders.of(this).get(TrainingViewModel.class);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("attempt") && intent.hasExtra("timeRelax")) {
            trainingViewModel.createTimer(intent.getLongExtra("timeRelax", 60000L));
            trainingViewModel.setAttemptAll(intent.getIntExtra("attempt", 5));
        } else {
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textViewTimer = findViewById(R.id.textViewTimer);
        textViewAttempt = findViewById(R.id.textViewCounter);
        constraintLayout = findViewById(R.id.constraintLayoutRelax);
        textViewWork = findViewById(R.id.textViewWork);
        buttonEndWork = findViewById(R.id.buttonEndAttempt);
        buttonEndRelax = findViewById(R.id.buttonEndRelax);



        textViewAttempt.setText(trainingViewModel.counterString());
        trainingViewModel.getCurrentTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String string) {
                textViewTimer.setText(string);
            }
        });
        trainingViewModel.getIsRunning().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer isRun) {
                if (isRun == 1) {
                    runTimer();
                } else {
                    stopTimer();

                }
            }
        });

    }

    public void endAttempt(View view) {
        trainingViewModel.countAddAttemptNow();
        textViewAttempt.setText(trainingViewModel.counterString());
        trainingViewModel.startTimer();
    }

    public void endRelax(View view) {
        stopTimer();
        trainingViewModel.cancelTimer();
    }


    private void runTimer() {
        textViewWork.setVisibility(View.INVISIBLE);
        constraintLayout.setVisibility(View.VISIBLE);
        buttonEndWork.setVisibility(View.INVISIBLE);
        buttonEndRelax.setVisibility(View.VISIBLE);
    }

    private void stopTimer() {
        textViewWork.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.INVISIBLE);
        buttonEndWork.setVisibility(View.VISIBLE);
        buttonEndRelax.setVisibility(View.INVISIBLE);
    }


}

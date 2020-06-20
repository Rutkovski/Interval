package com.rutkovski.interval.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.rutkovski.interval.R;

import java.util.Locale;

public class TrainingActivity extends AppCompatActivity {

    private TextView textViewTimer;
    private TextView textViewAttempt;
    private ConstraintLayout constraintLayout;
    private TextView textViewWork;
    private Button buttonEndWork;
    private Button buttonEndRelax;
    private TrainingViewModel trainingViewModel;
    private int attemptAll;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                createDialog(0).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        trainingViewModel = ViewModelProviders.of(this).get(TrainingViewModel.class);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("attempt") && intent.hasExtra("timeRelax")) {
            trainingViewModel.createTimer(intent.getLongExtra("timeRelax", 60000L));
            attemptAll = intent.getIntExtra("attempt", 5);
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
        textViewAttempt.setText(String.format(Locale.getDefault(), "%d/%d", trainingViewModel.getAttemptNow(), attemptAll));
        trainingViewModel.getCurrentTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String string) {
                textViewTimer.setText(string);
            }
        });


        trainingViewModel.getIsRunning().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isRun) {
                if (isRun) {
                    runTimer();
                } else {
                    stopTimer();
                }
            }
        });
    }

    public void endAttempt(View view) {
        trainingViewModel.countAddAttemptNow();
        int attemptNow = trainingViewModel.getAttemptNow();
        textViewAttempt.setText(String.format(Locale.getDefault(), "%d/%d", attemptNow, attemptAll));
        if (attemptNow != attemptAll) {
            trainingViewModel.startTimer();
        } else {
            createDialog(1).show();
        }


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


    private AlertDialog createDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (id == 1) {
            builder.setTitle(R.string.dialog_end_title);
            builder.setMessage(R.string.dialog_end_description);
            builder.setPositiveButton(R.string.dialog_button_continue, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    trainingViewModel.startTimer();
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton(R.string.dialog_button_complete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
        } else {
            builder.setTitle(R.string.dialog_exit_title);
            builder.setMessage(R.string.dialog_exit_description);
            builder.setPositiveButton(R.string.dialog_button_continue, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton(R.string.dialog_button_complete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
        }


        builder.setCancelable(false);
        return builder.create();
    }

    @Override
    public void onBackPressed() {
        createDialog(0).show();
        super.onBackPressed();
    }
}

package com.rutkovski.interval.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rutkovski.interval.R;

import java.util.Locale;

public class StartActivity extends AppCompatActivity {
    private EditText editTextTimeSek;
    private EditText editTextTimeMin;
    private EditText editTextAttempt;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        editTextAttempt = findViewById(R.id.editTextAttempt);
        editTextTimeSek = findViewById(R.id.editTextTimeSek);
        editTextTimeMin = findViewById(R.id.editTextTimeMin);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editTextTimeSek.setText(String.format(Locale.getDefault(), "%02d", sharedPreferences.getInt("timeRelaxSek", 60)));
        editTextTimeMin.setText(String.format(Locale.getDefault(), "%02d", sharedPreferences.getInt("timeRelaxMin", 0)));
        editTextAttempt.setText(String.format(Locale.getDefault(), "%d", sharedPreferences.getInt("attempt", 5)));
        setListenersEditText();

    }

    public void startTraining(View view) {

        Intent intent = new Intent(this, TrainingActivity.class);
        String timeRelaxSekStr = editTextTimeSek.getText().toString().trim();
        String timeRelaxMinStr = editTextTimeMin.getText().toString().trim();
        String attemptStr = editTextAttempt.getText().toString().trim();


        if (timeRelaxMinStr.isEmpty()){
            timeRelaxMinStr="0";
        }
        if (timeRelaxSekStr.isEmpty()){
            timeRelaxSekStr="0";
        }

        if (attemptStr.isEmpty()) {
            attemptStr = "1";
        }

        try {
            int timeRelaxSek = Integer.parseInt(timeRelaxSekStr);
            int timeRelaxMin = Integer.parseInt(timeRelaxMinStr);
            int attempt = Integer.parseInt(attemptStr);

            if (timeRelaxSek==0&&timeRelaxMin==0){
                timeRelaxSek = 1;
            }

            sharedPreferences.edit().putInt("attempt", attempt).apply();
            sharedPreferences.edit().putInt("timeRelaxSek", timeRelaxSek).apply();
            sharedPreferences.edit().putInt("timeRelaxMin", timeRelaxMin).apply();
            intent.putExtra("timeRelax", (timeRelaxMin * 60 + timeRelaxSek) * 1000L);
            intent.putExtra("attempt", attempt);
            startActivity(intent);

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.toast_fail, Toast.LENGTH_SHORT).show();
            editTextTimeSek.setText(String.format(Locale.getDefault(), "%02d", sharedPreferences.getInt("timeRelaxSek", 60)));
            editTextTimeMin.setText(String.format(Locale.getDefault(), "%02d", sharedPreferences.getInt("timeRelaxMin", 0)));
        }
    }


    void setListenersEditText() {
        editTextTimeSek.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {

                    //Проверка на пустоту
                    String sekStr = editTextTimeSek.getText().toString().trim();
                    if (sekStr.isEmpty()) {
                        editTextTimeSek.setText(String.format(Locale.getDefault(), "%02d", 0));
                    }

                    //проверка на на значение
                    try {
                        int sek = Integer.parseInt(sekStr);
                        if (sek == 0 || sek > 59) {
                            int min = Integer.parseInt(editTextTimeMin.getText().toString().trim());
                            if (sek == 0 && min == 0) {
                                editTextTimeSek.setText(String.format(Locale.getDefault(), "%02d", 1));
                            }
                            if (sek > 59) {
                                editTextTimeSek.setText(String.format(Locale.getDefault(), "%02d", sek - 60));
                                editTextTimeMin.setText(String.format(Locale.getDefault(), "%02d", min + 1));
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
        editTextTimeMin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String minkStr = editTextTimeMin.getText().toString().trim();
                    if (minkStr.isEmpty()) {
                        editTextTimeMin.setText(String.format(Locale.getDefault(), "%02d", 0));
                    }


                }

            }
        });


        editTextAttempt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String attemptStr = editTextAttempt.getText().toString().trim();
                    if (attemptStr.isEmpty()) {
                        editTextAttempt.setText(String.format(Locale.getDefault(), "%d", 1));
                    } else {
                        int attemptInt = Integer.parseInt(attemptStr);
                        if (attemptInt==0){
                            editTextAttempt.setText(String.format(Locale.getDefault(), "%d", 1));
                        }
                    }




                }
            }
        });
    }


}

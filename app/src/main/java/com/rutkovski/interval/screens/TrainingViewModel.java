package com.rutkovski.interval.screens;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rutkovski.interval.R;

import java.util.Locale;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;


public class TrainingViewModel extends AndroidViewModel {

    // Идентификатор уведомления
    private static final int NOTIFY_ID = 101;
    // Идентификатор канала
    private static String CHANNEL_ID = "intervalChannel";

    private TrainingTimer trainingTimer;
    private MutableLiveData<String> currentTime;
    private MutableLiveData<Boolean> isRunning;
    private MutableLiveData<Boolean> finishTimer;
    private int attemptNow;
    private SoundPool soundPool;
    private int soundIdEndTimer;
    private Vibrator vibrator;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;


    public TrainingViewModel(@NonNull Application application) {
        super(application);
        currentTime = new MutableLiveData<>();
        isRunning = new MutableLiveData<>();
        attemptNow = 0;
    }


    LiveData<String> getCurrentTime() {
        return currentTime;
    }

    LiveData<Boolean> getIsRunning() {
        return isRunning;
    }

    void countAddAttemptNow() {
        this.attemptNow++;
    }

    Integer getAttemptNow() {
        return attemptNow;
    }


    void createTimer(long millis) {
        if (trainingTimer == null) {
            soundPool = new SoundPool(0, AudioManager.STREAM_NOTIFICATION, 0);
            soundIdEndTimer = soundPool.load(getApplication(), R.raw.endtimer, 1);
            vibrator = (Vibrator) getApplication().getSystemService(Context.VIBRATOR_SERVICE);
            trainingTimer = new TrainingTimer(millis, 1000);
            Intent notificationIntent = new Intent(getApplication(), TrainingActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplication(),
                    0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder = new NotificationCompat.Builder(getApplication(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true);
            createNotificationChannel();
            setAndShowNotifyBreak();
        }

    }

    void cancelTimer() {
        trainingTimer.cancel();
        isRunning.setValue(false);
        setAndShowNotifyBreak();
    }

    void startTimer() {
        trainingTimer.start();
        isRunning.setValue(true);
        builder.setContentTitle(getApplication().getString(R.string.notify_title));
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (trainingTimer != null) {
            trainingTimer.cancel();
        }
        if (notificationManager!=null){
            notificationManager.cancelAll();
        }
        if (soundPool != null) {
            soundPool.release();
        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "workInterval", importance);
            channel.setDescription("WorkNotification");
            notificationManager = getApplication().getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }



    public class TrainingTimer extends CountDownTimer {
        TrainingTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            currentTime.setValue(getStringTime(l + 1000));
            builder.setContentText(getStringTime(l + 1000));
            notificationManager.notify(NOTIFY_ID, builder.build());
        }

        @Override
        public void onFinish() {
            isRunning.setValue(false);
            endTimerAlarm();

        }
    }

    private void endTimerAlarm() {
        soundPool.play(soundIdEndTimer, 1, 1, 1, 0, 1);

        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(100);
        }

        setAndShowNotifyBreak();

    }

    private void setAndShowNotifyBreak(){
            builder.setContentTitle(getApplication().getString(R.string.work))
                    .setContentText(getApplication().getString(R.string.notify_text_));
        notificationManager.notify(NOTIFY_ID, builder.build());
    }


    private String getStringTime(long time) {
        long secondsAll = time / 1000;
        int second = (int) secondsAll % 60;
        if (secondsAll > 59) {
            int minute = (int) (secondsAll % 3600) / 60;
            if (secondsAll > 3599) {
                int hours = (int) secondsAll / 3600;
                return String.format(Locale.getDefault(), "%d:%2d:%2d", hours, minute, second);
            } else {
                return String.format(Locale.getDefault(), getApplication().getString(R.string.format_timer), minute, second);
            }

        } else {
            return String.format(Locale.getDefault(), getApplication().getString(R.string.format_timer_sek), second);
        }
    }


}


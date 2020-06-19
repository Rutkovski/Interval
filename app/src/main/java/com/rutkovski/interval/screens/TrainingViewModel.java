package com.rutkovski.interval.screens;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rutkovski.interval.R;

import java.util.Locale;


public class TrainingViewModel extends AndroidViewModel {
    public final static int INTERRUPTED = 2;
    public final static int RUN = 1;
    public final static int NOT_RUN = 0;
    // Идентификатор уведомления
    private static final int NOTIFY_ID = 101;
    // Идентификатор канала
    private static String CHANNEL_ID = "intervalChannel";
    private TrainingTimer trainingTimer;
    private MutableLiveData<String> currentTime;
    private MutableLiveData<Integer> isRunning;
    private int attemptNow;
    private int attemptAll;
    private SoundPool soundPool;
    private int soundIdEndTimer;

    private  NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private Vibrator vibrator;
    private Intent notificationIntent;
    private PendingIntent contentIntent;



    public TrainingViewModel(@NonNull Application application) {
        super(application);
        currentTime = new MutableLiveData<>();
        isRunning = new MutableLiveData<>();
        isRunning.setValue(NOT_RUN);
        attemptNow = 0;
        soundPool = new SoundPool(0, AudioManager.STREAM_MUSIC, 0);
        soundIdEndTimer = soundPool.load(getApplication(), R.raw.endtimer, 1);


        vibrator = (Vibrator) getApplication().getSystemService(Context.VIBRATOR_SERVICE);

        notificationIntent = new Intent(getApplication(), TrainingActivity.class);
        contentIntent = PendingIntent.getActivity(getApplication(),
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder = new NotificationCompat.Builder(getApplication(),CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotificationChannel();
    }




    LiveData<String> getCurrentTime() {
        return currentTime;
    }

    LiveData<Integer> getIsRunning() {
        return isRunning;
    }

    void countAddAttemptNow() {
        this.attemptNow++;
    }


    void setAttemptAll(int attemptAll) {
        this.attemptAll = attemptAll;
    }

    String counterString() {
        return String.format(getApplication().getString(R.string.count_work), attemptNow, attemptAll);
    }


    void createTimer(long millis) {
        if (trainingTimer == null) {
            trainingTimer = new TrainingTimer(millis, 1000);
        }
        builder.setContentTitle(getApplication().getString(R.string.work))
                .setContentText(getApplication().getString(R.string.notify_text_));
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    void cancelTimer() {
        trainingTimer.cancel();
        isRunning.setValue(INTERRUPTED);
        builder.setContentTitle(getApplication().getString(R.string.work))
                .setContentText(getApplication().getString(R.string.notify_text_));
        notificationManager.notify(NOTIFY_ID, builder.build());
        trainingTimer.cancel();
    }

    void startTimer() {
        trainingTimer.start();
        isRunning.setValue(RUN);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        if (soundPool!=null) {
            soundPool.release();
            soundPool = null;
        }
        if (trainingTimer!=null) {
            trainingTimer.cancel();
        }
        notificationManager.cancelAll();
    }

    public class TrainingTimer extends CountDownTimer {

        TrainingTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        @Override
        public void onTick(long l) {
            currentTime.setValue(getStringTime(l + 1000));
            builder.setContentTitle(getApplication().getString(R.string.notify_title))
                    .setContentText(getStringTime(l + 1000));
            notificationManager.notify(NOTIFY_ID, builder.build());



        }

        @Override
        public void onFinish() {
            isRunning.setValue(NOT_RUN);

            builder.setContentTitle(getApplication().getString(R.string.work))
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setLights(1,1000,1000)
//                   // .setSound(R.raw.endtimer)
                    .setContentText(getApplication().getString(R.string.notify_text_));

            notificationManager.notify(NOTIFY_ID, builder.build());

            soundPool.play(soundIdEndTimer, 1, 1, 1, 0, 1);


            if (vibrator!=null&&vibrator.hasVibrator()) {
                vibrator.vibrate(300);
            }


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


}


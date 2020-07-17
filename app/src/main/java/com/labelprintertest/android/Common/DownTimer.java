package com.labelprintertest.android.Common;

import android.os.CountDownTimer;

/**
 *
 * Custom Down Timer Class
 *
 */
public class DownTimer {
    public interface OnFinishListener{
        public void onFinish();
        public void onTick(int progressValue);
    }

    public long startTime = 20000;
    public long remainTime;
    private long timeInterval = 100;
    public boolean isRunning = false;
    public boolean isfinished = false;
    private MyTimer timer;
    private OnFinishListener listener;

    public DownTimer(long startTime){
        this.startTime = startTime;
        remainTime = startTime;
    }

    public DownTimer(long startTime, long timeInterval){
        this.startTime = startTime;
        this.timeInterval = timeInterval;
        remainTime = startTime;
    }

    public void update(long startTime){
        initialize();
        this.startTime = startTime;
        remainTime = startTime;
    }

    public void start(){
        if (timer != null)
            timer.cancel();
        isRunning = true;
        isfinished = false;
        timer = new MyTimer(remainTime, timeInterval);
        timer.start();

    }

    public void pause(){
        if (timer == null)
            return;
        timer.cancel();
        timer = null;
        isRunning = false;
    }

    public void initialize(){
        this.isfinished = false;
        this.isRunning = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        this.remainTime = this.startTime;
    }

    public void setOnFinishListener(OnFinishListener listener){
        this.listener = listener;
    }

    private class MyTimer extends CountDownTimer {

        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            remainTime = millisUntilFinished;
            if (listener != null)
                listener.onTick(100 - (int) ((float) remainTime/ (float) startTime * 100f));
        }

        @Override
        public void onFinish() {
            remainTime = 0;
            isRunning = false;
            isfinished = true;
            if (listener != null)
                listener.onFinish();
        }
    }
}

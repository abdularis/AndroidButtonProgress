package com.github.abdularis.sample;

import android.os.Handler;
import android.widget.TextView;

import com.github.abdularis.buttonprogress.DownloadButtonProgress;

/**
 * Created by abdularis on 07/01/18.
 */

class SampleTask implements Runnable {

    private DownloadButtonProgress btnProgress;
    private TextView subtitle;
    private Handler handler;
    private int progress = 0;

    SampleTask(Handler handler, DownloadButtonProgress btnProgress, TextView subtitle) {
        this.handler = handler;
        this.btnProgress = btnProgress;
        this.subtitle = subtitle;
    }

    @Override
    public void run() {
        try {
            setBtnIndeterminate();

            Thread.sleep(2000);
            setBtnDeterminate();
            while (progress <= 100) {
                Thread.sleep(30);
                progress++;
                setBtnProgress();
            }
            setBtnFinish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setBtnIndeterminate() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                subtitle.setText("Indeterminate");
                btnProgress.setIndeterminate();
            }
        });
    }

    private void setBtnDeterminate() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                subtitle.setText("Determinate");
                btnProgress.setDeterminate();
            }
        });
    }

    private void setBtnProgress() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                btnProgress.setCurrentProgress(progress);
            }
        });
    }

    private void setBtnFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                subtitle.setText("Finish");
                btnProgress.setFinish();
            }
        });
    }
}
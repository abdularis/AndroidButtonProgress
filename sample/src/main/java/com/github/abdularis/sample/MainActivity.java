package com.github.abdularis.sample;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.abdularis.buttonprogress.DownloadButtonProgress;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListenerFor(R.id.button_progress_1, R.id.subtitle_1);
        setListenerFor(R.id.button_progress_2, R.id.subtitle_2);
    }

    private void setListenerFor(int btnId, int subtitleId) {
        final DownloadButtonProgress btn = findViewById(btnId);
        final TextView subtitle = findViewById(subtitleId);

        btn.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                new Thread(new SampleTask(new Handler(), btn, subtitle)).start();
            }

            @Override
            public void onCancelButtonClick(View view) {
                // called when cancel button/icon is clicked
            }

            @Override
            public void onFinishButtonClick(View view) {
                // called when finish button/icon is clicked
            }
        });
    }

}

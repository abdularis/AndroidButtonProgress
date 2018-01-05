package com.github.abdularis.sample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.abdularis.buttonprogress.DownloadButtonProgress;

public class MainActivity extends AppCompatActivity {

    DownloadButtonProgress btn;
    boolean cancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button_progress);

        btn.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                cancelled = false;
                btn.setIndeterminate();
                new FakeDownloadTask().execute();
            }

            @Override
            public void onCancelButtonClick(View view) {
                cancelled = true;
                btn.setIdle();
            }

            @Override
            public void onFinishButtonClick(View view) {

            }
        });
    }

    class FakeDownloadTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int progress = 0;
            while (progress <= 100) {
                if (cancelled) break;

                try {
                    Thread.sleep(50);
                    publishProgress(progress);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                progress++;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            btn.setDeterminate();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (cancelled) return;
            btn.setFinish();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            btn.setProgress(values[0]);
        }
    }
}

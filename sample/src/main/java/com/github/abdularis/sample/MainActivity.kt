package com.github.abdularis.sample

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.abdularis.buttonprogress.DownloadButtonProgress
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListenerFor(button_progress_1)
        setListenerFor(button_progress_2)

        button_progress_1.addOnStateChangedListeners { newState ->
            if (newState == DownloadButtonProgress.STATE_FINISHED) {
                button_progress_1.visibility = View.GONE
            }

            subtitle_1.text = when (newState) {
                DownloadButtonProgress.STATE_IDLE -> "Idle"
                DownloadButtonProgress.STATE_INDETERMINATE -> "Indeterminate"
                DownloadButtonProgress.STATE_DETERMINATE -> "Determinate"
                DownloadButtonProgress.STATE_FINISHED -> "Finished"
                else -> ""
            }
        }

        button_progress_2.addOnStateChangedListeners { newState ->
            subtitle_2.text = when (newState) {
                DownloadButtonProgress.STATE_IDLE -> "Idle"
                DownloadButtonProgress.STATE_INDETERMINATE -> "Indeterminate"
                DownloadButtonProgress.STATE_DETERMINATE -> "Determinate"
                DownloadButtonProgress.STATE_FINISHED -> "Finished"
                else -> ""
            }
        }
    }

    private fun setListenerFor(downloadButtonProgress: DownloadButtonProgress) {
        downloadButtonProgress.addOnClickListener(object : DownloadButtonProgress.OnClickListener {
            override fun onIdleButtonClick(view: View) {
                Thread(SampleTask(Handler(), downloadButtonProgress)).start()
            }

            override fun onCancelButtonClick(view: View) {
                // called when cancel button/icon is clicked
            }

            override fun onFinishButtonClick(view: View) {
                // called when finish button/icon is clicked
            }
        })
    }
}
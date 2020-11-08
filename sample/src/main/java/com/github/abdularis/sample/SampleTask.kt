package com.github.abdularis.sample

import android.os.Handler
import com.github.abdularis.buttonprogress.DownloadButtonProgress

/**
 * Created by abdularis on 07/01/18.
 */
internal class SampleTask(
    private val handler: Handler,
    private val btnProgress: DownloadButtonProgress
) : Runnable {

    private var progress = 0

    override fun run() {
        try {
            setBtnIndeterminate()
            Thread.sleep(2000)
            setBtnDeterminate()
            while (progress <= 100) {
                Thread.sleep(30)
                progress++
                setBtnProgress()
            }
            setBtnFinish()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun setBtnIndeterminate() {
        handler.post {
            btnProgress.setIndeterminate()
        }
    }

    private fun setBtnDeterminate() {
        handler.post {
            btnProgress.setDeterminate()
        }
    }

    private fun setBtnProgress() {
        handler.post { btnProgress.currentProgress = progress }
    }

    private fun setBtnFinish() {
        handler.post {
            btnProgress.setFinish()
        }
    }
}
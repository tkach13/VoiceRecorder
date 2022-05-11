package com.benten.voicerecorder.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.benten.voicerecorder.R
import com.benten.voicerecorder.app.App.Companion.CHANNEL_ID
import java.io.File
import java.io.IOException

class RecorderService : Service() {

    private var recorder: MediaRecorder? = null


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val stopIntent = Intent(this, RecorderService::class.java).apply {
            putExtra(KEY_STOP_SERVICE, true)
        }
        val pendingIntent =
            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Recording audio")
            .setPriority(PRIORITY_MIN)
            .addAction(R.drawable.ic_baseline_stop_24, "Stop", pendingIntent)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .build()

        startForeground(1, notification)
        if (intent?.extras?.getBoolean(KEY_STOP_SERVICE) == true) {
            stopEverything()
        } else {
            startRecording()
        }
        return START_NOT_STICKY
    }

    private fun stopEverything() {
        stopSelf()
        recorder?.stop()
    }

    @SuppressLint("NewApi")
    private fun startRecording() {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        val file = File(cacheDir.path, "Recording ${System.currentTimeMillis()}.3gp")
        recorder!!.setOutputFile(file)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            recorder!!.prepare()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }
        recorder!!.start()
    }

    private fun stopRecording() {
        recorder!!.stop()
    }

    override fun onDestroy() {
        recorder = null
        super.onDestroy()
    }

    companion object {
        const val KEY_STOP_SERVICE = "KEY_STOP_SERVICE"
    }


}
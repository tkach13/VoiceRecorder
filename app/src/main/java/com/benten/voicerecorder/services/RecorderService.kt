package com.benten.voicerecorder.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RecorderService : Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}
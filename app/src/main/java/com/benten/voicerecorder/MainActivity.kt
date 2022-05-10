package com.benten.voicerecorder

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.benten.voicerecorder.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null

    private var recorder: MediaRecorder? = null


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnStartRecording.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (!it.isSelected) {
                    startRecording()
                    binding.btnStartRecording.text = "Stop recording"
                } else {
                    stopRecording()
                    binding.btnStartRecording.text = "Start recording"
                }
                it.isSelected = !it.isSelected


            } else {
                requestPermissions(arrayOf(RECORD_AUDIO), 23)
            }
        }

        binding.btnPlayFirst.setOnClickListener {
            startPlaying("${cacheDir.path}/Recording12.3gp")
        }
    }

    private fun stopRecording() {
        recorder!!.stop()
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startPlaying(fileName: String) {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }
        }
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
        val file = File(cacheDir.path, "Recording12.3gp")
        recorder!!.setOutputFile(file)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            recorder!!.prepare()
        } catch (e: IOException) {
            Log.e("LOG_TAG", "prepare() failed")
        }
        recorder!!.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 23) {
            if (permissions.contains(RECORD_AUDIO) && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                startRecording()
            }

        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
package com.benten.voicerecorder

import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaDataSource
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.benten.voicerecorder.adapterss.RecordingAdapter
import com.benten.voicerecorder.databinding.ActivityMainBinding
import com.benten.voicerecorder.services.RecorderService
import java.io.File
import java.io.IOException
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private var player: MediaPlayer? = null


    private lateinit var recordingAdapter: RecordingAdapter


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        val sharedPreferences = getSharedPreferences("MY_PREFERENCES", MODE_PRIVATE)
        binding.switchLastMinute.isChecked = sharedPreferences.getBoolean(KEY_LAST_MINUTE_CHECK, false)
        binding.switchLastMinute.setOnCheckedChangeListener { compoundButton, b ->
            sharedPreferences.edit().putBoolean(KEY_LAST_MINUTE_CHECK, b).apply()
        }
        binding.btnStartRecording.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(this, RecorderService::class.java)

                if (!it.isSelected) {
                    ContextCompat.startForegroundService(this, intent)
                    binding.btnStartRecording.text = "Stop recording"
                } else {
                    stopService(intent)
                    binding.btnStartRecording.text = "Start recording"
                }
                it.isSelected = !it.isSelected
            } else {
                requestPermissions(arrayOf(RECORD_AUDIO), VOICE_RECOORDER_PERMISSION_CODE)
            }
        }
    }

    private fun setupRecyclerView() {
        recordingAdapter = RecordingAdapter()
        binding.rvRecordings.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvRecordings.adapter = this.recordingAdapter
        recordingAdapter.setItemClickListener {
            startPlaying("${cacheDir.path}/$it")
        }
        updateRecycler()
    }

    private fun updateRecycler() {
        recordingAdapter.updateAll(cacheDir.listFiles().map {
            it.name
        })
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == VOICE_RECOORDER_PERMISSION_CODE) {
            if (permissions.contains(RECORD_AUDIO) && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    companion object {
        const val VOICE_RECOORDER_PERMISSION_CODE = 23
        const val KEY_LAST_MINUTE_CHECK = "KEY_LAST_MINUTE_CHECK"
    }
}
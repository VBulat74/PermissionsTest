package ru.com.bulat.permissionstest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.com.bulat.permissionstest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.requestCameraPermissionButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
                //Разрешение есть
                onCameraPermissionGranted()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    RQ_REMISSION_FOR_CAMERA,
                )
            }
        }

        binding.requestRecordAudioAndLocationPermissionsButton.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO),
                RQ_REMISSION_FOR_AUDIO_LOCATION,
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RQ_REMISSION_FOR_CAMERA -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCameraPermissionGranted()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        //show dialogs with explanation hear
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
                    } else {
                        //oops can't do anything
                        askUserForOpeningAppSettings()
                    }
                }
            }
            RQ_REMISSION_FOR_AUDIO_LOCATION -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "Location & Radio permission granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun askUserForOpeningAppSettings() {
        val appSettingIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null),
        )
        if (packageManager.resolveActivity(appSettingIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            Toast.makeText(this, R.string.permissions_denied_forever, Toast.LENGTH_SHORT).show()
        } else {
            AlertDialog.Builder(this)
                .setTitle(R.string.permission_denied)
                .setMessage(R.string.permission_denied_forever_message)
                .setPositiveButton("Open") {_, _->
                    startActivity(appSettingIntent)
                }
                .create()
                .show()
        }
    }

    private fun onCameraPermissionGranted(){
        Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val RQ_REMISSION_FOR_CAMERA = 1
        const val RQ_REMISSION_FOR_AUDIO_LOCATION = 2

    }
}
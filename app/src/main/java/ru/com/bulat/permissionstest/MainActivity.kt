package ru.com.bulat.permissionstest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.com.bulat.permissionstest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val permissionCameraLauncher  = registerForActivityResult(
        RequestPermission(),
        ::onGotCameraPermissionResult,
    )

    private val permissionLocationAndRadioLauncher  = registerForActivityResult(
        RequestMultiplePermissions(),
        ::onGotRecordAudioAndLocationPermissionsResult,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.requestCameraPermissionButton.setOnClickListener {
            permissionCameraLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.requestRecordAudioAndLocationPermissionsButton.setOnClickListener {
            permissionLocationAndRadioLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO)
            )
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

    private fun onGotCameraPermissionResult(granted: Boolean) {
        if (granted) {
            onCameraPermissionGranted()
        } else {
            // example of handling 'Deny & don't ask again' user choice
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                askUserForOpeningAppSettings()
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onGotRecordAudioAndLocationPermissionsResult(grantResults: Map<String, Boolean>) {
        if (grantResults.entries.all { it.value }) {
            onRecordAudioAndLocationPermissionsGranted()
        }
    }

    private fun onRecordAudioAndLocationPermissionsGranted() {
        Toast.makeText(this, R.string.audio_and_location_permissions_granted, Toast.LENGTH_SHORT).show()
    }

    private fun onCameraPermissionGranted(){
        Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val RQ_REMISSION_FOR_CAMERA = 1
        const val RQ_REMISSION_FOR_AUDIO_LOCATION = 2

    }
}
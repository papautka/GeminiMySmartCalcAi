package com.uteev.geminimysmartcalcai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.uteev.geminimysmartcalcai.ui.theme.GeminiMySmartCalcAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GeminiMySmartCalcAiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    var selectedImageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
                    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                        uri?.let {
                            val inputStream = contentResolver.openInputStream(it)
                            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                            selectedImageBitmap = bitmap
                        }
                    }

                    val requestPermissionLauncher = rememberLauncherForActivityResult(
                        RequestPermission()
                    ) { isGranted: Boolean ->
                        if (isGranted) {
                            galleryLauncher.launch("image/*")
                        } else {
                            // Handle permission denied
                        }
                    }

                    BakingScreen(
                        selectedImageBitmap = selectedImageBitmap,
                        onSelectImageClick = {
                            when {
                                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                                    galleryLauncher.launch("image/*")
                                }
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                                else -> {
                                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            }
                        },
                        onBackClick = {
                            selectedImageBitmap = null
                        }
                    )
                }
            }
        }
    }
}

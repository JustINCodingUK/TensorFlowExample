package io.github.JustINCodingUK.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.Camera
import androidx.camera.core.ImageAnalysis
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.camera.lifecycle.ProcessCameraProvider

class CameraActivity: ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            createCameraView()
        }
    }

    @Composable
    fun createCameraView(){

    }

    @Composable
    @Preview(showBackground = true)
    fun createCameraPreview(){
        createCameraView()
    }


}
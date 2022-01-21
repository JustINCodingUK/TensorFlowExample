package io.github.JustINCoding

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

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
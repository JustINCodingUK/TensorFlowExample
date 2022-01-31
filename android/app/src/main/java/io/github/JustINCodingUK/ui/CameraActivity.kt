package io.github.JustINCodingUK.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.JustINCoding.R
import android.util.Log
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import io.github.JustINCodingUK.logic.ImageAnalyzer
import io.github.JustINCodingUK.logic.recogmodel.RecognitionModel
import java.util.concurrent.Executors
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.camera.camera2.Camera2Config
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat


class CameraActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSIONS = 999
    private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    private val TAG = "CameraActivity"
    private lateinit var preview: androidx.camera.core.Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var cam: Camera
    private var camExec = Executors.newSingleThreadExecutor()
    private val recognitionModel = RecognitionModel()

    private val camPreview by lazy {
        findViewById<PreviewView>(R.id.viewFinder)
    }

    @androidx.camera.lifecycle.ExperimentalCameraProviderConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        if(camPerms()){
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                    this, PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun camPerms(): Boolean = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @androidx.camera.lifecycle.ExperimentalCameraProviderConfiguration
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (camPerms()) {
                startCamera()
            } else {
                Toast.makeText(
                        this,
                        "This app cannot work without a camera. Exiting now",
                        Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    @androidx.camera.lifecycle.ExperimentalCameraProviderConfiguration
    private fun startCamera(){
        ProcessCameraProvider.configureInstance(Camera2Config.defaultConfig())
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            preview = androidx.camera.core.Preview.Builder().build()
            imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(224,224))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysisUseCase: ImageAnalysis ->
                        analysisUseCase.setAnalyzer(camExec, ImageAnalyzer(this) { items ->
                            recognitionModel.update(items)
                        })
                    }

            val camSelector = if(cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA))
                CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cam = cameraProvider.bindToLifecycle(this, camSelector, preview, imageAnalysis)
                preview.setSurfaceProvider(camPreview.surfaceProvider)

            }catch (e: Exception){ Log.e(TAG, e.toString())}

        }, ContextCompat.getMainExecutor(this))
    }
}


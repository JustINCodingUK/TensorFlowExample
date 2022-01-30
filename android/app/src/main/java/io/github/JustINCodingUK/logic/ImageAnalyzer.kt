package io.github.JustINCodingUK.logic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.util.Size
import androidx.activity.viewModels
import androidx.camera.core.Camera
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.impl.ImageAnalysisConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import io.github.JustINCodingUK.logic.recogmodel.Recognition
import io.github.JustINCoding.ml.Classifier
import io.github.JustINCodingUK.logic.recogmodel.RecognitionModel
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.concurrent.Executors

class ImageAnalyzer(ctx: Context): ImageAnalysis.Analyzer {
    private var contextHolder = ctx
    private lateinit var bmpToBuffer: Bitmap
    private var imageClassifier: ImageClassify = ImageClassify(ctx)
    private lateinit var rotationMatrix: Matrix
    private lateinit var outputs: TensorBuffer
    private lateinit var listOfOutputs: List<Float>
    private lateinit var preview: androidx.camera.core.Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var cam: Camera
    private var camExec = Executors.newSingleThreadExecutor()
    private val recognitionModel = RecognitionModel()

    private val modelInstance: Classifier by lazy {
        val compatList = CompatibilityList()
        val options = if (compatList.isDelegateSupportedOnThisDevice){
            Model.Options.Builder().setDevice(Model.Device.GPU).build()
        }else{
            Model.Options.Builder().setNumThreads(4).build()
        }

        Classifier.newInstance(ctx)
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        val items = mutableListOf<Recognition>()
        val tfImage = TensorImage.fromBitmap(toBitmap(image))
        outputs = modelInstance.process(tfImage.tensorBuffer).outputFeature0AsTensorBuffer
        val recognition = Recognition(getLabelOfMaxOfOutputs(), returnNonNullFloat(listOfOutputs.maxOrNull()))
        items.add(recognition)
        Log.d("Image Classification", outputs.getFloatValue(1).toString())
    }

    private fun returnNonNullFloat(float: Float?): Float{
        if(float==null){
            error("Confidence is null")
        }else{
            return float
        }
    }

    private fun getLabelOfMaxOfOutputs(): String {
        listOfOutputs = mutableListOf<Float>(outputs.getFloatValue(0), outputs.getFloatValue(1), outputs.getFloatValue(2), outputs.getFloatValue(3))
        when(listOfOutputs.maxOrNull()){
            outputs.getFloatValue(0) -> return "Apple"
            outputs.getFloatValue(1) -> return "Banana"
            outputs.getFloatValue(2) -> return "Orange"
            outputs.getFloatValue(3) -> return "Mixed"
            else -> {
                return ""
                error("Could not get max confidence")
            }
        }
    }

    @SuppressLint("")
    @androidx.camera.core.ExperimentalGetImage
    private fun toBitmap(img: ImageProxy): Bitmap?{
        val image = img.image ?: return null
        if(!::bmpToBuffer.isInitialized){
            rotationMatrix = Matrix()
            rotationMatrix.postRotate(img.imageInfo.rotationDegrees.toFloat())
            bmpToBuffer = Bitmap.createBitmap(
                    img.width, img.height, Bitmap.Config.ARGB_8888
            )
        }

        imageClassifier.yuvToRgb(image, bmpToBuffer)

        return Bitmap.createBitmap(
                bmpToBuffer,
                0,
                0,
                bmpToBuffer.width,
                bmpToBuffer.height,
                rotationMatrix,
                false
        )

    }

    fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(contextHolder)
        /* TODO Fix this snippet, too many arguments
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()
            imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(224,224))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysisUseCase: ImageAnalysis ->
                        analysisUseCase.setAnalyzer(camExec, ImageAnalyzer(contextHolder) { items ->
                            recognitionModel.update(items)
                        })
                    }
        })*/
    }

}
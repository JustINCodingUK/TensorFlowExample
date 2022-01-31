package io.github.JustINCodingUK.logic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import io.github.JustINCodingUK.logic.recogmodel.Recognition
import io.github.JustINCoding.ml.Classifier
import org.tensorflow.lite.DataType
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

typealias RecognitionListener = (recognition: List<Recognition>) -> Unit

class ImageAnalyzer(ctx: Context, private val listener: RecognitionListener): ImageAnalysis.Analyzer {
    private var contextHolder = ctx
    private lateinit var bmpToBuffer: Bitmap
    private var imageClassifier: ImageClassify = ImageClassify(ctx)
    private lateinit var rotationMatrix: Matrix
    private lateinit var outputs: TensorBuffer
    private lateinit var listOfOutputs: List<Float>

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

        Log.w("Data Type", tfImage.dataType.toString())
        outputs = modelInstance.process(tfImage.tensorBuffer).outputFeature0AsTensorBuffer
        val recognition = Recognition(getLabelOfMaxOfOutputs(), returnNonNullFloat(listOfOutputs.maxOrNull()))
        items.add(recognition)

        Log.d("Image Classification", outputs.getFloatValue(1).toString())
        listener(items)
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



}
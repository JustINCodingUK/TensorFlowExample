package io.github.JustINCoding.logic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.speech.RecognitionListener;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

import io.github.JustINCoding.ml.Model;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {

    private Matrix rotationMatrix;
    private Bitmap bmpBuffer;
    private ImageClassify classifier;
    private Model modelInstance;

    public ImageAnalyzer(Context context, RecognitionListener listener) throws IOException {
        modelInstance = Model.newInstance(context);
        classifier = new ImageClassify(context);
    }

    @Override
    public void analyze(ImageProxy imageProxy){
        TensorImage tfImage = TensorImage.fromBitmap(toBitmap(imageProxy));
        // TODO String output = modelInstance.process()
        //        .getOutputFeature0AsTensorBuffer();
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    public Bitmap toBitmap(ImageProxy imageProxy){
        Image image = imageProxy.getImage();
        rotationMatrix = new Matrix();
        rotationMatrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());
        bmpBuffer = Bitmap.createBitmap(
                imageProxy.getWidth(),
                imageProxy.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        classifier.yuvToRgb(image, bmpBuffer);
        return Bitmap.createBitmap(
                bmpBuffer,
                0,
                0,
                bmpBuffer.getWidth(),
                bmpBuffer.getHeight(),
                rotationMatrix,
                false
        );
    }
}

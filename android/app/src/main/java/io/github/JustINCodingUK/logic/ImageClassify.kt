package io.github.JustINCodingUK.logic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import io.github.JustINCoding.BuildConfig
import java.nio.ByteBuffer

class ImageClassify(context: Context) {
    private val rs = RenderScript.create(context)
    private val scriptYuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))

    private var pixels: Int = -1
    private lateinit var yuvBuffer: ByteBuffer
    private lateinit var inputAllocation: Allocation
    private lateinit var outputAllocation: Allocation

    @Synchronized
    fun yuvToRgb(image: Image, output: Bitmap){
        if(!::yuvBuffer.isInitialized){
            pixels = image.cropRect.width() * image.cropRect.height()
            val pixelSize = ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888)
            yuvBuffer = ByteBuffer.allocateDirect(4*32*180*180*pixelSize)
        }

        yuvBuffer.rewind()
    }

    fun imageToByteBuffer(image: Image, outputBuffer: ByteArray){
        if(BuildConfig.DEBUG && image.format != ImageFormat.YUV_420_888){
            error("Bad Image Format")
        }

        val imageCrop = image.cropRect
        val imagePlanes = image.planes

        imagePlanes.forEachIndexed { planeIndex, plane ->
            var outputStride: Int
            var outputOffset: Int

            when(planeIndex) {
                0 -> {
                    outputStride = 1
                    outputOffset = 0
                }
                1 -> {
                    outputStride = 2
                    outputOffset = pixels + 1
                }
                2 -> {
                    outputStride = 2
                    outputOffset = pixels
                }
                else -> {
                    return@forEachIndexed
                }
            }

            val planeBuffer = plane.buffer
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride

            val planeCrop = if(planeIndex == 0){
                imageCrop
            } else {
                Rect(
                        imageCrop.left / 2,
                        imageCrop.top / 2,
                        imageCrop.right / 2,
                        imageCrop.bottom / 2
                )
            }

            val planesWidth = planeCrop.width()
            val planesHeight = planeCrop.height()

            val rowBuffer = ByteArray(plane.rowStride)

            val rowLength = if(pixelStride == 1 && outputStride == 1){
                planesWidth
            } else {
                (planesWidth-1)*pixelStride+1
            }

            for(row in 0 until planesHeight){
                planeBuffer.position(
                        (row + planeCrop.top)*rowStride + planeCrop.left * pixelStride
                )

                if(pixelStride==1 && outputStride==1){
                    planeBuffer.get(outputBuffer, outputOffset, rowLength)
                    outputOffset+=rowLength
                } else {
                    planeBuffer.get(rowBuffer, 0, rowLength)
                    for(col in 0 until planesWidth){
                        outputBuffer[outputOffset] = rowBuffer[col*pixelStride]
                        outputOffset+=outputStride
                    }
                }
            }

        }
    }



}
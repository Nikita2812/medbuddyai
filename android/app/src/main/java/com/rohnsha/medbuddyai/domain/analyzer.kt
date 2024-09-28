package com.rohnsha.medbuddyai.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.rohnsha.medbuddyai.domain.dataclass.classification
import com.rohnsha.medbuddyai.ml.BrainSegmentationv2
import com.rohnsha.medbuddyai.ml.BreastSegmentationv1
import com.rohnsha.medbuddyai.ml.ColonSegmentationv1
import com.rohnsha.medbuddyai.ml.KidneySegmentationv1
import com.rohnsha.medbuddyai.ml.LeukemaSegmentationv1
import com.rohnsha.medbuddyai.ml.OralSegmentationv1
import com.rohnsha.medbuddyai.ml.SkinSegmentationv1
import com.rohnsha.medbuddyai.ml.XrayClfV1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class analyzer(
    private val context: Context,
    private val onResults: (classification) -> Unit,
    private val index: Int
): ImageAnalysis.Analyzer {

    private var frameSkipCounter= 0

    override fun analyze(image: ImageProxy) {
        if (frameSkipCounter%60==0){
            val matrix = Matrix().apply {
                postRotate(image.imageInfo.rotationDegrees.toFloat())
            }
            val rotatedBitmap = Bitmap.createBitmap(
                image.toBitmap(),
                0,
                0,
                image.width,
                image.height,
                matrix,
                true
            )
            var tensorImage= TensorImage(DataType.FLOAT32)
            tensorImage.load(rotatedBitmap)
            val imageProcessor= ImageProcessor.Builder()
                .add(ResizeOp(256, 256, ResizeOp.ResizeMethod.BILINEAR))
                .build()
            tensorImage= imageProcessor.process(tensorImage)
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)
            Log.d("inputString", "indexAnalyzer: $index")
            val outputFeature0 = when(index){
                0 -> { LeukemaSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                1 -> { ColonSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                2 -> { OralSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                3-> { BrainSegmentationv2.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                4 -> { BreastSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                6 -> { XrayClfV1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                7 -> { XrayClfV1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                8 -> { SkinSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                9 -> { SkinSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                11 -> { KidneySegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
                else -> { XrayClfV1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            }
            val results= classification(
                getMaxIndex(outputFeature0.floatArray), outputFeature0.floatArray[getMaxIndex(
                outputFeature0.floatArray
            )] * 100)
            Log.d("inputString", "result: $results")
            onResults(results)
            Log.d("analyzerSuccess", results.indexNumber.toString())
        }
        frameSkipCounter++
        image.close()
    }

    private fun getMaxIndex(floatArray: FloatArray?): Int {
        return floatArray?.withIndex()?.maxByOrNull { it.value }?.index ?: -1
    }


}
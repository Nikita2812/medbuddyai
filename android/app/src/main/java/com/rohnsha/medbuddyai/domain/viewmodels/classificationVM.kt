package com.rohnsha.medbuddyai.domain.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohnsha.medbuddyai.domain.dataclass.classification
import com.rohnsha.medbuddyai.ml.BreastCancerV1
import com.rohnsha.medbuddyai.ml.BreastSegmentationv1
import com.rohnsha.medbuddyai.ml.ColonCacnerV1
import com.rohnsha.medbuddyai.ml.ColonSegmentationv1
import com.rohnsha.medbuddyai.ml.GiomaTumorv1
import com.rohnsha.medbuddyai.ml.KidneySegmentationv1
import com.rohnsha.medbuddyai.ml.KidneyTumorV1
import com.rohnsha.medbuddyai.ml.LeukEarlyV1
import com.rohnsha.medbuddyai.ml.LeukProV1
import com.rohnsha.medbuddyai.ml.LeukemaSegmentationv1
import com.rohnsha.medbuddyai.ml.LungSccV1
import com.rohnsha.medbuddyai.ml.LungsActV1
import com.rohnsha.medbuddyai.ml.MeningiomaTumorv1
import com.rohnsha.medbuddyai.ml.OralCacnerV1
import com.rohnsha.medbuddyai.ml.OralSegmentationv1
import com.rohnsha.medbuddyai.ml.PituitaryTumorv1
import com.rohnsha.medbuddyai.ml.PneumoniaV4
import com.rohnsha.medbuddyai.ml.SkinAcneV1
import com.rohnsha.medbuddyai.ml.SkinCancerV1
import com.rohnsha.medbuddyai.ml.SkinEzcamaV1
import com.rohnsha.medbuddyai.ml.SkinInfectionV1
import com.rohnsha.medbuddyai.ml.SkinPigmentationV1
import com.rohnsha.medbuddyai.ml.SkinSegmentationv1
import com.rohnsha.medbuddyai.ml.TuberculosisV1
import com.rohnsha.medbuddyai.ml.XrayClfV1
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class classificationVM: ViewModel() {

    fun classify(
        context: Context,
        bitmap: Bitmap,
        scanOption: Int,
        index: Int=0
    ): List<classification> {
        var predictedClasses:List<classification>

        var tensorImage= TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        var imageProcessor= ImageProcessor.Builder()
            .add(ResizeOp(256, 256, ResizeOp.ResizeMethod.BILINEAR))
            .build()
        tensorImage= imageProcessor.process(tensorImage)
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)
        Log.d("inputString", "scanOption: $scanOption, index: $index")

        predictedClasses= when(scanOption){
            0 -> { predictionLeukemia(inputFeature0 = inputFeature0, context = context) }
            1 -> { predictionColon(inputFeature0 = inputFeature0, context = context) }
            2 -> { predictionOral(inputFeature0 = inputFeature0, context = context) }
            3 -> { predictionBrain(inputFeature0 = inputFeature0, context = context) }
            4 -> { predictionBreast(inputFeature0 = inputFeature0, context = context) }
            6 -> { predictionLungs(context = context, bitmap = bitmap) }
            7 -> { predictionLungCancerous(context = context, inputFeature0 = inputFeature0) }
            8 -> { predictionSkin(context = context, inputFeature0 = inputFeature0) }
            9 -> { predictionSkinCancerous(context = context, inputFeature0 = inputFeature0) }
            11 -> { predictionKidney(inputFeature0 = inputFeature0, context = context) }
            999 -> { predictionMaster(context = context, inputFeature0 = inputFeature0, index = index)}
            else -> { emptyList() }
        }

        return predictedClasses
    }

    private fun predictionSkinCancerous(
        context: Context,
        inputFeature0: TensorBuffer
    ): List<classification> {
        val predictedClass= mutableListOf<classification>()
        val outputputPre= SkinCancerV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
        val maxIndexPre= getMaxIndex(outputputPre.floatArray)
        predictedClass.add(
            classification(maxIndexPre, outputputPre.floatArray[maxIndexPre]*100, 0)
        )
        return  predictedClass
    }

    private fun predictionLungCancerous(context: Context, inputFeature0: TensorBuffer,): List<classification> {
        val predictedClass= mutableListOf<classification>()
        val outputputPre= LungsActV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
        val maxIndexPre= getMaxIndex(outputputPre.floatArray)
        predictedClass.add(
            classification(maxIndexPre, outputputPre.floatArray[maxIndexPre]*100, 0)
        )
        val outputputPro= LungSccV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
        val maxIndexPro= getMaxIndex(outputputPre.floatArray)
        predictedClass.add(
            classification(maxIndexPro, outputputPro.floatArray[maxIndexPro]*100, 1)
        )
        return predictedClass
    }

    private fun predictionLeukemia(
        inputFeature0: TensorBuffer,
        context: Context
    ): List<classification> {
        val predictedClass= mutableListOf<classification>()
        val outputputPre= LeukEarlyV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
        val maxIndexPre= getMaxIndex(outputputPre.floatArray)
        predictedClass.add(
            classification(maxIndexPre, outputputPre.floatArray[maxIndexPre]*100, 0)
        )
        val outputputPro= LeukProV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
        val maxIndexPro= getMaxIndex(outputputPre.floatArray)
        predictedClass.add(
            classification(maxIndexPro, outputputPro.floatArray[maxIndexPro]*100, 1)
        )
        return predictedClass
    }

    private fun predictionBreast(
        inputFeature0: TensorBuffer,
        context: Context
    ): List<classification> {
        val predictedClass= mutableListOf<classification>()
        val outputBreast= BreastCancerV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
        val maxIndexBreast= getMaxIndex(outputBreast.floatArray)
        predictedClass.add(
            classification(maxIndexBreast, outputBreast.floatArray[maxIndexBreast]*100, 0)
        )

        return predictedClass
    }

    private fun predictionKidney(
        inputFeature0: TensorBuffer,
        context: Context
    ): List<classification> {
        val predictedClass= mutableListOf<classification>()
        val outputKidney= KidneyTumorV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
        val maxIndexKidney= getMaxIndex(outputKidney.floatArray)
        predictedClass.add(
            classification(maxIndexKidney, outputKidney.floatArray[maxIndexKidney]*100, 0)
        )

        return predictedClass
    }

    private fun predictionOral(
        inputFeature0: TensorBuffer,
        context: Context
    ): List<classification> {
        val predictedClass= mutableListOf<classification>()
        val outputOral= OralCacnerV1.newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
        val maxIndexOral= getMaxIndex(outputOral.floatArray)
        predictedClass.add(
            classification(
                indexNumber = maxIndexOral,
                confident = outputOral.floatArray[getMaxIndex(outputOral.floatArray)]*100,
                parentIndex = 0
            ),
        )

        return predictedClass
    }

    private fun predictionColon(inputFeature0: TensorBuffer, context: Context): List<classification> {
        val predictedClasses= mutableListOf<classification>()
        val outputColon= ColonCacnerV1.newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
        val maxIndexColon= getMaxIndex(outputColon.floatArray)
        predictedClasses.add(
            classification(
                indexNumber = maxIndexColon,
                confident = outputColon.floatArray[getMaxIndex(outputColon.floatArray)]*100,
                parentIndex = 0
            ),
        )
        return predictedClasses
    }

    fun predictionSkin(context: Context, inputFeature0: TensorBuffer): List<classification>{
        val predictedClasses= mutableListOf<classification>()

        val skinAcne= SkinAcneV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
            .floatArray
        val maxAcne= getMaxIndex(skinAcne)
        predictedClasses.add(
            classification(maxAcne, skinAcne[maxAcne]*100, 0)
        )

        val skinEzima= SkinEzcamaV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
            .floatArray
        val maxEzima= getMaxIndex(skinEzima)
        predictedClasses.add(
            classification(maxEzima, skinEzima[maxEzima]*100, 1)
        )

        val infection= SkinInfectionV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
            .floatArray
        val maxInfection= getMaxIndex(infection)
        predictedClasses.add(
            classification(maxInfection, infection[maxInfection], 2)
        )

        val pigmentation= SkinPigmentationV1
            .newInstance(context)
            .process(inputFeature0)
            .outputFeature0AsTensorBuffer
            .floatArray
        val maxPigmentation= getMaxIndex(pigmentation)
        predictedClasses.add(
            classification(maxPigmentation, pigmentation[maxPigmentation], 3)
        )

        return predictedClasses
    }

    fun predictionBrain(
        inputFeature0: TensorBuffer,
        context: Context
    ): List<classification>{
        val predictedClassesLungs= mutableListOf<classification>()

        viewModelScope.launch {
            val giomaTumorArray= GiomaTumorv1.newInstance(context)
                .process(inputFeature0)
                .outputFeature0AsTensorBuffer
                .floatArray
            val maxKeyGioma= getMaxIndex(giomaTumorArray)
            predictedClassesLungs.add(
                classification(
                    indexNumber = maxKeyGioma,
                    confident = giomaTumorArray[maxKeyGioma]*100,
                    parentIndex = 0
                )
            )
            val pituitaryTumorArr= PituitaryTumorv1.newInstance(context)
                .process(inputFeature0)
                .outputFeature0AsTensorBuffer
                .floatArray
            val maxKeyPituitary= getMaxIndex(pituitaryTumorArr)
            predictedClassesLungs.add(
                classification(
                    indexNumber = maxKeyPituitary,
                    confident = pituitaryTumorArr[maxKeyPituitary]*100,
                    parentIndex = 1
                )
            )
            val meningiomaTumorArr= MeningiomaTumorv1.newInstance(context)
                .process(inputFeature0)
                .outputFeature0AsTensorBuffer
                .floatArray
            val maxKeyMeningioma= getMaxIndex(meningiomaTumorArr)
            predictedClassesLungs.add(
                classification(
                    indexNumber = maxKeyMeningioma,
                    confident = meningiomaTumorArr[maxKeyMeningioma],
                    parentIndex = 2
                )
            )
        }

        return predictedClassesLungs
    }

    fun predictionLungs(
        context: Context,
        bitmap: Bitmap
    ): List<classification> {
        val predictedClassesLungs= mutableListOf<classification>()

        viewModelScope.launch {
            val modelPneumonia= PneumoniaV4.newInstance(context)
            var tensorImage= TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            var imageProcessor= ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .build()
            tensorImage= imageProcessor.process(tensorImage)
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(tensorImage.buffer)
            val outputPneumonia= modelPneumonia.process(inputFeature0).outputFeature0AsTensorBuffer
            val maxIndexPneumonia= getMaxIndex(outputPneumonia.floatArray)
            predictedClassesLungs.add(
                classification(
                    indexNumber = maxIndexPneumonia,
                    confident = outputPneumonia.floatArray[getMaxIndex(outputPneumonia.floatArray)]*100,
                    parentIndex = 0
                ),
            )
            Log.d("successIndex", predictedClassesLungs.toString())

            val modelTuberCulosis= TuberculosisV1.newInstance(context)
            val outputTuberculosis= modelTuberCulosis.process(inputFeature0).outputFeature0AsTensorBuffer
            val maxIndexTuberCulosis= getMaxIndex(outputTuberculosis.floatArray)
            predictedClassesLungs.add(
                classification(
                    indexNumber = maxIndexTuberCulosis,
                    confident = outputTuberculosis.floatArray[getMaxIndex(outputTuberculosis.floatArray)]*100,
                    parentIndex = 1
                )
            )
            modelPneumonia.close()
            modelTuberCulosis.close()
        }

        return predictedClassesLungs
    }

    fun predictionMaster(
        inputFeature0: TensorBuffer,
        context: Context,
        index: Int
    ): List<classification> {
        val predictedClassesLungs= mutableListOf<classification>()

        Log.d("inputString", "index: $index")

        val outputPneumonia= when(index){
            0 -> { LeukemaSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            1 -> { ColonSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            2 -> { OralSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            4 -> { BreastSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            6 -> { XrayClfV1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            7 -> { XrayClfV1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            8 -> { SkinSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            9 -> { SkinSegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            11 -> { KidneySegmentationv1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
            else -> { XrayClfV1.newInstance(context).process(inputFeature0).outputFeature0AsTensorBuffer }
        }
        val maxIndexPneumonia= getMaxIndex(outputPneumonia.floatArray)
        predictedClassesLungs.add(
            classification(
                indexNumber = maxIndexPneumonia,
                confident = outputPneumonia.floatArray[getMaxIndex(outputPneumonia.floatArray)]*100,
                parentIndex = 0
            ),
        )
        return predictedClassesLungs
    }

    fun getMaxIndex(floatArray: FloatArray): Int {
        return floatArray.withIndex().maxByOrNull { it.value }?.index ?: -1
    }

}
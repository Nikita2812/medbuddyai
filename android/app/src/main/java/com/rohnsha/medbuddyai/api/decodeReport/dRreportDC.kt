import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.rohnsha.medbuddyai.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.ByteArrayOutputStream

interface UploadService {
    @Multipart
    @POST("upload")
    fun uploadImage(@Part image: MultipartBody.Part): Call<UploadResponse>
}

data class UploadResponse(
    val message: String,
    val analysis: String
)

class ApiClient {
    private val retrofit: Retrofit
    private val service: UploadService

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("https://api-jjtysweprq-el.a.run.app/") // Replace with your actual base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()

        service = retrofit.create(UploadService::class.java)
    }

    fun textRecog(context: Context) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        try {
            // Step 3: Load the image from resources
            val drawable = ContextCompat.getDrawable(context, R.drawable.test2)
            val bitmap = drawable?.toBitmap()

            if (bitmap!=null){
                Log.d("UploadResponse", "Image loaded")
                val img= InputImage.fromBitmap(bitmap, 0)
                // Step 5: Process the image and extract text
                recognizer.process(img)
                    .addOnSuccessListener { visionText ->
                        // Task completed successfully
                        val resultText = visionText.text
                        // Use the extracted text as needed
                        if (resultText.isNotEmpty()) {
                            Log.d("UploadResponse", "Extracted text: $resultText")
                            for (block in visionText.textBlocks) {
                                Log.d("UploadResponse", "Block text: ${block.text}")
                                for (line in block.lines) {
                                    Log.d("UploadResponse", "Line text: ${line.text}")
                                    for (element in line.elements) {
                                        Log.d("UploadResponse", "Element text: ${element.text}")
                                    }
                                }
                            }
                        } else {
                            Log.d("UploadResponse", "No text detected.")
                        }
                        println("Extracted text: $resultText")
                        Log.d("UploadResponse", "Extracted text: $resultText")
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        println("Text recognition failed: ${e.message}")
                        Log.d("UploadResponse", "Text recognition failed: ${e.message}")
                    }
                    .addOnCompleteListener {
                        // Log the completion of the task
                        Log.d("UploadResponse", "Text recognition task completed")
                    }
            }


        } catch (e: Exception) {
            Log.e("UploadResponse", "Exception during text recognition: ${e.message}")
        } finally {
            // Clean up resources
            recognizer.close()
        }
    }


    fun uploadImage(context: Context, resourceId: Int) {
        val drawable = ContextCompat.getDrawable(context, resourceId)
        val bitmap = (drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()

        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)

        val call = service.uploadImage(body)
        call.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                if (response.isSuccessful) {
                    val uploadResponse = response.body()
                    Log.d("UploadResponse", "Success: ${uploadResponse?.message}")
                    Log.d("UploadResponse", "Analysis: ${uploadResponse?.analysis}")
                } else {
                    println("Error: ${response.code()}")
                    Log.d("UploadResponse", "Error: ${response.code()}, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                println("Network error: ${t.message}")
                Log.d("UploadResponse", "Network error: ${t.message}")
            }
        })
    }
}

// Usage example (typically in an Activity or ViewModel)
// val apiClient = ApiClient(context)
// apiClient.uploadImage(context, R.drawable.your_image)
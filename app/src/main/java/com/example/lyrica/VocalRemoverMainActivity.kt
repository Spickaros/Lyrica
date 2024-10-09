package com.example.lyrica

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val FileConversion.output: List<FileStem>?
    get() {
        TODO("Not yet implemented")
    }

class VocalRemoverMainActivity : AppCompatActivity() {

    private lateinit var selectFileButton: Button
    private lateinit var uploadFileButton: Button
    private lateinit var backToUploadButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var vocalsTextView: TextView
    private lateinit var instrumentalsTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var linksContainer: LinearLayout
    private lateinit var uploadSection: LinearLayout
    private lateinit var downloadSection: LinearLayout

    private var selectedFile: File? = null
    private val apiKey = "Uha1H9rP29FfUft8Pp4gA8jncgl48yBrYZrghJxIf8f964ac" // Replace with your actual API key
    private val handler = Handler(Looper.getMainLooper())

    private val selectFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val path = FileUtils.getPath(this, it)
            selectedFile = if (path != null) File(path) else null
            statusTextView.text = if (selectedFile != null) "File selected: ${selectedFile?.name}" else "Invalid file"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vocal_remnover_activity_main)

        // Initialize views
        selectFileButton = findViewById(R.id.selectFileButton)
        uploadFileButton = findViewById(R.id.uploadFileButton)
        backToUploadButton = findViewById(R.id.backToUploadButton)
        statusTextView = findViewById(R.id.statusTextView)
        vocalsTextView = findViewById(R.id.text_view_1)
        instrumentalsTextView = findViewById(R.id.text_view_2)
        progressBar = findViewById(R.id.progressBar)
        linksContainer = findViewById(R.id.linksContainer)
        uploadSection = findViewById(R.id.uploadSection)
        downloadSection = findViewById(R.id.downloadSection)

        selectFileButton.setOnClickListener {
            selectFileLauncher.launch("audio/*")
        }

        uploadFileButton.setOnClickListener {
            selectedFile?.let { file ->
                showProgressBar()
                uploadFileForConversion(file)
            } ?: run {
                updateUI("No file selected")
            }
        }

        backToUploadButton.setOnClickListener {
            showUploadSection()
            hideDownloadSection()
            resetUI()
        }
    }

    private fun uploadFileForConversion(file: File) {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val task = "spleeter:2stems".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val sample = "1".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        RetrofitClient.api.createFileConversion("Bearer $apiKey", filePart, task, sample)
            .enqueue(object : Callback<FileConversionResponse> {
                override fun onResponse(call: Call<FileConversionResponse>, response: Response<FileConversionResponse>) {
                    if (response.isSuccessful) {
                        val ulid = response.body()?.file_conversion?.ulid
                        if (ulid != null) {
                            updateUI("Download your stems")
                            hideProgressBar() // Hide ProgressBar when ULID is available
                            checkConversionStatusPeriodically(ulid)

                            val vocalsLink = "https://s3.vocalremover.com/samples/${ulid}.vocals.mp3"
                            val instrumentalsLink = "https://s3.vocalremover.com/samples/${ulid}.instrumentals.mp3"

                            // Set the text for the links
                            vocalsTextView.text = "Vocals"
                            instrumentalsTextView.text = "Instrumental"

                            // Set click listeners to open the links
                            vocalsTextView.setOnClickListener {
                                openLink(vocalsLink)
                            }
                            instrumentalsTextView.setOnClickListener {
                                openLink(instrumentalsLink)
                            }

                            // Make links container visible
                            showLinks()
                            hideUploadSection()
                            showDownloadSection()

                        } else {
                            updateUI("Error: Unable to retrieve ULID.")
                            hideProgressBar() // Hide ProgressBar if ULID retrieval fails
                        }
                    } else {
                        updateUI("Error: ${response.errorBody()?.string()}")
                        hideProgressBar() // Hide ProgressBar if there is an error
                    }
                }

                override fun onFailure(call: Call<FileConversionResponse>, t: Throwable) {
                    updateUI("Failed: ${t.message}")
                    hideProgressBar() // Hide ProgressBar on failure
                }
            })
    }

    private fun checkConversionStatusPeriodically(ulid: String) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                RetrofitClient.api.getFileConversionStatus("Bearer $apiKey", ulid)
                    .enqueue(object : Callback<FileConversionResponse> {
                        override fun onResponse(call: Call<FileConversionResponse>, response: Response<FileConversionResponse>) {
                            if (response.isSuccessful) {
                                val status = response.body()?.file_conversion?.status
                                when (status) {
                                    "completed" -> {
                                        displayStems(response.body()?.file_conversion?.output)
                                        handler.removeCallbacksAndMessages(null)
                                    }
                                    "failed" -> {
                                        updateUI("Conversion failed")
                                        handler.removeCallbacksAndMessages(null)
                                    }
                                    else -> {
                                        updateUI("Status: $status")
                                        handler.postDelayed(this, 1000)
                                    }
                                }
                            } else {
                                updateUI("Error checking status: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<FileConversionResponse>, t: Throwable) {
                            updateUI("Failed to check status: ${t.message}")
                        }
                    })
            }
        }, 1000) // Polling interval
    }

    private fun displayStems(output: List<FileStem>?) {
        if (output != null && output.isNotEmpty()) {
            val builder = StringBuilder("Stems available:\n")
            output.forEach { stem ->
                builder.append("${stem.name}: ${stem.url}\n")
            }

            updateUI(builder.toString())

            // Only show links if stems are available
            showLinks()
        } else {
            updateUI("No stems available")
        }
    }

    private fun openLink(url: String) {
        // Intent to open the URL
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun updateUI(message: String) {
        runOnUiThread {
            statusTextView.text = message
        }
    }

    private fun showProgressBar() {
        runOnUiThread {
            progressBar.visibility = View.VISIBLE
            linksContainer.visibility = View.GONE // Hide links container while progress bar is visible
        }
    }

    private fun hideProgressBar() {
        runOnUiThread {
            progressBar.visibility = View.GONE
        }
    }

    private fun showLinks() {
        runOnUiThread {
            linksContainer.visibility = View.VISIBLE // Show links container when progress bar is hidden
        }
    }

    private fun showDownloadSection() {
        runOnUiThread {
            uploadSection.visibility = View.GONE
            downloadSection.visibility = View.VISIBLE
        }
    }

    private fun hideUploadSection() {
        runOnUiThread {
            uploadSection.visibility = View.GONE
        }
    }

    private fun showUploadSection() {
        runOnUiThread {
            uploadSection.visibility = View.VISIBLE
        }
    }

    private fun hideDownloadSection() {
        runOnUiThread {
            downloadSection.visibility = View.GONE
        }
    }

    private fun resetUI() {
        runOnUiThread {
            statusTextView.text = "" // Clear status text
            progressBar.visibility = View.GONE // Ensure progress bar is hidden
            linksContainer.visibility = View.GONE // Hide links container
        }
    }
}

private fun Handler.postDelayed(callback: Callback<FileConversionResponse>, l: Long) {

}

private fun <T> Call<T>.enqueue(callback: Callback<FileConversionResponse>) {

}

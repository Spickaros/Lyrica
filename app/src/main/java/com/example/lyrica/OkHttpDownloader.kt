// OkHttpDownloader.kt
/*
import okhttp3.OkHttpClient

import okhttp3.Request
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.example.vocalremover.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class OkHttpDownloader(private val context: Context) {

    private val client = OkHttpClient()

    fun downloadStem(url: String, fileName: String) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                (context as? MainActivity)?.runOnUiThread {
                    Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    (context as? MainActivity)?.runOnUiThread {
                        Toast.makeText(context, "Download failed: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
                val inputStream: InputStream = response.body?.byteStream()!!
                val outputStream = FileOutputStream(file)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                (context as? MainActivity)?.runOnUiThread {
                    Toast.makeText(context, "$fileName downloaded to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
*/
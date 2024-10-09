
import com.example.lyrica.FileDuration
import com.example.lyrica.FileStem

data class FileConversionResponse(
    val data: List<FileConversion>
)

data class FileConversion(
    val ulid: String,
    val status: String,
    val output: FileOutput
)

data class FileOutput(
    val vocals: FileStem?,
    val instrumentals: FileStem?,
    val source: FileStem?
)

data class FileStem(
    val name: String,
    val url: String,
    val download_file_name: String
)

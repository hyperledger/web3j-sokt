import kotlinx.serialization.Serializable
import java.io.File
import java.nio.file.Paths

@Serializable
data class SolcRelease(val version: String, val url: String)
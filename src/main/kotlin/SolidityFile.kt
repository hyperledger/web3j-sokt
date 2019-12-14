import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SolidityFile(
    sourceFileString: String
) {
    val sourceFile: Path = Paths.get(sourceFileString)
    val fileContent: String
    val versionPragma: String?

    init {
        if (!sourceFile.toFile().exists()) {
            throw Exception("Unable to find file: $sourceFile")
        }
        fileContent = String(Files.readAllBytes(sourceFile))
        val versionLine: String? =  fileContent.split("\n").firstOrNull { it.toLowerCase().trim().matches(Regex("^pragma.*solidity.*")) }
        versionPragma = versionLine?.substring(versionLine.lastIndexOf("solidity") + 8)?.trim()
    }

    fun getSolcInstance(): SolcInstance {
        return SolcInstance(VersionResolver().getLatestCompatibleVersion(versionPragma).toString())
    }

}
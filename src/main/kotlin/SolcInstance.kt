import com.github.kittinunf.fuel.Fuel
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.zip.ZipFile

class SolcInstance(
    val solcRelease: SolcRelease,
    private vararg val sourceFiles: SolidityFile
) {
    private val solcFile: File =
        Paths.get(
            System.getProperty("user.home"),
            ".web3j",
            "solc",
            solcRelease.version,
            if (SystemUtils.IS_OS_WINDOWS) "solc.exe" else "solc"
        ).toFile()

    private fun installed(): Boolean {
        return solcFile.exists()
    }

    private fun install(): Boolean {
        println("Solidity version ${solcRelease.version} is not installed. Downloading and installing it to ~/.web3j/solc/${solcRelease.version}")
        when {
            SystemUtils.IS_OS_WINDOWS -> {
                val downloadUrl = "${solcRelease.url}${solcRelease.version}/solc_windows.zip"
                solcFile.parentFile.mkdirs()
                val winDownloadFile = File("${solcFile.absolutePath.dropLast(4)}.zip")
                Fuel.download(downloadUrl).destination { _, _ -> winDownloadFile }.response { _, _, _ -> }.join()
                ZipFile(winDownloadFile).use { zip ->
                    zip.entries().asSequence().forEach { entry ->
                        zip.getInputStream(entry).use { input ->
                            Paths.get(solcFile.parentFile.absolutePath, entry.name).toFile().outputStream()
                                .use { output ->
                                    input.copyTo(output)
                                }
                        }
                    }
                }
                winDownloadFile.delete()
                return true;
            }
            SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC -> {
                val downloadUrl =
                    "${solcRelease.url}${solcRelease.version}/solc_${if (SystemUtils.IS_OS_MAC) "mac" else "linux"}"
                solcFile.parentFile.mkdirs()
                Fuel.download(downloadUrl).destination { _, _ -> solcFile }.response { _, _, _ -> }.join()
                if (installed()) {
                    solcFile.setExecutable(true);
                    return true;
                }
            }
        }
        return false;
    }

    fun execute(vararg args: SolcArguments): Int {
        if (!installed() && !install()) {
            println("Failed to install solc version")
        }
        val command =
            "${solcFile.absolutePath} ${args.joinToString(" ") { it.toString() }} ${sourceFiles.joinToString(" ") {
                it.sourceFile.toAbsolutePath().toString()
            }}";
        println(command)
        return command.runCommand()
    }


    private fun String.runCommand(): Int {
        val result = ProcessBuilder(*split(" ").toTypedArray())
            .start()
        return if (result.waitFor(30, TimeUnit.SECONDS)) {
            result.exitValue()
        } else {
            -1
        }
    }
}
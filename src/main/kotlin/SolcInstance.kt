import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.result.Result
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class SolcInstance(
    val solcRelease: SolcRelease,
    private vararg val sourceFiles: SolidityFile
) {
    private val solcFile: File =
        Paths.get(System.getProperty("user.home"), ".web3j", "solc", solcRelease.version, "solc").toFile()

    fun installed(): Boolean {
        return solcFile.exists()
    }

    fun install(): Boolean {

        println("Solidity version ${solcRelease.version} is not installed. Downloading and installing it to ~/.web3j/solc/${solcRelease.version}")
        when {
            SystemUtils.IS_OS_WINDOWS -> {

            }
            SystemUtils.IS_OS_MAC -> {

            }
            SystemUtils.IS_OS_LINUX -> {
                val downloadUrl = "${solcRelease.url}${solcRelease.version}/solc_linux"
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

    fun execute(vararg args: String?) {
        if (!installed() && !install()) {
            println("Failed to install solc version")
        }
        "${solcFile.absolutePath} --bin --abi --optimize --overwrite -o . ${sourceFiles.joinToString(" ") { it.sourceFile.toAbsolutePath().toString() }}".runCommand()
    }


    private fun String.runCommand() {
        ProcessBuilder(*split(" ").toTypedArray())
            .start()
            .waitFor(2, TimeUnit.MINUTES)
    }
}
import java.io.File
import java.nio.file.Paths

class SolcInstance(
    val version: String
) {
    private val solcFile: File = Paths.get(System.getProperty("user.home"), ".web3j", "solc", version, "solc").toFile()

    fun installed(): Boolean {
        return solcFile.exists()
    }

//    fun install() {
//
//    }
//
//    fun runSolc(vararg args: String) {
//
//    }
}
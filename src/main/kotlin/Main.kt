import java.lang.Exception

fun main(vararg params: String) {
    for (filePath in params) {
        try {
            val solidityFile = SolidityFile(filePath)
            println(solidityFile.sourceFile.fileName + solidityFile.versionPragma + solidityFile.getSolcInstance().version)
        } catch (ex: Exception) {
            println("Error: $filePath")
        }
    }
}

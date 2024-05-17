/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.sokt

fun main(vararg params: String) {
    for (filePath in params) {
        val fileName = filePath.substringAfterLast("/")
        println("sokt Processing $fileName")
        val solidityFile = SolidityFile(filePath)

        println("Resolving compiler version for $fileName")
        val compilerInstance = solidityFile.getCompilerInstance()

        println("Resolved ${compilerInstance.solcRelease.version} for $fileName")

        val result = compilerInstance.execute(
            SolcArguments.OUTPUT_DIR.param { "/tmp" },
            SolcArguments.BIN,
            SolcArguments.OVERWRITE,
            SolcArguments.METADATA,
        )

        println("Solc exited with code: ${result.exitCode}")
        println("Solc standard output:\n${result.stdOut}")
        println("Solc standard error:\n${result.stdErr}")

        println("-------------------------")
    }
}

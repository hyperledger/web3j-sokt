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

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class SolidityFile(
    sourceFileString: String,
) {
    val sourceFile: Path = Paths.get(sourceFileString)
    val fileContent: String
    val versionPragma: String?

    init {
        if (!sourceFile.toFile().exists()) {
            throw Exception("Unable to find file: $sourceFile")
        }
        fileContent = String(Files.readAllBytes(sourceFile))
        val versionLine: String? =
            fileContent.split("\n").firstOrNull { it.lowercase().trim().matches(Regex("^pragma.*solidity.*")) }
        versionPragma = versionLine?.substring(versionLine.lastIndexOf("solidity") + 8)?.trim()
    }

    fun getCompilerInstance(directoryPath: String = ".web3j", redirectOutput: Boolean = false): SolcInstance {
        val release = VersionResolver(directoryPath).getLatestCompatibleVersion(versionPragma)
        if (release != null) {
            return SolcInstance(release, directoryPath, redirectOutput, this)
        }
        throw Exception("No compatible solc release could be found for the file: $sourceFile")
    }
}

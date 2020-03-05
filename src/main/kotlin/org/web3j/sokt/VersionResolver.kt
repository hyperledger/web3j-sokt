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

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.zafarkhaja.semver.Version
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Paths
import javax.net.ssl.HttpsURLConnection


class VersionResolver(private val directoryPath: String = ".web3j") {

    private val ver = Token("ver", "(\\d|\\.)+\\d*")
    private val hat = Token(null, "\\^")
    private val til = Token(null, "~")
    private val eq = Token(null, "=")
    private val lt = Token(null, ">")
    private val gt = Token(null, "<")
    private val ng = Token(null, "!")
    private val ignored = Token(null, "\\s+|;|v|(//.*)", true)

    private val tokenizer = DefaultTokenizer(listOf(ver, hat, til, eq, lt, gt, ng, ignored))

    operator fun get(uri: String): String {
        val con = URL(uri).openConnection() as HttpsURLConnection
        con.requestMethod = "GET"
        con.setRequestProperty("Content-Type", "application/json")
        con.setRequestProperty("Accept", "application/json")
        con.doOutput = true
        val reader = BufferedReader(
            InputStreamReader(con.inputStream)
        )
        var inputLine: String?
        val response = StringBuffer()
        while (reader.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        reader.close()
        return response.toString()
    }

    fun getSolcReleases(): List<SolcRelease> {
        val versionsFile = Paths.get(System.getProperty("user.home"), directoryPath, "solc", "releases.json").toFile()
        try {
            val result = get("https://internal.services.web3labs.com/api/solidity/versions/")
            versionsFile.parentFile.mkdirs()
            versionsFile.writeText(result)
            return Json(JsonConfiguration.Stable).parse(SolcRelease.serializer().list, result)
        } catch (e: Exception) {
            if (versionsFile.exists()) {
                return Json(JsonConfiguration.Stable).parse(SolcRelease.serializer().list, versionsFile.readText())
            }
            throw Exception("Failed to get solidity version from server")
        }
    }

    fun versionsFromString(input: String): List<String> {
        return tokenizer.tokenize(input).filter { !it.type.ignored }.fold(mutableListOf(mutableListOf<TokenMatch>()),
            { all, ele ->
                if (all.last().isNotEmpty() && all.last().last().type.name == "ver") {
                    all.add(mutableListOf(ele))
                } else {
                    all.last().add(ele)
                }
                all
            }).map { it.joinToString("") { nr -> nr.text } }
    }

    fun getCompatibleVersions(pragmaRequirement: String, releases: List<SolcRelease>): List<SolcRelease> {
        val requiredVersions = versionsFromString(pragmaRequirement)
        return releases.filter {
            requiredVersions.all(fun(nr: String): Boolean {
                return Version.valueOf(it.version).satisfies(nr) && it.isCompatibleWithOs()
            })
        }
    }

    fun getLatestCompatibleVersion(pragmaRequirement: String?): SolcRelease? {
        val solcReleases = getSolcReleases()
        return if (pragmaRequirement != null) {
            getCompatibleVersions(pragmaRequirement, solcReleases).lastOrNull()
        } else {
            solcReleases.last()
        }
    }
}
